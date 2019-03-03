package com.example.igro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.DateFormat;


public class TemperatureActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;

    //initialize the layout fields
    TextView tempControlTextView;
    Switch tempSwitch;

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "HeaterIsOnTag";

    //create heater database reference
    DatabaseReference heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("heaterControlLog");



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_temperature);

       tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
       tempSwitch = (Switch)findViewById(R.id.tempSwitch);
       tempSwitch.setChecked(false);

        //
        heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("heaterControlLog");

        //set Switch state based on the last child value in heaterSwitchEventsDB
        heaterSwitchState();
        Boolean switchState = tempSwitch.isChecked();


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

        heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("heaterControlLog");

        //call function check last child in heaterSwitchEventDB and set switch to that state
        heaterSwitchState();

        final Boolean switchState = tempSwitch.isChecked();

        tempSwitch.setClickable(true);

        if(switchState == true){
            Log.d(TAG, "The heater was on");
        }else{
            Log.d(TAG, "The heater was off");
        }

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean tempSwitchState){

                //Call heaterSwitchEvent function
                heaterSwitchEvent(switchState);


            }
        });


    }


    private void heaterSwitchState() {

        heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference().child("heaterControlLog");


        heaterSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HeaterControlEvents lastRecord = dataSnapshot.getValue(HeaterControlEvents.class);
                Boolean checkedStatus = lastRecord.getHeaterEventOnOff();

                if(!(checkedStatus == null)){

                    tempSwitch.setChecked(checkedStatus);

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
            DateFormat heatOnDateTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

            //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
            String heatEventId = heaterSwitchEventDB.push().getKey();


            HeaterControlEvents heatSwitchClickEvent = new HeaterControlEvents(heatEventId, heatOnDateTime.toString(), tempSwitchState);
            heaterSwitchEventDB.child(heatEventId).setValue(heatSwitchClickEvent);

            if(!(heatEventId == null)) {


                if (tempSwitchState) {

                    Log.d(TAG, "The heater was turned on " + heatOnDateTime);
                    Toast.makeText(this, "The heater was switched ON on " + heatOnDateTime, Toast.LENGTH_LONG).show();

                } else {
                    Log.d(TAG, "The heater was turned off on " + heatOnDateTime);
                    Toast.makeText(this, "The heater was switched OFF on " + heatOnDateTime, Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d(TAG, "ERROR: heatEventId can't be null");

            }

        }


    }