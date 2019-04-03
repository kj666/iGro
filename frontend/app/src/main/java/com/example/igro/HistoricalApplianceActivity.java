package com.example.igro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igro.Controller.Helper;
import com.example.igro.Models.ActuatorControl.ApplianceControlEvents;
import com.example.igro.Models.ActuatorControl.ApplianceEventsListConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoricalApplianceActivity extends AppCompatActivity {


    private TemperatureActivity tempContext;
    private HumidityActivity humContext;
    private MoistureActivity moistContext;
    private UvIndexActivity uvContext;

    TextView historicalApplianceTitleTextView;
    TextView listCounterTitleTextView;
    TextView listDateTitleTextView;
    TextView listUserNameTitleTextView;
    TextView listTimeSinceLastTriggerTitleTextView;
    TextView listOnOffTitleTextView;
    ListView applianceEventListView;
    TextView recordLimitTitleTextView;
    EditText recordLimitEditText;
    Button refreshButton;

    private Helper helper = new Helper(this, FirebaseAuth.getInstance());

    List<ApplianceControlEvents> applianceList = new ArrayList<>();
    protected Integer recordNumberEntered = 20;
    protected String applianceTypePassed;
    public Integer recordsEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_appliance_data);
        helper.setSharedPreferences(getApplicationContext());

//initialization for all the fields
        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listItemDateTextView);
        listUserNameTitleTextView = findViewById(R.id.listItemUserNameTextView);
        listTimeSinceLastTriggerTitleTextView = findViewById(R.id.listItemTimeSinceLastTriggerTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);
        recordLimitTitleTextView = (TextView)findViewById(R.id.recordLimitTitleTextView);
        recordLimitEditText = (EditText)findViewById(R.id.recordLimitEditText);
        refreshButton = (Button)findViewById(R.id.refreshButton);

//getting intent and retrieving the extra
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("UserName");
        final String applianceType = intent.getStringExtra("ApplianceType");
        applianceTypePassed = applianceType;
// setting the title based on which Appliance data will be displayed based on intent extra
        final String pageTitle = "HISTORICAL " + applianceType + " ON/OFF EVENTS";
        historicalApplianceTitleTextView.setText(pageTitle);

        // store appliancetype in shared preferences file
        SharedPreferences applianceTypeSharedPrefs = getSharedPreferences(getString(R.string.ApplianceTypeSharedPrefsFile), MODE_PRIVATE);
        SharedPreferences.Editor editor = applianceTypeSharedPrefs.edit();
        editor.putString(getString(R.string.ApplianceTypePassed), applianceType);
        editor.apply();

        // depending on where the intent comes from, the extra determines which appliance data to load.
        loadApplianceListByApplianceType(recordNumberEntered);

        //check for number of records entered by the user
        recordLimitEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recordNumberEnteredStr = recordLimitEditText.getText().toString();

                // check the number of records entered by the user & set to record Number Entered
                recordNumberEntered = numberOfRecordsEntered(recordNumberEnteredStr);
            }
        });


// reload when refresh button is hit
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the user input limit, check if the input is numerical;...
                String recordNumberEnteredStr = recordLimitEditText.getText().toString();
                recordNumberEntered = numberOfRecordsEntered(recordNumberEnteredStr);

                // depending on where the intent comes from, the extra determines which appliance data to load.
                //if the intent extra comes from Temperature Activity, load heater data
                loadApplianceListByApplianceType(recordNumberEntered);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

//initializations
        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listItemDateTextView);
        listUserNameTitleTextView = findViewById(R.id.listItemUserNameTextView);
        listTimeSinceLastTriggerTitleTextView = findViewById(R.id.listItemTimeSinceLastTriggerTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);
        recordLimitTitleTextView = (TextView)findViewById(R.id.recordLimitTitleTextView);
        recordLimitEditText = (EditText)findViewById(R.id.recordLimitEditText);
        refreshButton = (Button)findViewById(R.id.refreshButton);

        //check for number of records entered by the user
        recordLimitEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retreave user record limit as string
                String recordNumberEnteredStr = recordLimitEditText.getText().toString();
                // check the number of records entered by the user & set to record Number Entered
                recordNumberEntered = numberOfRecordsEntered(recordNumberEnteredStr);
            }
        });


