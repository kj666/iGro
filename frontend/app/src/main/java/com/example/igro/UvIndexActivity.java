package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.text.TextUtils;

import com.example.igro.Controller.Helper;
import com.example.igro.Models.ActuatorControl.ApplianceControlEvents;
import com.example.igro.Models.SensorData.UvRange;
import com.google.firebase.auth.FirebaseAuth;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.igro.Models.SensorData.SensorData;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class UvIndexActivity extends AppCompatActivity {
    private static final String UV_ACTIVITY_LOG_TAG = "UV_ACTIVITY_LOG_TAG";

    TextView outdoorUVTextView;
    private RequestQueue queue;

    LineGraphSeries<DataPoint> series;

    //initialize the layout fields
    EditText lowUvEditText;
    EditText highUvEditText;
    TextView uvControlTextView;
    Switch uvSwitch;
    TextView uvTextView;
    Button uvHistoryButton;
    Button lightUseButton;
    Button setUvRange;
    TextView ghUvTextView;
    Double ghUv;
    private FirebaseUser currentUser;
    private Helper helper = new Helper(this, FirebaseAuth.getInstance());

    public Boolean lastUvState = false;

    //log tag to test the on/off state on changeState event of heaterSwitch
    private static final String TAG = "LightsAreOnTag";

    //create heater database reference for the correct node
    DatabaseReference uvSwitchEventDB, databaseRange, db, appliances;

    public void initializeDB(String greenhouseID){
        databaseRange = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Ranges");
        uvSwitchEventDB = FirebaseDatabase.getInstance().getReference(greenhouseID+"/ApplianceControlLog").child("UVControlLog");
        db = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Data");
        appliances = FirebaseDatabase.getInstance().getReference().child(greenhouseID+"/Appliances");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uv_index);


        initializeUI();
        helper.setSharedPreferences(getApplicationContext());
        initializeDB(helper.retrieveGreenhouseID());

        currentUser = helper.checkAuthentication();
        retrieveSensorData();
        retrieveRange();
        setUvRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUvRange();
            }
        });
        retrieveSensorData();

    }


    @Override
    protected void onStart() {
        super.onStart();
        initializeUI();

        //call function check last child in heaterSwitchEventDB and set switch to that state

        final Boolean switchState = uvSwitch.isChecked();
        uvSwitchStateFromRecord();

        uvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton uvSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                uvSwitchEvent(SwitchState);
            }
        });

        uvHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = UvIndexActivity.this;
                Intent i = new Intent(context, SensorDataActivity.class);
                i.putExtra("SensorType", "UV");
                context.startActivity(i);
            }
        });

        lightUseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = UvIndexActivity.this ;
                Intent i = new Intent(context, HistoricalApplianceActivity.class);
                i.putExtra("ApplianceType", "LIGHTS");
                context.startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        final Boolean switchState = uvSwitch.isChecked();
        uvSwitchStateFromRecord();

        uvSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton uvSwitch, boolean SwitchState){

                //Call heaterSwitchEvent function
                uvSwitchEvent(SwitchState);
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
        }
        return super.onOptionsItemSelected(item);
    }


    //Initialization
    void initializeUI(){
        uvControlTextView = (TextView)findViewById(R.id.uvControlTextView);
        uvSwitch = (Switch)findViewById(R.id.uvSwitch);
        uvSwitch.setClickable(true);

        lowUvEditText = (EditText)findViewById(R.id.lowUvEditText);
        highUvEditText = (EditText)findViewById(R.id.highUvEditText);
        uvTextView = (TextView)findViewById(R.id.ghUvTextView);

        outdoorUVTextView = findViewById(R.id.outdoorUvTextView);
        queue = Volley.newRequestQueue(this);
        requestUVIndex();

        uvHistoryButton = findViewById(R.id.uvHistoryButton);
        lightUseButton = findViewById(R.id.lightUseHistoryButton);
        //indoor uv
        ghUvTextView = (TextView) findViewById(R.id.ghUvTextView);
        //set range button
        setUvRange = (Button) findViewById(R.id.setUvRange);
    }


    void retrieveRange(){
        DatabaseReference uvRange = databaseRange.child("UV");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Double lowRange = Helper.retrieveRange("lowUvValue", dataSnapshot);
                Double highRange = Helper.retrieveRange("highUvValue", dataSnapshot);

                lowUvEditText.setText(lowRange.toString());
                highUvEditText.setText(highRange.toString());
                if (!((ghUv > lowRange)
                        && (ghUv< highRange))) {

                    ghUvTextView.setTextColor(Color.RED);
                    Toast.makeText(UvIndexActivity.this,"THE SENSOR VALUE IS OUT OF THRESHOLD!!!", Toast.LENGTH_LONG).show();
                }
                else{
                    ghUvTextView.setTextColor(Color.GREEN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        uvRange.addValueEventListener(eventListener);

    }


    // set the user input UV range
    public void setUvRange() {

        String lowUv = lowUvEditText.getText().toString();
        String highUv = highUvEditText.getText().toString();
 //check if the ranges are empty or not
            if (!TextUtils.isEmpty(lowUv) && !TextUtils.isEmpty(highUv)) {
 //Check if Lower limit is < upper limit
                if (Double.parseDouble(lowUv) < Double.parseDouble(highUv)) {

                    UvRange UvRange = new UvRange(lowUv, highUv);
                    databaseRange.child("UV").setValue(UvRange);
                    Toast.makeText(this, "RANGE SUCCESSFULLY SET!!!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "Please, make sure the Upper Limit is bigger than the Lower Limit", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "Please set values to your desired Upper and Lower UV Limits", Toast.LENGTH_LONG).show();
            }
    }


    private void uvSwitchStateFromRecord() {

        uvSwitchEventDB.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ApplianceControlEvents lastRecord = dataSnapshot.getValue(ApplianceControlEvents.class);
                assert lastRecord != null;
                final Boolean checkedStatus = lastRecord.getEventOnOff();

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
                if(checkedStatus){
                    Log.d(TAG, "The lights were on");
                }else{
                    Log.d(TAG, "The lights were off");
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

        if(!(uvSwitchState==lastUvState)){

            //generate unique key for each switch, create a new object of HeaterControlEvents, record on/off & date/time in firebase
            String uvEventId = uvSwitchEventDB.push().getKey();


            ApplianceControlEvents uvSwitchClickEvent = new ApplianceControlEvents(uvEventId, uvOnTimeStampFormated, uvOnOffDateUnixFormat, uvSwitchState);
            uvSwitchEventDB.child(uvEventId).setValue(uvSwitchClickEvent);

            if(!(uvEventId == null)) {


                if (!lastUvState) {
                    appliances.child("LightCtrl").setValue(true);
                    Log.d(TAG, "The lights were turned on " + uvOnTimeStampFormated);
                    Toast.makeText(this, "The lights were switched ON on " + uvOnTimeStampFormated, Toast.LENGTH_LONG).show();

                } else {
                    appliances.child("LightCtrl").setValue(false);
                    Log.d(TAG, "Thelights were turned off on " + uvOnTimeStampFormated);
                    Toast.makeText(this, "The lights were switched OFF on " + uvOnTimeStampFormated, Toast.LENGTH_LONG).show();
                }
            }else{
                Log.d(TAG, "ERROR: uvEventId can't be null");

            }

        }


    }

    void retrieveSensorData(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    DecimalFormat df = new DecimalFormat("####0.0");

                    //UVindex
                    uvTextView.setText(df.format(sensorData.getUv())+"");
                    ghUv = Double.parseDouble(uvTextView.getText().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);
    }

    void requestUVIndex() {
        // TODO: 2019-03-18
        // Make this function capable of pulling data for any city as per user request

        // Get weather for Montreal
        String url = "https://api.openweathermap.org/data/2.5/uvi?lat=45&lon=-73&appid=b4840319c97c4629912dc391ed164bcb";
        // Make request
        JsonObjectRequest weatherRequest = new JsonObjectRequest(
                Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(UV_ACTIVITY_LOG_TAG, "response: " + response);
                        try {
                            // Get description from weather response
                            //String description = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            //descriptionTextView.setText(description);

                            // Get temperature from weather response
                            Double uv = response.getDouble("value");
                            outdoorUVTextView.setText(uv.toString());

                        } catch (Exception e) {
                            Log.w(UV_ACTIVITY_LOG_TAG, "Attempt to parse JSON Object failed");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(UV_ACTIVITY_LOG_TAG, "JSON Request has failed");
                    }
                });
        queue.add(weatherRequest);
    }
    public void openDialog(){
        PollingFrequencyDialogFragment dialog = new PollingFrequencyDialogFragment();
        dialog.show(getSupportFragmentManager(), "Polling dialog");
    }

}


