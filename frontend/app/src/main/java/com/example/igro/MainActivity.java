package com.example.igro;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;


import com.example.igro.Controller.Helper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.igro.Models.SensorData.SensorData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_LOG_TAG = "MAIN_ACTIVITY_LOG_TAG";

    private Button temperatureTitleButton;
    private Button temperatureNumberButton;
    private Button temperatureCelsiusButton;
    private Button temperatureFahrenheitButton;

    private Button uvTitleButton;
    private Button uvNumberButton;

    private boolean celcius_pressed = true;
    private boolean fahrenheit_pressed = false;

    private Button moistureNumberButton;
    private Button moistureTitleButton;

    protected Button humidityTitleButton;
    protected TextView humidityNumberButton;

    private Helper helper = new Helper(this, FirebaseAuth.getInstance());

    public int tempD;

    private TextView userWelcomeMessage;
    private FirebaseUser currentUser;

    private RequestQueue queue;
    private TextView cityWeatherMessage;
    private List<SensorData> sensorDataList = new ArrayList<>();

    /**
     * On create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = helper.checkAuthentication();

        //Initialize all the UI elements
        initializeUI();

        userWelcomeMessage = findViewById(R.id.welcomeMessageText);
        String welcomeMessage = currentUser != null ? "Hi " + currentUser.getEmail() : "";
        userWelcomeMessage.setText(welcomeMessage);
        cityWeatherMessage = findViewById(R.id.cityWeatherTextView);
        queue = Volley.newRequestQueue(this);
        requestWeather();

        getHumData("1");
//        getTempData("1");
        retrieveTemp();

        temperatureNumberButton.setText(sensorDataList.size()+"");

        //Temperature Celsius Button listener
        temperatureCelsiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fahrenheit_pressed) {
                    for(int i=0;i<1;i++) {
                        Double degrees = Double.parseDouble(temperatureNumberButton.getText().toString());
                        Double a = (degrees - 32) * 5 / 9;
                        temperatureNumberButton.setText(Double.toString(a));
                    }
                    celcius_pressed = true;
                    fahrenheit_pressed = false;
                }
            }
        });

        // convert temperatureCelsiusButton to temperatureFahrenheitButton
        temperatureFahrenheitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(celcius_pressed) {
                    for(int i=0;i<1;i++) {
                        Double degrees = Double.parseDouble(temperatureNumberButton.getText().toString());
                        Double a = degrees * 9 / 5 + 32;
                        temperatureNumberButton.setText(Double.toString(a));
                    }
                    fahrenheit_pressed = true;
                    celcius_pressed=false;
                }
            }
        });

        //opening the Temperature view when the temperature text is clicked
        temperatureTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(TemperatureActivity.class);
            }
        });

        //opening the Temperature view when the temperature number is clicked
        temperatureNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(TemperatureActivity.class);
            }
        });

        //opening the Uv index view  when the uvTitleButton text is clicked
        uvTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(UvIndexActivity.class);
            }
        });

        ////opening the uvTitleButton view when the uvTitleButton number is clicked
        uvNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(UvIndexActivity.class);
            }
        });
        humidityTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(HumidityActivity.class);
            }
        });
        humidityNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(HumidityActivity.class);
            }
        });
        moistureTitleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v2){
                helper.goToActivity(MoistureActivity.class);
            }
        });
        moistureNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(MoistureActivity.class);
            }
        });
    }

    protected void initializeUI(){
        //Temperature View initialization
        temperatureTitleButton = (Button) findViewById(R.id.temp_button);
        temperatureNumberButton = (Button) findViewById(R.id.tempNumberView);
        temperatureCelsiusButton = (Button) findViewById(R.id.celciusOutButton);
        temperatureFahrenheitButton = (Button) findViewById(R.id.fahrenheitOutButton);

        //UV view initialization
        uvTitleButton = (Button) findViewById(R.id.uvButton);
        uvNumberButton = (Button)findViewById(R.id.uvNumberButton);

        //Humidity view initialization
        humidityTitleButton = (Button)findViewById(R.id.humidityButton);
        humidityNumberButton = (TextView) findViewById(R.id.humidityPercentView);

        //Moisture view initialization
        moistureTitleButton = (Button) findViewById(R.id.moistureButton);
        moistureNumberButton = (Button) findViewById(R.id.moisturePercentView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        helper.checkAuthentication();
        requestWeather();
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.checkAuthentication();
        requestWeather();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        helper.checkAuthentication();
        requestWeather();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out:
                helper.signout();
                helper.goToActivity(LoginActivity.class);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    //Todo have to change this to real time database format
    //Get document from firestore
    public void getTempData(String id){
        //Reference to collection in firestore
        CollectionReference tempRef = FirebaseFirestore.getInstance().collection("temperature");

        tempRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        tempD = Integer.parseInt(document.getString("temp"));

                        Log.d("WORKING", ""+tempD);
                    }
                    else{
                        Log.d("ERROR", "Cannot get Temperature");
                    }
                }
                temperatureNumberButton.setText(tempD+"");
            }
        });
    }

    void retrieveTemp(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    Log.d("FIREBASE", sensorData.getTime()+"");
                    temperatureNumberButton.setText(sensorData.getTemperatureC()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);

    }


    public int humD;

    //Get humidity document from firestore
    public void getHumData(String id){
        //Reference to humidity collection in firestore
        CollectionReference humRef = FirebaseFirestore.getInstance().collection("humidity");
        humRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        humD = Integer.parseInt(document.getString("humValue"));

                        Log.d("SUCCESS", ""+humD);
                    }
                    else{
                        Log.d("ERROR", "Cannot Retreave Humidity");
                    }
                }
                humidityNumberButton.setText(humD+"");
            }
        });
    }

    void requestWeather() {
        // TODO: 2019-03-18
        // Make this function capable of pulling data for any city as per user request

        // Get weather for Montreal
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Montreal&units=metric&APPID=b4840319c97c4629912dc391ed164bcb";
        // Make request
        JsonObjectRequest weatherRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(MAIN_LOG_TAG, "response: " + response);
                        try {
                            // Get description from weather response
                            //String description = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            //descriptionTextView.setText(description);

                            // Get temperature from weather response
                            int temperature = response.getJSONObject("main").getInt("temp");
                            cityWeatherMessage.setText("Montreal " + temperature + "°C");

                        } catch (Exception e) {
                            Log.w(MAIN_LOG_TAG, "Attempt to parse JSON Object failed");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MAIN_LOG_TAG, "JSON Request has failed");
                    }
                });
        queue.add(weatherRequest);
    }
}
