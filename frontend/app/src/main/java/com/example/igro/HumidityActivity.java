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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.igro.Models.ActuatorControl.HumidControlEvents;
import com.example.igro.Models.SensorData.SensorData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

// TODO 2019-03-20
// use weather api to collect humidity information

public class HumidityActivity extends AppCompatActivity {
    private static final String HUMIDITY_LOG_TAG = "HUMID_ACTIVITY_LOG_TAG";


    //initialize the layout fields
    EditText lowHumEditText;
    EditText highHumEditText;
    TextView humTextView;
    TextView humControlTextView;
    Switch humSwitch;
    Button humidityHistoryButton;
    Button humidifierUseButton;

    public Boolean lastHumidState = false;

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "HumidifyerIsOnTag";

    //create heater database reference
    DatabaseReference humidSwitchEventDB = FirebaseDatabase.getInstance().getReference("HumidControlLog");

    TextView outdoorHumidityTextView; // displays the humidity in percentage
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humidity);

        inititalizeUI();

        retrieveSensorData();

        queue = Volley.newRequestQueue(this);
        requestHumidity();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        inititalizeUI();

        String lowHumLimit = lowHumEditText.getText().toString();
        String highHumLimit = lowHumEditText.getText().toString();

        if ((lowHumLimit.matches(".*[0-9].*")) && (highHumLimit.matches(".*[0-9].*"))) {
            int lowHum = parseInt(lowHumLimit);
            int highHum = parseInt(highHumLimit);
        } else {
            Toast.makeText(this, "Please enter a valid number for lower and upper humidity limits", Toast.LENGTH_LONG).show();
        }

        //call function check last child in heaterSwitchEventDB and set switch to that state

        final boolean switchState = humSwitch.isChecked();
        humidSwitchStateFromRecord();

        humSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton humSwitch, boolean SwitchState) {

                //Call heaterSwitchEvent function
                humidSwitchEvent(SwitchState);

            }
        });

        humidityHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = HumidityActivity.this;
                Intent i = new Intent(context, SensorDataActivity.class);
                i.putExtra("SensorType", "HUMIDITY");
                context.startActivity(i);
            }
        });

        humidifierUseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = HumidityActivity.this ;
                Intent i = new Intent(context, HistoricalApplianceActivity.class);
                i.putExtra("ApplianceType", "HUMIDIFIER");
                context.startActivity(i);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        //call function check last child in heaterSwitchEventDB and set switch to that state

        final boolean switchState = humSwitch.isChecked();
        humidSwitchStateFromRecord();

        if (switchState) {
            Log.d(TAG, "The heater was on");
        } else {
            Log.d(TAG, "The heater was off");
        }

        // Listen for change in switch status
        humSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton humSwitch, boolean SwitchState) {

                //Call heaterSwitchEvent function
                humidSwitchEvent(SwitchState);
            }
        });
    }

    void inititalizeUI(){
        humControlTextView = (TextView) findViewById(R.id.humControlTextView);
        humSwitch = (Switch) findViewById(R.id.humSwitch);
        humSwitch.setClickable(true);
        humTextView = (TextView) findViewById(R.id.ghHumTextView);
        lowHumEditText = (EditText) findViewById(R.id.lowHumEditText);
        highHumEditText = (EditText) findViewById(R.id.highHumEditText);

        outdoorHumidityTextView = findViewById(R.id.outdoorHumTextView);

        humidityHistoryButton = findViewById(R.id.humidityHistoryButton);
        humidifierUseButton = findViewById(R.id.humidifierUseHistoryButton);
    }

    private void humidSwitchStateFromRecord() {

        humidSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HumidControlEvents lastRecord = dataSnapshot.getValue(HumidControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getHumidEventOnOff();

                if (!(checkedStatus == null)) {

                    lastHumidState = checkedStatus;
                    humSwitch.setChecked(checkedStatus);
                    if (checkedStatus) {
                        humSwitch.setTextColor(Color.RED);
                    } else {
                        humSwitch.setTextColor(Color.DKGRAY);
                    }

                } else {

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

        long humOnOffDateUnixFormat = System.currentTimeMillis() / 1000;

        String humOnOffDateReadable = new java.text.SimpleDateFormat("MM/dd/yy HH:mm:ss").format(new java.util.Date(humOnOffDateUnixFormat * 1000));

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String humOnTimeStampFormated = df.format(Calendar.getInstance().getTime());

        if (!(humSwitchState == lastHumidState)) {

            //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
            String humEventId = humidSwitchEventDB.push().getKey();

            HumidControlEvents humSwitchClickEvent = new HumidControlEvents(humEventId, humOnTimeStampFormated, humOnOffDateUnixFormat, humSwitchState);
            humidSwitchEventDB.child(humEventId).setValue(humSwitchClickEvent);

            if (!(humEventId == null)) {

                if (lastHumidState) {

                    Log.d(TAG, "The humidifier was turned on " + humOnTimeStampFormated);
                    Toast.makeText(this, "The humidifier was switched ON on " + humOnTimeStampFormated, Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "The humidifier was turned off on " + humOnTimeStampFormated);
                    Toast.makeText(this, "The humidifier was switched OFF on " + humOnTimeStampFormated, Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d(TAG, "ERROR: humEventId can't be null");

            }

        }

    }

    void retrieveSensorData() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    SensorData sensorData = snap.getValue(SensorData.class);
                    DecimalFormat df = new DecimalFormat("####0.00");


                    //Humidity
                    humTextView.setText(df.format(sensorData.getHumidity()) + "");



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);
    }

    void requestHumidity() {
        // TODO: 2019-03-18
        // Make this function capable of pulling data for any city as per user request

        // Get weather for Montreal
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Montreal&units=metric&APPID=b4840319c97c4629912dc391ed164bcb";
        // Make request
        JsonObjectRequest humidityRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(HUMIDITY_LOG_TAG, "response: " + response);
                        try {
                            // Get description from weather response
                            //String description = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            //descriptionTextView.setText(description);

                            // Get temperature from weather response
                            Integer humidity = response.getJSONObject("main").getInt("humidity");
                            outdoorHumidityTextView.setText(humidity.toString());

                        } catch (Exception e) {
                            Log.w(HUMIDITY_LOG_TAG, "Attempt to parse JSON Object failed");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(HUMIDITY_LOG_TAG, "JSON Request has failed");
                    }
                });
        queue.add(humidityRequest);
    }
}

