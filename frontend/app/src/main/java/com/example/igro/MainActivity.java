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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_LOG_TAG = "MAIN_ACTIVITY_LOG_TAG";

    //Temperature layout item
    private Button temperatureTitleButton;
    private Button temperatureNumberButton;
    Button celsiusFahrenheitSwitchButton;
    boolean celsiusOrFahrenheit = true; // default is celsius

    private Button uvTitleButton;
    private Button uvNumberButton;

    private Button moistureNumberButton;
    private Button moistureTitleButton;

    protected Button humidityTitleButton;
    protected TextView humidityNumberButton;

    private Helper helper = new Helper(this, FirebaseAuth.getInstance());

    protected TextView userWelcomeMessage;

    private FirebaseUser currentUser;

    DecimalFormat df = new DecimalFormat("####0.00");
    private RequestQueue queue;
    private TextView cityWeatherMessage;

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


        //Retrieve data from DB
        retrieveSensorData();

        celsiusFahrenheitSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celsiusFahrenheitSwitch();
            }
        });

            //opening the Temperature view when the temperature text is clicked
        temperatureTitleButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                helper.goToActivity(TemperatureActivity.class);
            }
            });

            //opening the Temperature view when the temperature number is clicked
        temperatureNumberButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                helper.goToActivity(TemperatureActivity.class);
            }
            });

            //opening the Uv index view  when the uvTitleButton text is clicked
        uvTitleButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                helper.goToActivity(UvIndexActivity.class);
            }
            });

            //opening the uvTitleButton view when the uvTitleButton number is clicked
        uvNumberButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                helper.goToActivity(UvIndexActivity.class);
            }
            });
        humidityTitleButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                helper.goToActivity(HumidityActivity.class);
            }
            });
        humidityNumberButton.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View v){
                helper.goToActivity(HumidityActivity.class);
            }
            });
        moistureTitleButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v2){
                    helper.goToActivity(MoistureActivity.class);
            }

            });
        moistureNumberButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick (View v){
                helper.goToActivity(MoistureActivity.class);
            }
            });
        }

    // TODO 2019-03-21
    // Switch the fixed value given for temperature below to sensor data when available
    protected void initializeUI(){
        //Temperature View initialization
        temperatureTitleButton = (Button) findViewById(R.id.temp_button);
        temperatureNumberButton = (Button) findViewById(R.id.tempNumberView);
        celsiusFahrenheitSwitchButton = findViewById(R.id.celsiusFahrenheitSwitchButton);

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

                case R.id.polling_menu:
                openDialog();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    //Retrieve data from the database and store it as Sensor Data
    void retrieveSensorData(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);

                    //Temperature
                    temperatureNumberButton.setText(df.format(sensorData.getTemperatureC())+"");

                    //Humidity
                    humidityNumberButton.setText(df.format(sensorData.getHumidity())+"");

                    //UVIndex
                    uvNumberButton.setText(new DecimalFormat("####0.0").format(sensorData.getUv())+"");

                    //SoilMoisture
                    moistureNumberButton.setText(new DecimalFormat("####0.0").format(sensorData.getSoil())+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);
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

    /*
     * Function that will convert all necessary parameters between celsius and fahrenheit
     */
    void celsiusFahrenheitSwitch(){
        temperatureNumberButton.setText(
                celsiusFahrenheitConversion(temperatureNumberButton.getText().toString()));
        celsiusOrFahrenheit = !celsiusOrFahrenheit;
    }

    /*
     * Function that handles the mathematical aspect of the celsius <-> fahrenheit process
     */
    String celsiusFahrenheitConversion(String valueToBeConverted) {
        Double numberToBeConverted = Double.parseDouble(valueToBeConverted);
        if (celsiusOrFahrenheit) { // number currently in celsius
            numberToBeConverted = (9.0/5.0) * numberToBeConverted + 32.0;
            numberToBeConverted = Math.round(numberToBeConverted * 100.0) / 100.0;
            return numberToBeConverted.toString();
        } else { //number currently in fahrenheit
            numberToBeConverted = (5.0/9.0) * (numberToBeConverted - 32.0);
            numberToBeConverted = Math.round(numberToBeConverted * 100.0) / 100.0;
            return numberToBeConverted.toString();
        }
    }
    public void openDialog(){
        PollingFrequencyDialogFragment dialog = new PollingFrequencyDialogFragment();
        dialog.show(getSupportFragmentManager(), "Polling dialog");
    }

}
