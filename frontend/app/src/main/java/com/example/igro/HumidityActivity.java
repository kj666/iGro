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


public class HumidityActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;

    //initialize the layout fields
    EditText lowHumEditText;
    EditText highHumEditText;
    TextView humControlTextView;
    Switch humSwitch;

    public Boolean lastHumidState;

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "HumidifyerIsOnTag";

    //create heater database reference
    DatabaseReference humidSwitchEventDB = FirebaseDatabase.getInstance().getReference("HumidControlLog");


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        humControlTextView = (TextView)findViewById(R.id.humControlTextView);
        humSwitch = (Switch)findViewById(R.id.humSwitch);
        humSwitch.setClickable(true);

        lowHumEditText = (EditText)findViewById(R.id.lowHumEditText);
        highHumEditText = (EditText)findViewById(R.id.highHumEditText);

        humidSwitchStateFromRecord();

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

        humControlTextView = (TextView)findViewById(R.id.humControlTextView);
        humSwitch = (Switch)findViewById(R.id.humSwitch);

        lowHumEditText = (EditText)findViewById(R.id.lowHumEditText);
        highHumEditText = (EditText)findViewById(R.id.highHumEditText);

        String lowHumLimit = lowHumEditText.getText().toString();
        String highHumLimit = lowHumEditText.getText().toString();

        if((lowHumLimit.matches(".*[0-9].*"))&&(highHumLimit.matches(".*[0-9].*"))){
            int lowHum = parseInt(lowHumLimit);
            int highHum = parseInt(highHumLimit);
        }else{
            Toast.makeText(this, "Please enter a valid number for lower and upper humidity limits", Toast.LENGTH_LONG).show();
        }

        //call function check last child in heaterSwitchEventDB and set switch to that state
        humidSwitchStateFromRecord();

        final Boolean switchState = humSwitch.isChecked();

        if(switchState){
            Log.d(TAG, "The heater was on");
            Toast.makeText(this,  "The humidifier was On", Toast.LENGTH_LONG).show();
        }else{
            Log.d(TAG, "The heater was off");
            Toast.makeText(this,  "The humidifier was Off", Toast.LENGTH_LONG).show();
        }

        humSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton humSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                humidSwitchEvent(SwitchState);

            }
        });

    }


    private void humidSwitchStateFromRecord() {



        humidSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HumidControlEvents lastRecord = dataSnapshot.getValue(HumidControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getHumidEventOnOff();

                if(!(checkedStatus == null)){

                    lastHumidState = checkedStatus;
                    humSwitch.setChecked(checkedStatus);
                    if(checkedStatus){
                        humSwitch.setTextColor(Color.RED);
                    }else{
                        humSwitch.setTextColor(Color.DKGRAY);
                    }

                }else{

                    Log.d(TAG, "On/Off Status of humidifier can't be null, getHumidEventOnOff points to null");

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


    private void humidSwitchEvent(boolean humSwitchState) {

        //record the time of the click
        //DateFormat heatOnDateTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        long humOnOffDateUnixFormat = System.currentTimeMillis()/1000;

        String humOnOffDateReadable = new java.text.SimpleDateFormat("MM/dd/yy HH:mm:ss").format(new java.util.Date(humOnOffDateUnixFormat*1000));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String humOnTimeStampFormated = df.format(Calendar.getInstance().getTime());

        //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
        String humEventId = humidSwitchEventDB.push().getKey();

        HumidControlEvents humSwitchClickEvent = new HumidControlEvents(humEventId, humOnTimeStampFormated, humOnOffDateUnixFormat, humSwitchState);
        humidSwitchEventDB.child(humEventId).setValue(humSwitchClickEvent);

        if(!(humEventId == null)) {


            if (humSwitchState) {

                Log.d(TAG, "The humidifier was turned on " + humOnTimeStampFormated);
                Toast.makeText(this, "The humidifier was switched ON on " + humOnTimeStampFormated, Toast.LENGTH_LONG).show();

            } else {
                Log.d(TAG, "The humidifier was turned off on " + humOnTimeStampFormated);
                Toast.makeText(this, "The humidifier was switched OFF on " + humOnTimeStampFormated, Toast.LENGTH_LONG).show();
            }
        }else{
            Log.d(TAG, "ERROR: humEventId can't be null");

        }

    }

}

