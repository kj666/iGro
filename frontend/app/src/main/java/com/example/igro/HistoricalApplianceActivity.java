package com.example.igro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igro.Models.ActuatorControl.ApplianceControlEvents;
import com.example.igro.Models.ActuatorControl.ApplianceEventsListConfig;
import com.google.firebase.auth.FirebaseUser;
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
    TextView listOnOffTitleTextView;
    ListView applianceEventListView;

    private FirebaseUser currentUser;
    String currentUserName;
    String currentUserId;


    List<ApplianceControlEvents> applianceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_appliance_data);

//initialization for all the fields
        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listItemDateTextView);
        listUserNameTitleTextView = (TextView)findViewById(R.id.listItemUserNameTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);



//getting intent and retrieving the extra
        Intent intent = getIntent();
        final String userName = intent.getStringExtra("UserName");
        final String applianceType = intent.getStringExtra("ApplianceType");
// setting the title based on which Appliance data will be displayed based on intent extra
        final String pageTitle = "HISTORICAL " + applianceType + " ON/OFF EVENTS";
        historicalApplianceTitleTextView.setText(pageTitle);

    }


    @Override
    protected void onStart() {
        super.onStart();

//initializations
        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listItemDateTextView);
        listUserNameTitleTextView = (TextView)findViewById(R.id.listItemUserNameTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

//getting intent
        Intent intent = getIntent();
        String userAccessingRecords = intent.getStringExtra("CurrentUser");
        String applianceType = intent.getStringExtra("ApplianceType");
        String pageTitle = "HISTORICAL " + applianceType + " ON/OFF EVENTS";

        historicalApplianceTitleTextView.setText(pageTitle);

// depending on where the intent comes from, the extra determines which appliance data to load.
        //if the intent extra comes from Temperature Activity, load heater data
        if(applianceType.equals("HEATER")){
            loadHeaterOnOffList();
        } else if (applianceType.equals("HUMIDIFIER")) {
            //retrieves the humidifier historical trigger records
            loadHumidityOnOffList();

        }else if(applianceType.equals("IRRIGATION")){
            //gets irrigation historical trigger records
            loadIrrigationOnOffList();

        }else if(applianceType.equals("LIGHTS")){
            //loads artificial lights on/off trigger records
            loadLightsOnOffList();
        }else{
            Toast.makeText(this, "ERROR: unKnown appliance type ", Toast.LENGTH_LONG ).show();
        }

    }

    // function definition for heater records
    protected void loadHeaterOnOffList() {
// referrence the correct DB node
        final DatabaseReference heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("HeaterControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();

        //   fuction orders the db entries by key and limits to last 20 entries to display
        heaterSwitchEventDB.orderByKey().limitToLast(20).addValueEventListener(new ValueEventListener() {

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
    protected void loadHumidityOnOffList() {

        final DatabaseReference humidSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("HumidityControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        humidSwitchEventDB.orderByChild("eventUnixEpoch").limitToLast(20).addValueEventListener(new ValueEventListener() {

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
    protected void loadIrrigationOnOffList() {

        final DatabaseReference moistSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("SoilMoistureControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        moistSwitchEventDB.orderByKey().limitToLast(20).addValueEventListener(new ValueEventListener() {

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
    protected void loadLightsOnOffList() {

        final DatabaseReference uvSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("UVControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<ApplianceControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        uvSwitchEventDB.orderByChild("uvEventUnixEpoch").limitToLast(20).addValueEventListener(new ValueEventListener() {

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


}
