package com.example.igro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
import com.example.igro.Models.ActuatorControl.ApplianceControlEvents;
import com.example.igro.Models.SensorData.SensorDataValue;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    TextView tempLastUpdatedTextView;
    Button heaterUseHistoryButton;
    Button setRangeTempButton;
    EditText lowTempEditText;
    EditText highTempEditText;
    TextView tempControlTextView;
    Switch temperatureSwitch; // Confusing name, its heater control switch but it looks like its temperature switch

    TextView outdoorTemperatureTextView;
    TextView greenhouseTemperatureTextView;
    private RequestQueue queue;
    private Helper helper = new Helper(this, FirebaseAuth.getInstance());
    String greenhouseID;

    // create database reference for ranges

    private List<SensorDataValue> tempDataList = new ArrayList<>();
    private FirebaseUser currentUser;
    String currentUserID;
    String currentUserName;
    String currentUserEmail;
    private FirebaseUser currentuser;
    String currentuserID;
    String currentuserName;
    String currentuserEmail;

    //get last heater states from record
    public Long previousHeaterTriggerTime;
    public Boolean lastHeaterState = false;
    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "HeaterIsOnTag";
    //create heater database reference
    DatabaseReference heaterSwitchEventDB, appliances, databaseRange, db, userDB, generalDB;

    int lastpollfrequencyInt;
    long LastUnixTime;
    private final String Channel_ID = "channel1";
    private NotificationManagerCompat notificationManager;



    public void initializeDB(String greenhouseID){
        heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/ApplianceControlLog").child("HeaterControlLog");
        appliances = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Appliances");
        databaseRange = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Ranges");
        db = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Data");
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        generalDB = FirebaseDatabase.getInstance().getReference().child(greenhouseID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        helper.setSharedPreferences(getApplicationContext());
        greenhouseID = helper.retrieveGreenhouseID();
        celisusOrFahrenheit = helper.retrieveTemperatureMetric();

        initializeDB(greenhouseID);
        initializeUI();
        // make temperature control switch clickable
        temperatureSwitch.setClickable(true);

        currentUser = helper.checkAuthentication();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        currentUserID = currentUser.getUid();
        currentUserName = currentUser.getDisplayName();
        currentUserEmail = currentUser.getEmail();

        currentuserID = currentuser.getUid();
        currentuserName = currentuser.getDisplayName();
        currentUserEmail = currentUser.getEmail();

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

        /*
        celsiusFahrenheitSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                celsiusFahrenheitSwitch();
            }
        });
        */
        retrieveRange();

        notificationManager = NotificationManagerCompat.from(this);
        checkSensorInactivity();
        createChannel();
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
            case R.id.settings:
                helper.goToActivity(SettingsActivity.class);
                return true;
            case R.id.about:
                helper.goToActivity(AboutActivity.class);
                return true;
            case R.id.sign_out:
                helper.signout();
                helper.goToActivity(LoginActivity.class);
                return true;
            case R.id.polling_menu:
                openDialog();
                return true;
            case R.id.changePassword:
                changePasswordDialog();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializeUI();

        //opening the HistoricalApplianceActivity view when the HeaterUseHistory button is clicked
        heaterUseHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = TemperatureActivity.this ;
                Intent i = new Intent(context, HistoricalApplianceActivity.class);
         //sends extra with the intent to distinguish from which activity intect came and which records to display
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
                if (helper.retrieveTemperatureMetric()) { //Celsius
                    i.putExtra("SensorType", "TEMPERATURE-C");
                } else { //Fahrenheit
                    i.putExtra("SensorType", "TEMPERATURE-F");
                }
                context.startActivity(i);
            }
        });


        //call function check last child in heaterSwitchEventDB and set switch to that state
        final boolean switchState = temperatureSwitch.isChecked();
        heaterSwitchStateFromRecord();


        temperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean SwitchState){


                //Call heaterSwitchEvent function
                heaterSwitchEvent(SwitchState);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        final boolean switchState = temperatureSwitch.isChecked();
            heaterSwitchStateFromRecord();

        //Listen for changes in switch status
        temperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean tempSwitchState){

                //Call heaterSwitchEvent function
                heaterSwitchEvent(tempSwitchState);
            }
        });

        //opening the SensorDataActivity on history sensor data button click
        tempHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = TemperatureActivity.this ;
                Intent i = new Intent(context, SensorDataActivity.class);
                if (helper.retrieveTemperatureMetric()) { //Celsius
                    i.putExtra("SensorType", "TEMPERATURE-C");
                } else { //Fahrenheit
                    i.putExtra("SensorType", "TEMPERATURE-F");
                }
                context.startActivity(i);
            }
        });

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

        celsiusFahrenheitSwitchButton = findViewById(R.id.celsiusFahrenheitSwitchButton);
        if (helper.retrieveTemperatureMetric()) {
            celsiusFahrenheitSwitchButton.setText("°C" );
        } else {
            celsiusFahrenheitSwitchButton.setText("°F" );
        }
        requestWeather();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        final boolean switchState = temperatureSwitch.isChecked();
        heaterSwitchStateFromRecord();

        //Listen for changes in switch status
        temperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean tempSwitchState){

                //Call heaterSwitchEvent function
                heaterSwitchEvent(tempSwitchState);
            }
        });

        //opening the SensorDataActivity on history sensor data button click
        tempHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = TemperatureActivity.this ;
                Intent i = new Intent(context, SensorDataActivity.class);
                if (helper.retrieveTemperatureMetric()) { //Celsius
                    i.putExtra("SensorType", "TEMPERATURE-C");
                } else { //Fahrenheit
                    i.putExtra("SensorType", "TEMPERATURE-F");
                }
                context.startActivity(i);
            }
        });

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

        celsiusFahrenheitSwitchButton = findViewById(R.id.celsiusFahrenheitSwitchButton);
        if (helper.retrieveTemperatureMetric()) {
            celsiusFahrenheitSwitchButton.setText("°C" );

        } else {
            celsiusFahrenheitSwitchButton.setText("°F" );
        }
        retrieveSensorData();
        requestWeather();
    }

    void retrieveRange(){
        DatabaseReference tempRange = databaseRange.child("TemperatureSensor1");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Double lowRange = Helper.retrieveRange("Low", dataSnapshot);
                Double highRange = Helper.retrieveRange("High", dataSnapshot);

                lowTempEditText.setText(lowRange.toString());
                highTempEditText.setText(highRange.toString());
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
        tempHistoryButton = findViewById(R.id.tempHistoriyButton);
        heaterUseHistoryButton = findViewById(R.id.heaterUseHistoryButton);
        tempControlTextView = findViewById(R.id.tempControlTextView);
        temperatureSwitch = findViewById(R.id.tempSwitch);

        greenhouseTemperatureTextView=findViewById(R.id.ghTempTextView);
        //Get the values from the user
        lowTempEditText = findViewById(R.id.lowTempEditText);
        highTempEditText = findViewById(R.id.highTempEditText);

        outdoorTemperatureTextView = findViewById(R.id.outdoorTempTextView);
        greenhouseTemperatureTextView = findViewById(R.id.ghTempTextView);
        tempLastUpdatedTextView=findViewById(R.id.tempLastUpdatedTextview);

        queue = Volley.newRequestQueue(this);
        requestWeather();

        celsiusFahrenheitSwitchButton = findViewById(R.id.celsiusFahrenheitSwitchButton);
        if (helper.retrieveTemperatureMetric()) {
            celsiusFahrenheitSwitchButton.setText("°C" );
        } else {
            celsiusFahrenheitSwitchButton.setText("°F" );
        }
        celsiusFahrenheitSwitchButton.setClickable(false);
        celsiusFahrenheitSwitchButton.setFocusable(false);
        currentUser = helper.checkAuthentication();
        setRangeTempButton=(Button)findViewById(R.id.setRangeTempButton);
    }



    private void heaterSwitchStateFromRecord() {

        heaterSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ApplianceControlEvents lastRecord = dataSnapshot.getValue(ApplianceControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getEventOnOff();
                    previousHeaterTriggerTime = lastRecord.getEventUnixEpoch();

                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.AppliancePreviousTriggerTimesFile), MODE_PRIVATE).edit();
                editor.putLong(getString(R.string.PreviousHeaterTriggerTime), previousHeaterTriggerTime);
                editor.apply();

                if(!(checkedStatus == null)){

                    lastHeaterState = checkedStatus;
                    temperatureSwitch.setChecked(checkedStatus);
                    if(checkedStatus){
                        temperatureSwitch.setTextColor(Color.RED);
                    }else{
                        temperatureSwitch.setTextColor(Color.DKGRAY);
                    }

                }else{

                    Log.d(TAG, "On/Off Status of heater can't be null, getHeaterEventOnOff points to null");
                }
                if(checkedStatus){
                    Log.d(TAG, "The heater was on");
                }else{
                    Log.d(TAG, "The heater was off");
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

                ApplianceControlEvents heatSwitchClickEvent = new ApplianceControlEvents(heatEventId, heatOnTimeStampFormated, heatOnOffDateUnixFormat, previousHeaterTriggerTime, currentUserID, currentUserName, currentUserEmail, tempSwitchState);
                heaterSwitchEventDB.child(heatEventId).setValue(heatSwitchClickEvent);

                if(!(heatEventId == null)) {

                    if (tempSwitchState) {
                        appliances.child("HeaterCtrl").setValue(true);
                        Log.d(TAG, "The heater was turned on " + heatOnTimeStampFormated);
                        Toast.makeText(this, "The heater was switched ON on " + heatOnTimeStampFormated, Toast.LENGTH_LONG).show();

                    } else {
                        appliances.child("HeaterCtrl").setValue(false);
                        Log.d(TAG, "The heater was turned off on " + heatOnTimeStampFormated);
                        Toast.makeText(this, "The heater was switched OFF on " + heatOnTimeStampFormated, Toast.LENGTH_LONG).show();
                    }
                }else{
                    Log.d(TAG, "ERROR: heatEventId can't be null");

                }
            }
        }

    void retrieveSensorData() {
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SensorDataValue sensorDataValue = snap.getValue(SensorDataValue.class);
                    greenhouseTemperatureTextView.setText(new DecimalFormat("####0.0").format(sensorDataValue.getValue()) +"");
                    if (helper.retrieveTemperatureMetric()) {
                        //Temperature already in the correct format
                    } else { // change to fahrenheit
                        celsiusFahrenheitSwitch();
                    }
                    tempDegree = Double.parseDouble(greenhouseTemperatureTextView.getText().toString());
                    long unixTime= sensorDataValue.getTime();
                    String readableTime=Helper.convertTime(unixTime);
                    tempLastUpdatedTextView.setText("Sensor last updated "+readableTime);

                    LastUnixTime = sensorDataValue.getTime()/1000;
                    setLastUnixTime(LastUnixTime);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        db.child("TemperatureSensor1").orderByKey().limitToLast(1).addValueEventListener(eventListener);
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
                            if (helper.retrieveTemperatureMetric()) {
                                outdoorTemperatureTextView.setText(temperature.toString());
                            } else {
                                outdoorTemperatureTextView.setText(
                                        helper.celsiusFahrenheitConversion(temperature.toString()));
                            }
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
        DatabaseReference databaseRange = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Ranges");

        String lowTemp = lowTempEditText.getText().toString();
        String highTemp = highTempEditText.getText().toString();

        //check if the ranges are empty or not
        if (!TextUtils.isEmpty(lowTemp) && !TextUtils.isEmpty(highTemp)) {

            //theck if input is numerical
            if((lowTemp.matches(".*[0-999].*"))&&(highTemp.matches(".*[0-999].*"))){
                //Check if Lower limit is < upper limit
                if (Double.parseDouble(lowTemp) < Double.parseDouble(highTemp)) {
                    databaseRange.child("TemperatureSensor1").child("Low").setValue(Double.parseDouble(lowTemp));
                    databaseRange.child("TemperatureSensor1").child("High").setValue(Double.parseDouble(highTemp));
                    Toast.makeText(this, "RANGE SUCCESSFULLY SET!!!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "", Toast.LENGTH_LONG).show();
                }

            } else{
                Toast.makeText(this, "Please enter a number for lower and upper temperature limits", Toast.LENGTH_LONG).show();

            }

        } else{
            Toast.makeText(this, "Please set a values to your desired Upper and Lower Temperature Limits", Toast.LENGTH_LONG).show();

        }
    }

    /*
    * Function that will convert all necessary parameters between celsius and fahrenheit
     */
    void celsiusFahrenheitSwitch(){
        outdoorTemperatureTextView.setText(
                helper.celsiusFahrenheitConversion(outdoorTemperatureTextView.getText().toString()));
        greenhouseTemperatureTextView.setText(
                helper.celsiusFahrenheitConversion(greenhouseTemperatureTextView.getText().toString()));
        //celisusOrFahrenheit = !celisusOrFahrenheit;
    }
    public void openDialog(){
        PollingFrequencyDialogFragment dialog = new PollingFrequencyDialogFragment();
        dialog.show(getSupportFragmentManager(), "Polling dialog");
    }
    // dialog to display the change password fragment
    public void changePasswordDialog(){

        ChangePasswordDialogFragment changePassword=new ChangePasswordDialogFragment();
        changePassword.show(getSupportFragmentManager(),"Change Password dialog");
    }

    public void checkSensorInactivity(){

        //Obtain Poll Frequency, Current Unix time and determine if sensor is inactive
        generalDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long CurrentunixTime = System.currentTimeMillis() / 1000L;

                String lastpollfrequencyMs = dataSnapshot.child("SensorConfig/poll").getValue().toString();
                lastpollfrequencyInt = Integer.parseInt(lastpollfrequencyMs) / 1000;

                if ((CurrentunixTime-LastUnixTime) > (lastpollfrequencyInt+5)){
                    sendSensorInactivityNotification ();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setLastUnixTime(long lastTime){
        LastUnixTime = lastTime;
    }

    public void createChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(Channel_ID,"Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is channel 1");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    public void sendSensorInactivityNotification (){
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this,Channel_ID);
        notification.setSmallIcon(R.drawable.igro_logo);
        notification.setContentTitle("Temperature Sensor is Currently Inactive!");
        notification.setContentText("Please Reconnect Sensor or Turn on iGRO System");
        notification.setPriority(NotificationCompat.PRIORITY_HIGH);
        notification.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);


        //functionality to open humidityactivity on notification click
        Intent notifyIntent = new Intent(this, HumidityActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        notification.setContentIntent(notifyPendingIntent);


        notificationManager.notify(1,notification.build());
    }

}

