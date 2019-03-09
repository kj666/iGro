package com.example.igro;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class UvIndexActivity extends AppCompatActivity {

    LineGraphSeries<DataPoint> series;

    //initialize the layout fields
    EditText lowUvEditText;
    EditText highUvEditText;
    TextView uvControlTextView;
    Switch uvSwitch;

    public Boolean lastUvState;

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "LightsAreOnTag";

    //create heater database reference
    DatabaseReference uvSwitchEventDB = FirebaseDatabase.getInstance().getReference("UVControlLog");



    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uv_index);

        uvControlTextView = (TextView)findViewById(R.id.uvControlTextView);
        uvSwitch = (Switch)findViewById(R.id.uvSwitch);
        uvSwitch.setClickable(true);

        lowUvEditText = (EditText)findViewById(R.id.lowUvEditText);
        highUvEditText = (EditText)findViewById(R.id.highUvEditText);


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

        uvControlTextView = (TextView)findViewById(R.id.uvControlTextView);
        uvSwitch = (Switch)findViewById(R.id.uvSwitch);

        lowUvEditText = (EditText)findViewById(R.id.lowUvEditText);
        highUvEditText = (EditText)findViewById(R.id.highUvEditText);

        String lowUvLimit = lowUvEditText.getText().toString();
        String highUvLimit = lowUvEditText.getText().toString();

        if((lowUvLimit.matches(".*[0-9].*"))&&(highUvLimit.matches(".*[0-9].*"))){
            int lowUv = parseInt(lowUvLimit);
            int highUv = parseInt(highUvLimit);
        }else{
            Toast.makeText(this, "Please enter a valid number for lower and upper UV limits", Toast.LENGTH_LONG).show();
        }

        //call function check last child in heaterSwitchEventDB and set switch to that state
        uvSwitchStateFromRecord();

        final Boolean switchState = uvSwitch.isChecked();

        if(switchState){
            Log.d(TAG, "The lights were on");
            Toast.makeText(this,  "The lights were On", Toast.LENGTH_LONG).show();
        }else{
            Log.d(TAG, "Thelights were off");
            Toast.makeText(this,  "The lights were Off", Toast.LENGTH_LONG).show();
        }

        uvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton uvSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                uvSwitchEvent(SwitchState);
            }
        });

    }


    private void uvSwitchStateFromRecord() {

        uvSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                UvControlEvents lastRecord = dataSnapshot.getValue(UvControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getUvEventOnOff();

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

        //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
        String uvEventId = uvSwitchEventDB.push().getKey();


        UvControlEvents uvSwitchClickEvent = new UvControlEvents(uvEventId, uvOnTimeStampFormated, uvOnOffDateUnixFormat, uvSwitchState);
        uvSwitchEventDB.child(uvEventId).setValue(uvSwitchClickEvent);

        if(!(uvEventId == null)) {


            if (uvSwitchState) {

                Log.d(TAG, "The lights were turned on " + uvOnTimeStampFormated);
                Toast.makeText(this, "The lights were switched ON on " + uvOnTimeStampFormated, Toast.LENGTH_LONG).show();

            } else {
                Log.d(TAG, "Thelights were turned off on " + uvOnTimeStampFormated);
                Toast.makeText(this, "The lights were switched OFF on " + uvOnTimeStampFormated, Toast.LENGTH_LONG).show();
            }
        }else{
            Log.d(TAG, "ERROR: uvEventId can't be null");

        }

    }

}
