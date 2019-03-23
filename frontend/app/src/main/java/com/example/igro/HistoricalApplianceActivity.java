package com.example.igro;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igro.Models.ActuatorControl.HeaterControlEvents;
import com.example.igro.Models.ActuatorControl.HeaterEventsListConfig;
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
    TextView listOnOffTitleTextView;
    ListView applianceEventListView;

    private static final String PARAM = "type";
    private static Bundle extra = new Bundle();
    private static String extraStr;

    List<HeaterControlEvents> applianceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_appliance_data);

        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listDateTitleTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);



        Intent intent = getIntent();
        final String userName = intent.getStringExtra("UserName");
        final String applianceType = intent.getStringExtra("ApplianceType");
        extra = intent.getExtras();
        extraStr = intent.getExtras().toString();
        final String pageTitle = "HISTORICAL " + applianceType + " ON/OFF EVENTS";

        historicalApplianceTitleTextView.setText(pageTitle);

        Bundle passData = new Bundle();
        passData.putString(PARAM, applianceType);

    }


    @Override
    protected void onStart() {
        super.onStart();


        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listDateTitleTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("UserName");
        String applianceType = intent.getStringExtra("ApplianceType");
        String pageTitle = "HISTORICAL " + applianceType + " ON/OFF EVENTS";

        extra = intent.getExtras();
        extraStr = intent.getExtras().toString();

        historicalApplianceTitleTextView.setText(pageTitle);
// depending on where the intent comes from, the extra determines which appliance data to load.
        //if the intent extra comes from Temperature Activity, load heater data
        if(applianceType.equals("HEATER")){
            loadHeaterOnOffList();
        } else if (applianceType.equals("HUMIDIFIER")) {
            //todo change
            loadHumidityOnOffList();

        }else if(applianceType.equals("IRRIGATION")){
            //todo change
            loadIrrigationOnOffList();

        }else if(applianceType.equals("LIGHTS")){
            //todo change
            loadLightsOnOffList();
        }else{
            Toast.makeText(this, "ERROR: unKnown appliance type ", Toast.LENGTH_LONG ).show();
        }


    }

    protected void loadHeaterOnOffList() {

        final DatabaseReference heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("HeaterControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<HeaterControlEvents> heaterList = new ArrayList<>();


 //   fuction orders the db entries by key and limits to last 20 entries to display

            heaterSwitchEventDB.orderByKey().limitToLast(20).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    long records = dataSnapshot.getChildrenCount();

                  for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                        HeaterControlEvents heaterEvent = heaterEventSnapshot.getValue(HeaterControlEvents.class);
                        heaterList.add(heaterEvent);

                    }

                    HeaterEventsListConfig adapter = new HeaterEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                  applianceEventListView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }



    protected void loadHumidityOnOffList() {

        final DatabaseReference humidSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("HumidityControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<HeaterControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        humidSwitchEventDB.orderByChild("eventUnixEpoch").limitToLast(20).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                    HeaterControlEvents heaterEvent = heaterEventSnapshot.getValue(HeaterControlEvents.class);
                    heaterList.add(heaterEvent);

                }

                HeaterEventsListConfig adapter = new HeaterEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                applianceEventListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    protected void loadIrrigationOnOffList() {

        final DatabaseReference moistSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("SoilMoistureControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<HeaterControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        moistSwitchEventDB.orderByKey().limitToLast(20).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                    HeaterControlEvents heaterEvent = heaterEventSnapshot.getValue(HeaterControlEvents.class);
                    heaterList.add(heaterEvent);

                }

                HeaterEventsListConfig adapter = new HeaterEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                applianceEventListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    protected void loadLightsOnOffList() {

        final DatabaseReference uvSwitchEventDB = FirebaseDatabase.getInstance().getReference("ApplianceControlLog").child("UVControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

// creates a new array list of the Class Heater Control Events which will be populated by records
        final List<HeaterControlEvents> heaterList = new ArrayList<>();


        //   fuction orders the db entries by key and limits to last 20 entries to display

        uvSwitchEventDB.orderByChild("uvEventUnixEpoch").limitToLast(20).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                    HeaterControlEvents heaterEvent = heaterEventSnapshot.getValue(HeaterControlEvents.class);
                    heaterList.add(heaterEvent);

                }

                HeaterEventsListConfig adapter = new HeaterEventsListConfig(HistoricalApplianceActivity.this, heaterList);
                applianceEventListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
