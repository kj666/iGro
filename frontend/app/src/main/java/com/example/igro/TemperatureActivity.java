package com.example.igro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Integer.parseInt;


public class TemperatureActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;

    //initialize the layout fields
    EditText lowTempEditText;
    EditText highTempEditText;
    TextView tempControlTextView;
    Switch tempSwitch;

    public Boolean lastHeaterState;

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "HeaterIsOnTag";

    //create heater database reference
    DatabaseReference heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("HeaterControlEvents");



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);

        tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
        tempSwitch = (Switch)findViewById(R.id.tempSwitch);
        tempSwitch.setClickable(true);

        lowTempEditText = (EditText)findViewById(R.id.lowTempEditText);
        highTempEditText = (EditText)findViewById(R.id.highTempEditText);




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

        tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
        tempSwitch = (Switch)findViewById(R.id.tempSwitch);

        lowTempEditText = (EditText)findViewById(R.id.lowTempEditText);
        highTempEditText = (EditText)findViewById(R.id.highTempEditText);

        String lowTempLimit = lowTempEditText.getText().toString();
        String highTempLimit = lowTempEditText.getText().toString();

        if((lowTempLimit.matches(".*[0-9].*"))&&(highTempLimit.matches(".*[0-9].*"))){
            int lowTemp = parseInt(lowTempLimit);
            int highTemp = parseInt(highTempLimit);
        }else{
            Toast.makeText(this, "Please enter a valid number for lower and upper temperature limits", Toast.LENGTH_LONG).show();
        }



        heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("heaterControlLog");

        //call function check last child in heaterSwitchEventDB and set switch to that state
         heaterSwitchStateFromRecord();

        Boolean switchState = tempSwitch.isChecked();

        if(switchState){
            Log.d(TAG, "The heater was on");
        }else{
            Log.d(TAG, "The heater was off");
        }

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean tempSwitchState){

                //Call heaterSwitchEvent function
                heaterSwitchEvent(tempSwitchState);


            }
        });

    }


    private void heaterSwitchStateFromRecord() {

        heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference().child("heaterControlLog");

        heaterSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HeaterControlEvents lastRecord = dataSnapshot.getValue(HeaterControlEvents.class);
                Boolean checkedStatus = lastRecord.getHeaterEventOnOff();

                if(!(checkedStatus == null)){

                    lastHeaterState = checkedStatus;
                    tempSwitch.setChecked(checkedStatus);
                    if(checkedStatus){
                        tempSwitch.setTextColor(Color.GREEN);
                    }else{
                        tempSwitch.setTextColor(Color.RED);
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

            long heatOnOffUnixFormat = System.currentTimeMillis()/1000;

            String heatOnOffReadable = new java.text.SimpleDateFormat("MM/dd/yy HH:mm:ss").format(new java.util.Date(heatOnOffUnixFormat*1000));

            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String heatOnTimeStampFormated = df.format(Calendar.getInstance().getTime());

            //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
            String heatEventId = heaterSwitchEventDB.push().getKey();


            HeaterControlEvents heatSwitchClickEvent = new HeaterControlEvents(heatEventId, heatOnTimeStampFormated, heatOnOffUnixFormat, tempSwitchState);
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

