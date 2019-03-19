package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

    TextView historicalSensorDataTextView;
    GraphView graphView;

    LineGraphSeries<DataPoint> series;

    List<SensorData> sensorDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        retrieveSensorDataFromDB();
        initializeGraphUI();


        Intent intent = getIntent();
        String sensorType = intent.getStringExtra("SensorType");
        String pageTitle = "HISTORICAL " + sensorType + " SENSOR DATA";

        historicalSensorDataTextView.setText(pageTitle);

        if(sensorType=="TEMPERATURE"){

        } else if (sensorType == "HUMIDITY") {

        }else if(sensorType=="MOISTURE"){

        }else if(sensorType=="UV"){

        }else{
            Toast.makeText(this, "ERROR: unKnown sensor type ", Toast.LENGTH_LONG ).show();
        }

    }

    void initializeGraphUI(){
        historicalSensorDataTextView = (TextView) findViewById(R.id.historicalSensorDataTextView);
        graphView = (GraphView) findViewById(R.id.sensorGraph);
    }

    void initializeTableUI(){

    }

    void setupGraphUI(){

    }

    void setupTableUI(){

    }

    /**
     * Populate with data
     */
    void populateGraph(){
        series = new LineGraphSeries<>();

        for(SensorData data: sensorDataList){
            long t = data.getTime();
            Date time = new Date(t*1000L);

            Log.d("FIREBASE", data.getTime()+"");

            double y = data.getTemperatureC();
            series.appendData(new DataPoint(time.getTime(),y), true, 500);
        }
        graphView.addSeries(series);
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplication()));

        //make the graph scrollable and scalable
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalable(true);
    }

    /**
     * Retrieve sensor data from Firebase database
     */
    void retrieveSensorDataFromDB(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    Log.d("FIREBASE", sensorData.getTime()+"");
                    sensorDataList.add(sensorData);
                }

                populateGraph();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SensorDataActivity.this, "Failed to loead",Toast.LENGTH_SHORT).show();
            }
        };

        db.addValueEventListener(eventListener);

    }
}
