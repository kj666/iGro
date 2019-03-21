package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.example.igro.Models.SensorData.SensorData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TemperatureActivity extends AppCompatActivity {



    //initialize the layout fields
    Button tempHistoryButton;
    private TextView TempTextView;
    private double ghtempDegree;
    private boolean ghcelsius_pressed = true;
    private Button ghtemperatureCelsiusButton;
    private Button ghtemperatureFahrenheitButton;
    Button heaterUseHistoryButton;
    EditText lowTempEditText;
    EditText highTempEditText;
    TextView tempControlTextView;
    Switch tempSwitch;

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

        ghtemperatureCelsiusButton= (Button)findViewById(R.id.celciusghButton);
        ghtemperatureFahrenheitButton = (Button)findViewById(R.id.fahrenheitghButton);

        tempHistoryButton = (Button)findViewById(R.id.tempHistoriyButton);
        heaterUseHistoryButton = (Button)findViewById(R.id.heaterUseHistoryButton);
        tempControlTextView = (TextView)findViewById(R.id.tempControlTextView);
        tempSwitch = (Switch)findViewById(R.id.tempSwitch);
        TempTextView = (TextView)findViewById(R.id.ghTempTextView) ;
        tempSwitch.setClickable(true);

        lowTempEditText = (EditText)findViewById(R.id.lowTempEditText);
        highTempEditText = (EditText)findViewById(R.id.highTempEditText);



        currentUser = helper.checkAuthentication();
        retrieveSensorData();

        ghtemperatureCelsiusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ghcelsius_pressed) {
                   TempTextView.setText(ghtempDegree+"");
                   ghcelsius_pressed = true;
                }
            }
        });

       ghtemperatureFahrenheitButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {

               if(ghcelsius_pressed) {
                  Double degrees = Double.parseDouble(TempTextView.getText().toString());
                   Double a = degrees * 9 / 5 + 32;
                   TempTextView.setText(Double.toString(a));

                    ghcelsius_pressed=false;
                }
            }
        });



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

        String lowTempLimit = lowTempEditText.getText().toString();
        String highTempLimit = lowTempEditText.getText().toString();

        if((lowTempLimit.matches(".*[0-9].*"))&&(highTempLimit.matches(".*[0-9].*"))){
            Integer lowTemp = Integer.parseInt(lowTempLimit);
            Integer highTemp = Integer.parseInt(highTempLimit);
        }else{
  //          Toast.makeText(this, "Please enter a valid number for lower and upper temperature limits", Toast.LENGTH_LONG).show();
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

    void retrieveSensorData(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    DecimalFormat df = new DecimalFormat("####0.00");
                    //Temperature
                    TempTextView.setText(df.format(sensorData.getTemperatureC())+"");

                  ghtempDegree = Double.parseDouble(TempTextView.getText().toString());




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);
    }


}

