package com.example.igro;

import android.content.Intent;
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


import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String MAIN_LOG_TAG = "MAIN_LOG_TAG";

    private Button temperature;
    private Button uv;
    private Button uvNumber;
    private Button number;
    private Button celcius;
    private Button fahrenheit;
    private boolean celcius_pressed=true;
    private boolean fahrenheit_pressed=false;

    private Button moistureNumber;
    private Button moistureButton;

    private Button tempNumberButton;
    private Button logout;
    private String UserN;
    private String UserP;

    protected Button humidityTitle;
    protected TextView humNumber;

    public int tempD;

    private FirebaseAuth mAuth; // authentication instance
    private TextView userWelcomeMessage;
    private FirebaseUser currentUser;

    private RequestQueue queue;
    private TextView cityWeatherMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        checkAuthentication();
        //logout =  (Button) findViewById(R.id.logout);
        temperature=(Button) findViewById(R.id.temp_button);
        number=(Button) findViewById(R.id.tempNumberView);

        getTempData("1");
        number=(Button) findViewById(R.id.tempNumberView);
        celcius=(Button) findViewById(R.id.celciusOutButton);
        fahrenheit=(Button) findViewById(R.id.fahrenheitOutButton);
        uv=(Button) findViewById(R.id.uvButton);
        uvNumber=(Button)findViewById(R.id.uvNumberButton);

        humidityTitle = (Button)findViewById(R.id.humidityButton);
        humNumber = (TextView) findViewById(R.id.humidityPercentView);
        moistureButton = (Button) findViewById(R.id.moistureButton);
        moistureNumber = (Button) findViewById(R.id.moisturePercentView);

        userWelcomeMessage = findViewById(R.id.welcomeMessageText);
        String welcomeMessage = currentUser != null ? "Hi " + currentUser.getEmail() : "";
        userWelcomeMessage.setText(welcomeMessage);
        cityWeatherMessage = findViewById(R.id.cityWeatherTextView);
        queue = Volley.newRequestQueue(this);
        requestWeather();

        getHumData("1");

        //from fahrenheit to celcius
        celcius.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(fahrenheit_pressed) {
                    for(int i=0;i<1;i++) {
                        Double degrees = Double.parseDouble(number.getText().toString());
                         Double a = (degrees - 32) * 5 / 9;
                        number.setText(Double.toString(a));
                    }
                    celcius_pressed = true;
                    fahrenheit_pressed=false;
                }
            }
        });
        // from celcius to fahrenheit
        fahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(celcius_pressed) {
                    for(int i=0;i<1;i++) {
                        Double degrees = Double.parseDouble(number.getText().toString());
                        Double a = degrees * 9 / 5 + 32;
                        number.setText(Double.toString(a));
                    }
                    fahrenheit_pressed = true;
                    celcius_pressed=false;
                }
            }
        });

        //opening the Temperature view when the temperature text is clicked
        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperature();
            }
        });
        //opening the Uv index view  when the uv text is clicked
        uv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUv();
            }
        });
        //opening the Temperature view when the temperature number is clicked
        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperature();
            }
        });
        ////opening the uv view when the uv number is clicked
        uvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUv();
            }
        });
        humidityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHumidity();
            }
        });
        humNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHumidity();
            }
        });
        moistureButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick (View v2){
                openMoistureActivity();
            }
        });
        moistureNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoistureActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAuthentication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthentication();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkAuthentication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out:
                Intent f = new Intent(MainActivity.this, Dashboard.class);
                startActivity(f);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

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
                number.setText(tempD+"");
            }
        });
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
                humNumber.setText(humD+"");
            }
        });
    }

    public void openTemperature(){
        Intent tempIntent=new Intent(this,TemperatureActivity.class);
        startActivity(tempIntent);
    }
    public void openUv(){
        Intent intent=new Intent(this,UvIndexActivity.class);
        startActivity(intent);
    }

    public void openHumidity(){
        Intent humIntent=new Intent(this,HumidityActivity.class);
        startActivity(humIntent);
    }

    public void openMoistureActivity(){
        Intent intent2 = new Intent(this,MoistureActivity.class);
        startActivity(intent2);
    }

    void goToDashboardActivity() {
        Intent i = new Intent(MainActivity.this, Dashboard.class);
        startActivity(i);
    }

    private void checkAuthentication() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // current user validated
        } else {
            goToDashboardActivity();
        }
    }

    void requestWeather() {
        // TODO: 2019-03-18
        // Make this function capable of pulling data for any city as per user request
        // Get weather for Montreal
        final String url = "http://api.openweathermap.org/data/2.5/weather?q=Montreal&units=metric&APPID=9208dccec4431655d17d8dfa3d4fabc7";

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
                            cityWeatherMessage.setText("Montreal" + temperature + "°");

                        } catch (Exception e) {
                            Log.w(MAIN_LOG_TAG, "THIS SHIT AINT WORKING");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Called when there is an error making the request
                    }
                });
        queue.add(weatherRequest);
    }
}
