package com.example.igro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    DatabaseReference heaterSwitchEventDB;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_temperature);

       tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
       tempSwitch = (Switch)findViewById(R.id.tempSwitch);
        Boolean switchState = tempSwitch.isChecked();

        //
        heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("heaterControl");



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

        tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
        tempSwitch = (Switch)findViewById(R.id.tempSwitch);
        Boolean switchState = tempSwitch.isChecked();

        tempSwitch.setClickable(true);

        if(switchState == true){
            Log.d(TAG, "The heater is on");
        }else{
            Log.d(TAG, "The heater is off");
        }

        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton tempSwitch, boolean tempSwitchState){

                //Call heaterSwitchEvent function
                heaterSwitchEvent(tempSwitchState);


            }
        });


    }


    private void heaterSwitchEvent(boolean tempSwitchState) {

        //record the time of the click
        DateFormat heatOnDateTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
        String heatEventId = heaterSwitchEventDB.push().getKey();

        if(!(heatEventId==null)) {
            HeaterControlEvents heatSwitchClickEvent = new HeaterControlEvents(heatEventId, heatOnDateTime.toString(), tempSwitchState);
            heaterSwitchEventDB.child(heatEventId).setValue(heatSwitchClickEvent);

            if (tempSwitchState) {

                Log.d(TAG, "The heater was turned on " + heatOnDateTime);
                Toast.makeText(this, "The heater was switched ON on " + heatOnDateTime, Toast.LENGTH_LONG).show();

            } else {
                Log.d(TAG, "The heater was turned off on " + heatOnDateTime);
                Toast.makeText(this, "The heater was switched OFF on " + heatOnDateTime, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "HeaterEventId cannot be null", Toast.LENGTH_LONG).show();

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

}
