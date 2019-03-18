package com.example.igro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igro.Models.ActuatorControl.HeaterControlEvents;
import com.example.igro.Models.ActuatorControl.HeaterEventConfig;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.example.igro.R.layout.appliance_trigger_list_layout;

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
    private Activity context;
    private ArrayList<String> heaterEventArray;
    private List<String> heaterEventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_appliance_data);

        historicalApplianceTitleTextView = (TextView)findViewById(R.id.historicalApplianceTitleTextView);
        listCounterTitleTextView = (TextView)findViewById(R.id.listItemCounterTextView);
        listDateTitleTextView = (TextView)findViewById(R.id.listDateTitleTextView);
        listOnOffTitleTextView = (TextView)findViewById(R.id.listItemOnOffStatusTextView);
        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

        heaterEventArray = new ArrayList<>();
        heaterEventArray = new ArrayList<>();

        Intent intent = getIntent();
        String userName = intent.getStringExtra("UserName");
        String applianceType = intent.getStringExtra("ApplianceType");
        String pageTitle = "HISTORICAL " + applianceType + " ON/OFF EVENTS";

        historicalApplianceTitleTextView.setText(pageTitle);

        if(applianceType=="HEATER"){
            loadHeaterOnOffList();
        } else if (applianceType == "HUMIDIFIER") {

        }else if(applianceType=="IRRIGATION"){

        }else if(applianceType=="LIGHTS"){

        }else{
            Toast.makeText(this, "ERROR: unKnown appliance type ", Toast.LENGTH_LONG ).show();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();


        loadHeaterOnOffList();

    }

    protected void loadHeaterOnOffList() {

        final DatabaseReference heaterSwitchEventDB = FirebaseDatabase.getInstance().getReference("HeaterControlLog");

        applianceEventListView = (ListView)findViewById(R.id.applianceEventListView);

        final List<HeaterControlEvents> heaterList = new ArrayList<>();
        final List<String> heaterListStrings = new ArrayList<>();


 //     final int i = 0;
 //      while (i < 20) {

      //      heaterSwitchEventDB.orderByKey().limitToLast(1 + i).addValueEventListener(new ValueEventListener() {

            heaterSwitchEventDB.orderByKey().limitToLast(20).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    long records = dataSnapshot.getChildrenCount();

                  for(DataSnapshot heaterEventSnapshot : dataSnapshot.getChildren()){

                        HeaterControlEvents heaterEvent = heaterEventSnapshot.getValue(HeaterControlEvents.class);
                        heaterList.add(heaterEvent);

                    }

                    HeaterEventConfig adapter = new HeaterEventConfig(HistoricalApplianceActivity.this, heaterList);
                  applianceEventListView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }


}
