package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
    GraphView graphView;
    ListView listView;

    ConstraintLayout constraintLayout;
    //Graph
    LineGraphSeries<DataPoint> series;
    //Array of data
    List<SensorData> sensorDataList = new ArrayList<>();

    //boolean to decide if its table/graph
    boolean tableMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        historicalSensorDataTextView = (TextView) findViewById(R.id.historicalSensorDataTextView);

        retrieveSensorDataFromDB();


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
                    setupGraphUI();
                }
                else {
                    tableMode = true;
                    item.setChecked(true);
                    setupTableUI();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

    void initializeGraphUI(){
        graphView = (GraphView) findViewById(R.id.sensorGraph);
    }

    void initializeTableUI(){
        listView = findViewById(R.id.sensorDataTable);
    }

    void setupGraphUI(){
        constraintLayout = (ConstraintLayout) findViewById(R.id.sensorHistoryConstraintLayout);
        graphView = new GraphView(getApplicationContext());
        constraintLayout.removeView(graphView);
        populateGraph();
    }

    void setupTableUI(){

    }

    /**
     * Populate with data
     */
    void populateGraph(){
        initializeGraphUI();

        series = new LineGraphSeries<>();

        for(SensorData data: sensorDataList){
            long t = data.getTime();
            Date time = new Date(t);

            Log.d("FIREBASE", data.getTime()+"");

            double y = data.getTemperatureC();
            series.appendData(new DataPoint(time.getTime(),y), true, 500);
        }
        graphView.addSeries(series);

        //Use time as x-axis
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplication(),new SimpleDateFormat("dd-MM HH:mm")));

        //make the graph scrollable and scalable
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalable(true);
    }

    void populateTable(){

        initializeTableUI();
        SensorDataListAdapter sensorDataListAdapter = new SensorDataListAdapter(sensorDataList);
        listView.setAdapter(sensorDataListAdapter);
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
                populateTable();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SensorDataActivity.this, "Failed to loead",Toast.LENGTH_SHORT).show();
            }
        };

        db.addValueEventListener(eventListener);
    }

    //Used for the listView
    class SensorDataListAdapter extends BaseAdapter{

        private List<SensorData> sensorDataList;

        public SensorDataListAdapter(List<SensorData> sensorDataList) {
            this.sensorDataList = sensorDataList;
        }

        @Override
        public int getCount() {
            return sensorDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.sensor_data_list_view,null);
            TextView sensorDate = convertView.findViewById(R.id.sensorDateTextView);
            TextView sensorData = convertView.findViewById(R.id.sensorDataTextView);

            sensorDate.setText(Helper.convertTime(sensorDataList.get(position).getTime()));
            sensorData.setText(sensorDataList.get(position).getTemperatureC()+"");

            return convertView;
        }
    }
}
