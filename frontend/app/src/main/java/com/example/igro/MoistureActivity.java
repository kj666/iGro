package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MoistureActivity extends AppCompatActivity {

    //initialize the layout fields
    EditText lowMoistureEditText;
    EditText highMoistureEditText;
    TextView waterControlTextView;
    TextView moistureDataTextView;
    Switch moistureSwitch;
    Button moistureHistoryButton;
    Button irrigationUseButton;

    Button setRangeMoistureButton;
    Double ghMoisture;
    TextView ghMoistureTextView;
    private FirebaseUser currentuser;
    String currentuserID;
    String currentuserName;
    String currentuserEmail;
    String getCurrentuserID;
    String getCurrentuserName;
    String getCurrentuserEmail;
    private FirebaseUser currentUser;
    String currentUserID;
    String currentUserName;
    String currentUserEmail;
    public Boolean lastMoistureState = false;
    Long previousIrrigationTriggerTime;
    //Get current user using the Helper class
    private Helper helper = new Helper(this, FirebaseAuth.getInstance());

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "IrrigationIsOnTag";

    //create heater database reference
    DatabaseReference moistureSwitchEventDB, databaseRange, db, appliances, userDB;

    public void initializeDB(String greenhouseID){
        databaseRange = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Ranges");
        moistureSwitchEventDB= FirebaseDatabase.getInstance().getReference(greenhouseID+"/ApplianceControlLog").child("SoilMoistureControlLog");
        db = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Data");
        appliances = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Appliances");
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture);
        initializeUI();
        currentUser = helper.checkAuthentication();
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

        moistureSwitch.setClickable(true);

        setRangeMoistureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMoistureRange();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();


        //call function check last child in heaterSwitchEventDB and set switch to that state

        final Boolean switchState = moistureSwitch.isChecked();
        moistureSwitchStateFromRecord();

        retrieveSensorData();

        moistureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton humSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                moistureSwitchEvent(SwitchState);

            }
        });

        moistureHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MoistureActivity.this;
                Intent i = new Intent(context, SensorDataActivity.class);
                i.putExtra("SensorType", "MOISTURE");
                context.startActivity(i);
            }
        });

        irrigationUseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MoistureActivity.this ;
                Intent i = new Intent(context, HistoricalApplianceActivity.class);
                i.putExtra("ApplianceType", "IRRIGATION");
                context.startActivity(i);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();

        final Boolean switchState = moistureSwitch.isChecked();
        moistureSwitchStateFromRecord();

        moistureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton humSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                moistureSwitchEvent(SwitchState);

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


    public void setMoistureRange(){

        String lowMoisture=lowMoistureEditText.getText().toString();
        String highMoisture=highMoistureEditText.getText().toString();
        //check if the ranges are empty or not
        if (!TextUtils.isEmpty(lowMoisture) && !TextUtils.isEmpty(highMoisture)) {
            if (Double.parseDouble(lowMoisture) < Double.parseDouble(highMoisture)) {

                databaseRange.child("SoilSensor1").child("Low").setValue(Double.parseDouble(lowMoisture));
                databaseRange.child("SoilSensor1").child("High").setValue(Double.parseDouble(highMoisture));
                Toast.makeText(this, "RANGE SUCCESSFULLY SET!!!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "Please, make sure the Upper Limit is bigger than the Lower Limit", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please, set a values to your desired Upper and Lower Humidity Limits", Toast.LENGTH_LONG).show();
        }

    }

    void retrieveRange(){
        DatabaseReference moistureRange = databaseRange.child("SoilSensor1");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double highRange = Helper.retrieveRange("High", dataSnapshot);
                highMoistureEditText.setText(highRange.toString());
                Double lowRange = Helper.retrieveRange("Low", dataSnapshot);
                lowMoistureEditText.setText(lowRange.toString());

                if (!((ghMoisture > lowRange) && (ghMoisture< highRange))) {
                    ghMoistureTextView.setTextColor(Color.RED);
                    Toast.makeText(MoistureActivity.this,"THE SENSOR VALUE IS OUT OF THRESHOLD!!!", Toast.LENGTH_LONG).show();
                }
                else{
                    ghMoistureTextView.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };

        moistureRange.addValueEventListener(eventListener);

    }
    void retrieveSensorData(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorDataValue sensorDataValue = snap.getValue(SensorDataValue.class);
                    ghMoistureTextView.setText(new DecimalFormat("####0.0").format(sensorDataValue.getValue())+"");
                    ghMoisture = Double.parseDouble(ghMoistureTextView.getText().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        db.child("SoilSensor1").orderByKey().limitToLast(1).addValueEventListener(eventListener);
    }

    void initializeUI(){

        //Initialization
        ghMoistureTextView = (TextView)findViewById(R.id.numMoistureTextView);
        lowMoistureEditText = (EditText)findViewById(R.id.lowMoistureEditText);
        highMoistureEditText = (EditText)findViewById(R.id.highMoistureEditText);
        setRangeMoistureButton=(Button)findViewById(R.id.setRangeMoistureButton);
        waterControlTextView = (TextView)findViewById(R.id.waterControlTextView);
        moistureSwitch = (Switch)findViewById(R.id.moistureSwitch);
        moistureSwitch.setClickable(true);

        waterControlTextView = (TextView)findViewById(R.id.waterControlTextView);
        moistureSwitch = (Switch)findViewById(R.id.moistureSwitch);

        lowMoistureEditText = (EditText)findViewById(R.id.lowMoistureEditText);
        highMoistureEditText = (EditText)findViewById(R.id.highMoistureEditText);

        moistureHistoryButton = findViewById(R.id.moistureHistoryButton);
        irrigationUseButton = findViewById(R.id.irrigationUseHistoryButton);
        moistureDataTextView = findViewById(R.id.numMoistureTextView);
    }


    private void moistureSwitchStateFromRecord() {

        moistureSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ApplianceControlEvents lastRecord = dataSnapshot.getValue(ApplianceControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getEventOnOff();
                previousIrrigationTriggerTime = lastRecord.getEventUnixEpoch();

                SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.AppliancePreviousTriggerTimesFile), MODE_PRIVATE).edit();
                editor.putLong(getString(R.string.PreviousLightsTriggerTime), previousIrrigationTriggerTime);
                editor.apply();


                if(!(checkedStatus == null)){

                    lastMoistureState = checkedStatus;
                    moistureSwitch.setChecked(checkedStatus);
                    if(checkedStatus){
                        moistureSwitch.setTextColor(Color.RED);
                    }else{
                        moistureSwitch.setTextColor(Color.DKGRAY);
                    }

                }else{

                    Log.d(TAG, "On/Off Status of humidifier can't be null, getMoistureEventOnOff points to null");

                }
                if(checkedStatus){
                    Log.d(TAG, "The irrigation was on");
                }else{
                    Log.d(TAG, "The irrigation was off");
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


    private void moistureSwitchEvent(boolean moistSwitchState) {

        //record the time of the click
        //DateFormat heatOnDateTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        long moistOnOffDateUnixFormat = System.currentTimeMillis()/1000;

        String moistOnOffDateReadable = new java.text.SimpleDateFormat("MM/dd/yy HH:mm:ss").format(new java.util.Date(moistOnOffDateUnixFormat*1000));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String moistOnTimeStampFormated = df.format(Calendar.getInstance().getTime());

        if(!(moistSwitchState==lastMoistureState)){

            //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
            String moistEventId = moistureSwitchEventDB.push().getKey();

            // creates a record as an object of class HeaterControlEvents, which includes id, dates in 2 formats and on/off state to be recorded
            ApplianceControlEvents moistSwitchClickEvent = new ApplianceControlEvents(moistEventId, moistOnTimeStampFormated, moistOnOffDateUnixFormat, previousIrrigationTriggerTime, currentUserID, currentUserName, currentUserEmail,moistSwitchState);
            moistureSwitchEventDB.child(moistEventId).setValue(moistSwitchClickEvent);

            if(!(moistEventId == null)) {

                // checks the state of the switch to display message
                if (moistSwitchState) {
                    appliances.child("SoilCtrl").setValue(true);
                    Log.d(TAG, "The irrigation was turned on " + moistOnTimeStampFormated);
                    Toast.makeText(this, "The irrigation was switched ON on " + moistOnTimeStampFormated, Toast.LENGTH_LONG).show();
                } else {
                    appliances.child("SoilCtrl").setValue(false);
                    Log.d(TAG, "The irrigation was turned off on " + moistOnTimeStampFormated);
                    Toast.makeText(this, "The irrigation was switched OFF on " + moistOnTimeStampFormated, Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d(TAG, "ERROR: moistEventId can't be null");
            }
        }
    }

 // dialog to display the polling frequency fragment
    public void openDialog(){
        PollingFrequencyDialogFragment dialog = new PollingFrequencyDialogFragment();
        dialog.show(getSupportFragmentManager(), "Polling dialog");
    }
    // dialog to display the change password fragment
    public void changePasswordDialog(){

        ChangePasswordDialogFragment changePassword=new ChangePasswordDialogFragment();
        changePassword.show(getSupportFragmentManager(),"Change Password dialog");
    }


}