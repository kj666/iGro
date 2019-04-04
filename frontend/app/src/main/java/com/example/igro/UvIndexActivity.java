package com.example.igro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.text.TextUtils;

import com.example.igro.Controller.Helper;
import com.example.igro.Models.ActuatorControl.ApplianceControlEvents;
import com.example.igro.Models.SensorData.SensorDataValue;
import com.google.firebase.auth.FirebaseAuth;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class UvIndexActivity extends AppCompatActivity {
    private static final String UV_ACTIVITY_LOG_TAG = "UV_ACTIVITY_LOG_TAG";

    TextView outdoorUVTextView;
    private RequestQueue queue;

    LineGraphSeries<DataPoint> series;

    //initialize the layout fields
    EditText lowUvEditText;
    EditText highUvEditText;
    TextView uvControlTextView;
    TextView uvLastUpdatedTextView;
    Switch uvSwitch;
    TextView uvTextView;
    Button uvHistoryButton;
    Button lightUseButton;
    Button setUvRange;
    TextView ghUvTextView;
    Double ghUv;


    private FirebaseUser currentuser;
    String currentuserID;
    String currentuserName;
    String currentuserEmail;
    private FirebaseUser currentUser;
    String currentUserID;
    String currentUserName;
    String currentUserEmail;
    private Helper helper = new Helper(this, FirebaseAuth.getInstance());
    //last Lights switch state and time of previous trigger to calculated how long they were on
    public Boolean lastUvState = false;
    Long previousLightsTriggerTime;
    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "LightsAreOnTag";

    //create heater database reference for the correct node
    DatabaseReference uvSwitchEventDB, databaseRange, db, appliances, userDB, generalDB;

    int lastpollfrequencyInt;
    long LastUnixTime;
    private final String Channel_ID = "channel1";
    private NotificationManagerCompat notificationManager;


    public void initializeDB(String greenhouseID){
        databaseRange = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Ranges");
        uvSwitchEventDB = FirebaseDatabase.getInstance().getReference(greenhouseID+"/ApplianceControlLog").child("UVControlLog");
        db = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Data");
        appliances = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Appliances");
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        generalDB = FirebaseDatabase.getInstance().getReference().child(greenhouseID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uv_index);

        initializeUI();
        helper.setSharedPreferences(getApplicationContext());
        initializeDB(helper.retrieveGreenhouseID());

        currentUser = helper.checkAuthentication();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        currentuserID = currentuser.getUid();
        currentuserName = currentuser.getDisplayName();
        currentuserEmail = currentUser.getEmail();

        currentUserID = currentUser.getUid();
        currentUserName = currentUser.getDisplayName();
        currentUserEmail = currentUser.getEmail();

        retrieveSensorData();
        retrieveRange();
        setUvRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUvRange();
            }
        });

        notificationManager = NotificationManagerCompat.from(this);
        checkSensorInactivity();
        createChannel();

    }


    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();

        //call function check last child in heaterSwitchEventDB and set switch to that state

        final Boolean switchState = uvSwitch.isChecked();
        uvSwitchStateFromRecord();

        uvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton uvSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                uvSwitchEvent(SwitchState);
            }
        });

        uvHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = UvIndexActivity.this;
                Intent i = new Intent(context, SensorDataActivity.class);
                i.putExtra("SensorType", "UV");
                context.startActivity(i);
            }
        });

        lightUseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = UvIndexActivity.this ;
                Intent i = new Intent(context, HistoricalApplianceActivity.class);
                i.putExtra("ApplianceType", "LIGHTS");
                context.startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        final Boolean switchState = uvSwitch.isChecked();
        uvSwitchStateFromRecord();

        uvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton uvSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                uvSwitchEvent(SwitchState);
            }
        });

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


    //Initialization
    void initializeUI(){
        uvControlTextView = findViewById(R.id.uvControlTextView);
        uvSwitch = findViewById(R.id.uvSwitch);
        uvSwitch.setClickable(true);

        lowUvEditText = findViewById(R.id.lowUvEditText);
        highUvEditText = findViewById(R.id.highUvEditText);
        uvTextView = findViewById(R.id.ghUvTextView);
        uvLastUpdatedTextView=findViewById(R.id.uvLastUpdatedTextview);

        outdoorUVTextView = findViewById(R.id.outdoorUvTextView);
        queue = Volley.newRequestQueue(this);
        requestUVIndex();

        uvHistoryButton = findViewById(R.id.uvHistoryButton);
        lightUseButton = findViewById(R.id.lightUseHistoryButton);
        //indoor uv
        ghUvTextView =  findViewById(R.id.ghUvTextView);
        //set range button
        setUvRange = findViewById(R.id.setUvRange);
    }


    void retrieveRange(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Double lowRange = Helper.retrieveRange("Low", dataSnapshot);
                Double highRange = Helper.retrieveRange("High", dataSnapshot);

                lowUvEditText.setText(lowRange.toString());
                highUvEditText.setText(highRange.toString());
                if (!((ghUv > lowRange) && (ghUv< highRange))) {
                    ghUvTextView.setTextColor(Color.RED);
                    Toast.makeText(UvIndexActivity.this,"THE SENSOR VALUE IS OUT OF THRESHOLD!!!", Toast.LENGTH_LONG).show();
                }
                else{
                    ghUvTextView.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        databaseRange.child("UVSensor1").addValueEventListener(eventListener);

    }


    // set the user input UV range
    public void setUvRange() {

        String lowUv = lowUvEditText.getText().toString();
        String highUv = highUvEditText.getText().toString();
 //check if the ranges are empty or not
            if (!TextUtils.isEmpty(lowUv) && !TextUtils.isEmpty(highUv)) {
 //Check if Lower limit is < upper limit
                if (Double.parseDouble(lowUv) < Double.parseDouble(highUv)) {

                    databaseRange.child("UVSensor1").child("Low").setValue(Double.parseDouble(lowUv));
                    databaseRange.child("UVSensor1").child("High").setValue(Double.parseDouble(highUv));
                    Toast.makeText(this, "RANGE SUCCESSFULLY SET!!!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "Please, make sure the Upper Limit is bigger than the Lower Limit", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "Please set values to your desired Upper and Lower UV Limits", Toast.LENGTH_LONG).show();
            }
    }


    private void uvSwitchStateFromRecord() {

        uvSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ApplianceControlEvents lastRecord = dataSnapshot.getValue(ApplianceControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getEventOnOff();
                previousLightsTriggerTime = lastRecord.getEventUnixEpoch();

                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.AppliancePreviousTriggerTimesFile), MODE_PRIVATE).edit();
                editor.putLong(getString(R.string.PreviousLightsTriggerTime), previousLightsTriggerTime);
                editor.apply();

                if(!(checkedStatus == null)){
                    lastUvState = checkedStatus;
                    uvSwitch.setChecked(checkedStatus);
                    if(checkedStatus){
                        uvSwitch.setTextColor(Color.RED);
                    }else{
                        uvSwitch.setTextColor(Color.DKGRAY);
                    }
                }else{
                    Log.d(TAG, "On/Off Status of Lights can't be null, getUvEventOnOff points to null");

                }
                if(checkedStatus){
                    Log.d(TAG, "The lights were on");
                }else{
                    Log.d(TAG, "The lights were off");
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

    private void uvSwitchEvent(boolean uvSwitchState) {

        //record the time of the click
        //DateFormat heatOnDateTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        long uvOnOffDateUnixFormat = System.currentTimeMillis()/1000;

        String uvOnOffDateReadable = new java.text.SimpleDateFormat("MM/dd/yy HH:mm:ss").format(new java.util.Date(uvOnOffDateUnixFormat*1000));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String uvOnTimeStampFormated = df.format(Calendar.getInstance().getTime());

        if(!(uvSwitchState==lastUvState)){

            //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
            String uvEventId = uvSwitchEventDB.push().getKey();

            ApplianceControlEvents uvSwitchClickEvent = new ApplianceControlEvents(uvEventId, uvOnTimeStampFormated, uvOnOffDateUnixFormat, previousLightsTriggerTime, currentUserID, currentUserName, currentUserEmail, uvSwitchState);
            uvSwitchEventDB.child(uvEventId).setValue(uvSwitchClickEvent);

            if(!(uvEventId == null)) {
                if (!lastUvState) {
                    appliances.child("LightCtrl").setValue(true);
                    Log.d(TAG, "The lights were turned on " + uvOnTimeStampFormated);
                    Toast.makeText(this, "The lights were switched ON on " + uvOnTimeStampFormated, Toast.LENGTH_LONG).show();
                } else {
                    appliances.child("LightCtrl").setValue(false);
                    Log.d(TAG, "Thelights were turned off on " + uvOnTimeStampFormated);
                    Toast.makeText(this, "The lights were switched OFF on " + uvOnTimeStampFormated, Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d(TAG, "ERROR: uvEventId can't be null");

            }
        }
    }

    void retrieveSensorData(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorDataValue sensorDataValue = snap.getValue(SensorDataValue.class);
                    uvTextView.setText(new DecimalFormat("####0.00").format(sensorDataValue.getValue())+"");
                    ghUv = Double.parseDouble(uvTextView.getText().toString());
                    long unixTime= sensorDataValue.getTime();
                    String readableTime=Helper.convertTime(unixTime);
                    uvLastUpdatedTextView.setText("Sensor Last Updated on "+readableTime);
                    LastUnixTime = sensorDataValue.getTime()/1000;
                    setLastUnixTime(LastUnixTime);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        db.child("UVSensor1").orderByKey().limitToLast(1).addValueEventListener(eventListener);
    }

    void requestUVIndex() {
        // TODO: 2019-03-18
        // Make this function capable of pulling data for any city as per user request

        // Get weather for Montreal
        String url = "https://api.openweathermap.org/data/2.5/uvi?lat=45&lon=-73&appid=b4840319c97c4629912dc391ed164bcb";
        // Make request
        JsonObjectRequest weatherRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(UV_ACTIVITY_LOG_TAG, "response: " + response);
                        try {
                            // Get description from weather response
                            //String description = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            //descriptionTextView.setText(description);

                            // Get temperature from weather response
                            Double uv = response.getDouble("value");
                            outdoorUVTextView.setText(uv.toString());

                        } catch (Exception e) {
                            Log.w(UV_ACTIVITY_LOG_TAG, "Attempt to parse JSON Object failed");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(UV_ACTIVITY_LOG_TAG, "JSON Request has failed");
                    }
                });
        queue.add(weatherRequest);
    }
    public void openDialog(){
        PollingFrequencyDialogFragment dialog = new PollingFrequencyDialogFragment();
        dialog.show(getSupportFragmentManager(), "Polling dialog");
    }
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

                if ((CurrentunixTime-LastUnixTime) > (lastpollfrequencyInt+30)){
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
        notification.setContentTitle("UV Sensor is Currently Inactive!");
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


