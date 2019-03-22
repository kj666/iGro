package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.igro.Controller.Helper;
import com.example.igro.Models.ActuatorControl.HeaterControlEvents;
import com.example.igro.Models.SensorData.TempRange;
import com.example.igro.Models.SensorData.SensorData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

// TODO 2019-03-20
// change the tempswitch to something more appropriate
// check how to switch between Fahrenheit and Celsius on pre-existing temperature data

public class TemperatureActivity extends AppCompatActivity {
    private static final String TEMPERATURE_LOG_TAG = "TEMP_ACTIVITY_LOG_TAG";
    Button celsiusFahrenheitSwitchButton;
    boolean celisusOrFahrenheit = true; // default is celsius

    //initialize the layout fields
    Button tempHistoryButton;
    double tempDegree;

    Button heaterUseHistoryButton;
    Button setRangeTempButton;
    EditText lowTempEditText;
    EditText highTempEditText;
    TextView tempControlTextView;
    Switch tempSwitch; // Confusing name, its heater control switch but it looks like its temperature switch

    TextView outdoorTemperatureTextView;
    TextView greenhouseTemperatureTextView;
    private RequestQueue queue;

    // create database reference for ranges
    DatabaseReference databaseRange = FirebaseDatabase.getInstance().getReference().child("Ranges");
    private FirebaseUser currentUser;
    public Boolean lastHeaterState = false;
    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "HeaterIsOnTag";
    //create heater database reference
    DatabaseReference heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("HeaterControlLog");
    //Get current user using the Helper class
    private Helper helper = new Helper(this, FirebaseAuth.getInstance());


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        initializeUI();

        tempSwitch.setClickable(true);

        currentUser = helper.checkAuthentication();
        retrieveSensorData();

        setRangeTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTempRange();
            }
        });
        outdoorTemperatureTextView = findViewById(R.id.outdoorTempTextView);
        greenhouseTemperatureTextView = findViewById(R.id.ghTempTextView);

        Double fixedGreenhouseValue = 20.0; // Switch this to sensor data when available
        greenhouseTemperatureTextView.setText(fixedGreenhouseValue.toString());
        queue = Volley.newRequestQueue(this);
        requestWeather();

        celsiusFahrenheitSwitchButton = findViewById(R.id.celsiusFahrenheitSwitchButton);
        celsiusFahrenheitSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celsiusFahrenheitSwitch();
            }
        });

        currentUser = helper.checkAuthentication();
        retrieveRange();

    }

    void retrieveRange(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Ranges");
        DatabaseReference tempRange = db.child("Temperature");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                lowTempEditText.setText(dataSnapshot.child("lowTempValue").getValue().toString());
                Double lowRange = Double.parseDouble(dataSnapshot.child("lowTempValue").getValue().toString());

                highTempEditText.setText(dataSnapshot.child("highTempValue").getValue().toString());
                Double highRange = Double.parseDouble(dataSnapshot.child("highTempValue").getValue().toString());
                if (!((tempDegree > lowRange)
                        && (tempDegree < highRange))) {

                    greenhouseTemperatureTextView.setTextColor(Color.RED);
                    Toast.makeText(TemperatureActivity.this,"THE SENSOR VALUE IS OUT OF THRESHOLD!!!", Toast.LENGTH_LONG).show();
                }
                else{
                    greenhouseTemperatureTextView.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        tempRange.addValueEventListener(eventListener);

    }

    void initializeUI(){
        tempHistoryButton = (Button)findViewById(R.id.tempHistoriyButton);
        heaterUseHistoryButton = (Button)findViewById(R.id.heaterUseHistoryButton);
        tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
        tempSwitch = (Switch)findViewById(R.id.tempSwitch);

        greenhouseTemperatureTextView=(TextView)findViewById(R.id.ghTempTextView);
        //Get the values from the user
        lowTempEditText = (EditText)findViewById(R.id.lowTempEditText);
        highTempEditText = (EditText)findViewById(R.id.highTempEditText);

        outdoorTemperatureTextView = findViewById(R.id.outdoorTempTextView);
        greenhouseTemperatureTextView = findViewById(R.id.ghTempTextView);

        queue = Volley.newRequestQueue(this);
        requestWeather();

        celsiusFahrenheitSwitchButton = findViewById(R.id.celsiusFahrenheitSwitchButton);
        celsiusFahrenheitSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celsiusFahrenheitSwitch();
            }
        });

        currentUser = helper.checkAuthentication();
        setRangeTempButton=(Button)findViewById(R.id.setRangeTempButton);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

  /*  //Create option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();

        initializeUI();

        String lowTempLimit = lowTempEditText.getText().toString();
        String highTempLimit = lowTempEditText.getText().toString();

        if((lowTempLimit.matches(".*[0-9].*"))&&(highTempLimit.matches(".*[0-9].*"))){
            Integer lowTemp = Integer.parseInt(lowTempLimit);
            Integer highTemp = Integer.parseInt(highTempLimit);
        }else{
          Toast.makeText(this, "Please enter a valid number for lower and upper temperature limits", Toast.LENGTH_LONG).show();
        }

        //opening the HistoricalApplianceActivity view when the HeaterUseHistory button is clicked
        heaterUseHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = TemperatureActivity.this ;
                Intent i = new Intent(context, HistoricalApplianceActivity.class);
                i.putExtra("ApplianceType", "HEATER");
                context.startActivity(i);
            }
        });


        //opening the SensorDataActivity on history sensor data button click
        tempHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = TemperatureActivity.this ;
                Intent i = new Intent(context, SensorDataActivity.class);
                i.putExtra("SensorType", "TEMPERATURE");
                context.startActivity(i);

            }
        });


        //call function check last child in heaterSwitchEventDB and set switch to that state
        final boolean switchState = tempSwitch.isChecked();
        heaterSwitchStateFromRecord();


        if(lastHeaterState){

            Log.d(TAG, "The heater was on");
        }else{

            Log.d(TAG, "The heater was on");
        }

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean SwitchState){


                //Call heaterSwitchEvent function
                heaterSwitchEvent(SwitchState);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        final boolean switchState = tempSwitch.isChecked();
            heaterSwitchStateFromRecord();
            
        if(switchState){
            Log.d(TAG, "The heater was on");
        }else{
            Log.d(TAG, "The heater was off");
        }

        //Listen for changes in switch status
        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean tempSwitchState){

                //Call heaterSwitchEvent function
                heaterSwitchEvent(tempSwitchState);
            }
        });
    }


    private void heaterSwitchStateFromRecord() {

        heaterSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HeaterControlEvents lastRecord = dataSnapshot.getValue(HeaterControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getHeaterEventOnOff();

                if(!(checkedStatus == null)){

                    lastHeaterState = checkedStatus;
                    tempSwitch.setChecked(checkedStatus);
                    if(checkedStatus){
                        tempSwitch.setTextColor(Color.RED);
                    }else{
                        tempSwitch.setTextColor(Color.DKGRAY);
                    }

                }else{

                    Log.d(TAG, "On/Off Status of heater can't be null, getHeaterEventOnOff points to null");
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void heaterSwitchEvent(boolean tempSwitchState) {

            //record the time of the click
            //DateFormat heatOnDateTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

            long heatOnOffDateUnixFormat = System.currentTimeMillis()/1000;

            String heatOnOffDateReadable = new java.text.SimpleDateFormat("MM/dd/yy HH:mm:ss").format(new java.util.Date(heatOnOffDateUnixFormat*1000));

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String heatOnTimeStampFormated = df.format(Calendar.getInstance().getTime());

            if(!(tempSwitchState==lastHeaterState)){

                //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
                String heatEventId = heaterSwitchEventDB.push().getKey();

                HeaterControlEvents heatSwitchClickEvent = new HeaterControlEvents(heatEventId, heatOnTimeStampFormated, heatOnOffDateUnixFormat, tempSwitchState);
                heaterSwitchEventDB.child(heatEventId).setValue(heatSwitchClickEvent);

                if(!(heatEventId == null)) {

                    if (tempSwitchState) {

                        Log.d(TAG, "The heater was turned on " + heatOnTimeStampFormated);
                        Toast.makeText(this, "The heater was switched ON on " + heatOnTimeStampFormated, Toast.LENGTH_LONG).show();

                    } else {
                        Log.d(TAG, "The heater was turned off on " + heatOnTimeStampFormated);
                        Toast.makeText(this, "The heater was switched OFF on " + heatOnTimeStampFormated, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Log.d(TAG, "ERROR: heatEventId can't be null");

                }


            }

        }

    void retrieveSensorData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SensorData sensorData = snap.getValue(SensorData.class);
                    DecimalFormat df = new DecimalFormat("####0.00");
                    //Temperature
                    greenhouseTemperatureTextView.setText(df.format(sensorData.getTemperatureC()) + "");
                    tempDegree = Double.parseDouble(greenhouseTemperatureTextView.getText().toString());

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
                        Log.i(TEMPERATURE_LOG_TAG, "response: " + response);
                        try {
                            // Get description from weather response
                            //String description = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            //descriptionTextView.setText(description);

                            // Get temperature from weather response
                            Integer temperature = response.getJSONObject("main").getInt("temp");
                            outdoorTemperatureTextView.setText(temperature.toString());

                        } catch (Exception e) {
                            Log.w(TEMPERATURE_LOG_TAG, "Attempt to parse JSON Object failed");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TEMPERATURE_LOG_TAG, "JSON Request has failed");
                    }
                });
        queue.add(weatherRequest);
    }


    public void setTempRange(){

        String lowTemp=lowTempEditText.getText().toString();
        String highTemp=highTempEditText.getText().toString();
        //check if the ranges are empty or not
        if (!TextUtils.isEmpty(lowTemp) && !TextUtils.isEmpty(highTemp)) {
            if (Integer.parseInt(lowTemp.toString()) < Integer.parseInt(highTemp.toString())) {

                TempRange temperatureTempRange = new TempRange(lowTemp, highTemp);
                databaseRange.child("Temperature").setValue(temperatureTempRange);
                Toast.makeText(this, "RANGE SUCCESSFULLY SET!!!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "HIGH VALUES SHOULD BE GREATER THAN LOW VALUES!!!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "YOU SHOULD ENTER LOW AND HIGH VALUES!!!", Toast.LENGTH_LONG).show();
        }

    }


    /*
    * Function that will convert all necessary parameters between celsius and fahrenheit
     */
    void celsiusFahrenheitSwitch(){
        outdoorTemperatureTextView.setText(
                celsiusFahrenheitConversion(outdoorTemperatureTextView.getText().toString()));
        greenhouseTemperatureTextView.setText(
                celsiusFahrenheitConversion(greenhouseTemperatureTextView.getText().toString()));
        celisusOrFahrenheit = !celisusOrFahrenheit;
    }

    /*
    * Function that handles the mathematical aspect of the celsius <-> fahrenheit process
     */
    String celsiusFahrenheitConversion(String valueToBeConverted) {
        Double numberToBeConverted = Double.parseDouble(valueToBeConverted);
        if (celisusOrFahrenheit) { // number currently in celsius
            numberToBeConverted = (9.0/5.0) * numberToBeConverted + 32.0;
            numberToBeConverted = Math.round(numberToBeConverted * 100.0) / 100.0;
            return numberToBeConverted.toString();
        } else { //number currently in fahrenheit
            numberToBeConverted = (5.0/9.0) * (numberToBeConverted - 32.0);
            numberToBeConverted = Math.round(numberToBeConverted * 100.0) / 100.0;
            return numberToBeConverted.toString();
        }
    }
}

