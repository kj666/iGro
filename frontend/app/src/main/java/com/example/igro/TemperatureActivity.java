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

import com.example.igro.Controller.Helper;
import com.example.igro.Models.ActuatorControl.HeaterControlEvents;
import com.example.igro.Models.SensorData.Range;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class TemperatureActivity extends AppCompatActivity {



    //initialize the layout fields
    Button tempHistoryButton;
    Button heaterUseHistoryButton;
    Button setRangeTempButton;
    EditText lowTempEditText;
    EditText highTempEditText;
    TextView tempControlTextView;
    TextView indoorTempTextView;
    Switch tempSwitch;
    private  String tempRange;

    DatabaseReference databaseRange=FirebaseDatabase.getInstance().getReference("Ranges");
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
        FirebaseUser rangeT=FirebaseAuth.getInstance().getCurrentUser();
        tempRange=rangeT.getUid();

        tempHistoryButton = (Button)findViewById(R.id.tempHistoriyButton);
        heaterUseHistoryButton = (Button)findViewById(R.id.heaterUseHistoryButton);
        tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
        tempSwitch = (Switch)findViewById(R.id.tempSwitch);
        tempSwitch.setClickable(true);
        indoorTempTextView=(TextView)findViewById(R.id.indoorTempTextView);
        //Get the values from the user
        lowTempEditText = (EditText)findViewById(R.id.lowTempEditText);
        highTempEditText = (EditText)findViewById(R.id.highTempEditText);
        setRangeTempButton=(Button)findViewById(R.id.setRangeTempButton);

        currentUser = helper.checkAuthentication();
        setRangeTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTempRange();

                
            }
        });

        databaseRange.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              // checkTempRange(dataSnapshot);

            }





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

private void checkTempRange(DataSnapshot dataSnapshot) {
    for (DataSnapshot ds : dataSnapshot.getChildren()) {

        Range range = new Range();
        range.setLowTempValue(ds.child(tempRange).getValue(Range.class).getLowTempValue()); //set the name
        range.setHighTempValue(ds.child(tempRange).getValue(Range.class).getHighTempValue()); //set the name
        if (!(Integer.parseInt(indoorTempTextView.getText().toString()) > Integer.parseInt(range.getLowTempValue())
                && Integer.parseInt(indoorTempTextView.getText().toString()) < Integer.parseInt(range.getHighTempValue()))) {

            indoorTempTextView.setTextColor(Color.RED);
        }
        else{

                indoorTempTextView.setTextColor(Color.GREEN);
            }
        }

}
    public void setTempRange(){

        String lowTemp=lowTempEditText.getText().toString();
        String highTemp=highTempEditText.getText().toString();
        //check if the ranges are empty or not
            if (!TextUtils.isEmpty(lowTemp) && !TextUtils.isEmpty(highTemp)) {
                if (Integer.parseInt(lowTemp.toString()) < Integer.parseInt(highTemp.toString())) {

                    Range temperatureRange = new Range(lowTemp, highTemp);
                    databaseRange.child("Temperature").setValue(temperatureRange);
                    Toast.makeText(this, "RANGE SUCCESSFULLY SET!!!", Toast.LENGTH_LONG).show();



                } else {
                    Toast.makeText(this, "HIGH VALUES SHOULD BE GREATER THAN LOW VALUES!!!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "YOU SHOULD ENTER LOW AND HIGH VALUES!!!", Toast.LENGTH_LONG).show();
            }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();

        tempHistoryButton = (Button)findViewById(R.id.tempHistoriyButton);
        heaterUseHistoryButton = (Button)findViewById(R.id.heaterUseHistoryButton);
        tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
        tempSwitch = (Switch)findViewById(R.id.tempSwitch);

        lowTempEditText = (EditText)findViewById(R.id.lowTempEditText);
        highTempEditText = (EditText)findViewById(R.id.highTempEditText);
        setRangeTempButton=(Button)findViewById(R.id.setRangeTempButton);

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

    }

