package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igro.Controller.Helper;
import com.example.igro.Models.SensorData.SensorData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SensorDataActivity extends AppCompatActivity {

    //UI components
    TextView historicalSensorDataTextView;
    ListView listView;

    //Array of data
    List<SensorData> sensorDataList = new ArrayList<>();

    //boolean to decide if its table/graph
    boolean tableMode = false;
    String sensorType;

    //Fragments
    SensorGraphFragment sensorGraphFragment;
    SensorDataTableFragment sensorDataTableFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        fragmentManager =  getSupportFragmentManager();

        historicalSensorDataTextView = (TextView) findViewById(R.id.historicalSensorDataTextView);

        Intent intent = getIntent();
        sensorType = intent.getStringExtra("SensorType");
        String pageTitle = "HISTORICAL " + sensorType + " SENSOR DATA";

        createGraphFrag(sensorType);

        historicalSensorDataTextView.setText(pageTitle);

        if(sensorType.equals("TEMPERATURE")){

        } else if (sensorType.equals("HUMIDITY")) {

        }else if(sensorType.equals("MOISTURE")){

        }else if(sensorType.equals("UV")){

        }else{
            Toast.makeText(this, "ERROR: unKnown sensor type ", Toast.LENGTH_LONG ).show();
        }

    }
    void createGraphFrag(String type){
        sensorGraphFragment = SensorGraphFragment.newInstance(type);
        fragmentTransaction = fragmentManager.beginTransaction().add(R.id.fragmentContainer, sensorGraphFragment);
        fragmentTransaction.addToBackStack("graph");
        fragmentTransaction.commit();
    }

    void removeGraphFrag(){
        fragmentManager.popBackStack("graph",1);
    }

    void createTableFrag(String type){
        sensorDataTableFragment = SensorDataTableFragment.newInstance(type);
        fragmentTransaction = fragmentManager.beginTransaction().add(R.id.fragmentContainer, sensorDataTableFragment);
        fragmentTransaction.addToBackStack("table");
        fragmentTransaction.commit();
    }

    void removeTableFrag(){
        fragmentManager.popBackStack("table",1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.graph_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.tableGraph_switch:
                if(tableMode) {
                    tableMode = false;
                    item.setChecked(false);
                    removeTableFrag();
                    createGraphFrag(sensorType);

                }
                else {
                    tableMode = true;
                    item.setChecked(true);
                    removeGraphFrag();
                    createTableFrag(sensorType);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
