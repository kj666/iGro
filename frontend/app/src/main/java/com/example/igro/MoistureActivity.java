package com.example.igro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igro.Models.ActuatorControl.MoistureControlEvents;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class MoistureActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;

    //initialize the layout fields
    EditText lowMoistureEditText;
    EditText highMoistureEditText;
    TextView waterControlTextView;
    Switch moistureSwitch;

    public Boolean lastMoistureState;

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "IrrigationIsOnTag";

    //create heater database reference
    DatabaseReference moistureSwitchEventDB = FirebaseDatabase.getInstance().getReference("SoilMoistureControlLog");



    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moisture);

        waterControlTextView = (TextView)findViewById(R.id.waterControlTextView);
        moistureSwitch = (Switch)findViewById(R.id.moistureSwitch);
        moistureSwitch.setClickable(true);

        lowMoistureEditText = (EditText)findViewById(R.id.lowMoistureEditText);
        highMoistureEditText = (EditText)findViewById(R.id.highMoistureEditText);


        double y,x;
        x=-5;

        GraphView graph =findViewById(R.id.graph);
        series=new LineGraphSeries<>();
        for(int i=0; i<500; i++){
            x=x+0.1;
            y=Math.sin(x);
            series.appendData(new DataPoint(x,y),true,500);
        }
        graph.addSeries(series);
    }


    @Override
    protected void onStart() {
        super.onStart();

        waterControlTextView = (TextView)findViewById(R.id.waterControlTextView);
        moistureSwitch = (Switch)findViewById(R.id.moistureSwitch);

        lowMoistureEditText = (EditText)findViewById(R.id.lowMoistureEditText);
        highMoistureEditText = (EditText)findViewById(R.id.highMoistureEditText);

        String lowMoistureLimit = lowMoistureEditText.getText().toString();
        String highMoistureLimit = lowMoistureEditText.getText().toString();

        if((lowMoistureLimit.matches(".*[0-9].*"))&&(highMoistureLimit.matches(".*[0-9].*"))){
            int lowHum = parseInt(lowMoistureLimit);
            int highHum = parseInt(highMoistureLimit);
        }else{
            Toast.makeText(this, "Please enter a valid number for lower and upper moistsure limits", Toast.LENGTH_LONG).show();
        }

        //call function check last child in heaterSwitchEventDB and set switch to that state
        moistureSwitchStateFromRecord();

        final Boolean switchState = moistureSwitch.isChecked();

        if(switchState){
            Log.d(TAG, "The irrigation was on");
            Toast.makeText(this,  "The irrigation was On", Toast.LENGTH_LONG).show();
        }else{
            Log.d(TAG, "The irrigation was off");
            Toast.makeText(this,  "The irrigation was Off", Toast.LENGTH_LONG).show();
        }

        moistureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton humSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                moistureSwitchEvent(SwitchState);

            }
        });

    }


    private void moistureSwitchStateFromRecord() {

        moistureSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                MoistureControlEvents lastRecord = dataSnapshot.getValue(MoistureControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getMoistEventOnOff();

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

        //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
        String moistEventId = moistureSwitchEventDB.push().getKey();


        MoistureControlEvents moistSwitchClickEvent = new MoistureControlEvents(moistEventId, moistOnTimeStampFormated, moistOnOffDateUnixFormat, moistSwitchState);
        moistureSwitchEventDB.child(moistEventId).setValue(moistSwitchClickEvent);

        if(!(moistEventId == null)) {


            if (moistSwitchState) {

                Log.d(TAG, "The irrigation was turned on " + moistOnTimeStampFormated);
                Toast.makeText(this, "The irrigation was switched ON on " + moistOnTimeStampFormated, Toast.LENGTH_LONG).show();

            } else {
                Log.d(TAG, "The irrigation was turned off on " + moistOnTimeStampFormated);
                Toast.makeText(this, "The irrigation was switched OFF on " + moistOnTimeStampFormated, Toast.LENGTH_LONG).show();
            }
        }else{
            Log.d(TAG, "ERROR: moistEventId can't be null");

        }

    }

}