// reload when refresh button is hit
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the user input limit, check if the input is numerical;...
                String recordNumberEnteredStr = recordLimitEditText.getText().toString();
                recordNumberEntered = numberOfRecordsEntered(recordNumberEnteredStr);

                // depending on where the intent comes from, the extra determines which appliance data to load.
                //if the intent extra comes from Temperature Activity, load heater data
                loadApplianceListByApplianceType(recordNumberEntered);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //initializations
        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listItemDateTextView);
        listUserNameTitleTextView = findViewById(R.id.listItemUserNameTextView);
        listTimeSinceLastTriggerTitleTextView = findViewById(R.id.listItemTimeSinceLastTriggerTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);
        recordLimitTitleTextView = (TextView)findViewById(R.id.recordLimitTitleTextView);
        recordLimitEditText = (EditText)findViewById(R.id.recordLimitEditText);
        refreshButton = (Button)findViewById(R.id.refreshButton);

//getting recorded type of appliance from shared prefs

        loadApplianceTypeFromSharedPrefs();

        //check for number of records entered by the user
        recordLimitEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recordNumberEnteredStr = recordLimitEditText.getText().toString();
                numberOfRecordsEntered(recordNumberEnteredStr);
            }
        });


// reload when refresh button is hit
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the user input limit, check if the input is numerical;...
                String recordNumberEnteredStr = recordLimitEditText.getText().toString();
                recordNumberEntered = numberOfRecordsEntered(recordNumberEnteredStr);

                // depending on where the intent comes from, the extra determines which appliance data to load.
                //if the intent extra comes from Temperature Activity, load heater data
                loadApplianceListByApplianceType(recordNumberEntered);
            }
        });
    }


    // calls appliance record list function definition

    protected void errorToast(){
        Toast.makeText(this, "ERROR: unKnown appliance type ", Toast.LENGTH_LONG).show();
    }
    protected void numberOfRecordsError(){
        Toast.makeText(this, "ERROR: The number of records cannot be more than 1000", Toast.LENGTH_LONG).show();
    }
    protected void numericalError(){
        Toast.makeText(this, "ERROR: The record limit input must be numerical!", Toast.LENGTH_LONG).show();
    }

    //load appliance list based on intent that was passed
    protected void loadApplianceListByApplianceType(int records){
        //if the intent extra comes from Temperature Activity, load heater data
        switch (applianceTypePassed) {
            case "HEATER":
                loadHeaterOnOffList(records);
                break;
            case "HUMIDIFIER":
                //retrieves the humidifier historical trigger records
                loadHumidityOnOffList(records);

                break;
            case "IRRIGATION":
                //gets irrigation historical trigger records
                loadIrrigationOnOffList(records);

                break;
            case "LIGHTS":
                //loads artificial lights on/off trigger records
                loadLightsOnOffList(records);
                break;
            default:
                Toast.makeText(this, "ERROR: unKnown appliance type ", Toast.LENGTH_LONG).show();
                break;
        }
    }

    protected void loadApplianceTypeFromSharedPrefs(){
        //getting recorded type of appliance from shared prefs
        SharedPreferences applianceTypeSharedPrefs = getSharedPreferences(getString(R.string.ApplianceTypeSharedPrefsFile), MODE_PRIVATE);
        String applianceTypeFromSharedPrefs = applianceTypeSharedPrefs.getString(getString(R.string.ApplianceTypePassed), null);
        loadApplianceListByApplianceType(recordNumberEntered);
    }

    // function definition for number of records entered by the user
    protected int numberOfRecordsEntered(String recordsEnteredStr) {

        //check if the input is are empty or not
        if (!TextUtils.isEmpty(recordsEnteredStr)) {
            //theck if input is numerical
            if (recordsEnteredStr.matches(".*[0-999].*")) {
                //Check if Lower limit is < upper limit
                if (Integer.parseInt(recordsEnteredStr) < 1000) {
                    recordNumberEntered = Integer.parseInt(recordsEnteredStr);

                } else {
                    numberOfRecordsError();
                }

            } else {
                numericalError();
            }

        } else {
            recordNumberEntered = 20;
        }
        return recordNumberEntered;
    }

    // function definition for heater records
    protected void loadHeaterOnOffList(int numberOfRecords) {
// referrence the correct DB node
        final DatabaseReference heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference(helper.retrieveGreenhouseID()+"/ApplianceControlLog").child("HeaterControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();

        //   fuction orders the db entries by key and limits to last 20 entries to display
        heaterSwitchEventDB.orderByKey().limitToLast(numberOfRecords).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//creates a snapshot of the last 20 node entries
                long records = dataSnapshot.getChildrenCount();

                for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){
//retrieves each of the 20 nodes of object of HeaterControlEvents class to build the array
                    ApplianceControlEvents heaterEvent = heaterEventSnapshot.getValue(ApplianceControlEvents.class);

                    heaterList.add(heaterEvent);

                }
// calls the array adapter to display the list of records
                ApplianceEventsListConfig adapter = new ApplianceEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                applianceEventListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




    //function definition to load humidifier trigger records
    protected void loadHumidityOnOffList(int numberOfRecords) {

        final DatabaseReference humidSwitchEventDB = FirebaseDatabase.getInstance().getReference(helper.retrieveGreenhouseID()+"/ApplianceControlLog").child("HumidityControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        humidSwitchEventDB.orderByChild("eventUnixEpoch").limitToLast(numberOfRecords).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                    ApplianceControlEvents heaterEvent = heaterEventSnapshot.getValue(ApplianceControlEvents.class);
                    heaterList.add(heaterEvent);

                }

                ApplianceEventsListConfig adapter = new ApplianceEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                applianceEventListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    // function definition for irrigation control records
    protected void loadIrrigationOnOffList(int numberOfRecords) {

        final DatabaseReference moistSwitchEventDB = FirebaseDatabase.getInstance().getReference(helper.retrieveGreenhouseID()+"/ApplianceControlLog").child("SoilMoistureControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        moistSwitchEventDB.orderByKey().limitToLast(numberOfRecords).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                    ApplianceControlEvents heaterEvent = heaterEventSnapshot.getValue(ApplianceControlEvents.class);
                    heaterList.add(heaterEvent);

                }

                ApplianceEventsListConfig adapter = new ApplianceEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                applianceEventListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    // function definition for loading the table of lights control records
    protected void loadLightsOnOffList(int numberOfRecords) {

        final DatabaseReference uvSwitchEventDB = FirebaseDatabase.getInstance().getReference(helper.retrieveGreenhouseID()+"/ApplianceControlLog").child("UVControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        uvSwitchEventDB.orderByChild("uvEventUnixEpoch").limitToLast(numberOfRecords).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                    ApplianceControlEvents heaterEvent = heaterEventSnapshot.getValue(ApplianceControlEvents.class);

                    heaterList.add(heaterEvent);

                }

                ApplianceEventsListConfig adapter = new ApplianceEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                applianceEventListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

// set up menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.settings:
                helper.goToActivity(SettingsActivity.class);
                return true;
            case R.id.about:
                helper.goToActivity(AboutActivity.class);
                return true;
            case R.id.sign_out:
                helper.signout();
                helper.goToActivity(LoginActivity.class);
                return true;
            case R.id.polling_menu:
                openDialog();
                return true;
            case R.id.changePassword:
                changePasswordDialog();
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    // dialog to display the polling dialog
    public void openDialog(){
        PollingFrequencyDialogFragment dialog = new PollingFrequencyDialogFragment();
        dialog.show(getSupportFragmentManager(), "Polling dialog");
    }
    // dialog to display the change password fragment
    public void changePasswordDialog(){

        ChangePasswordDialogFragment changePassword=new ChangePasswordDialogFragment();
        changePassword.show(getSupportFragmentManager(),"Change Password dialog");
    }

}
