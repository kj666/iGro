package com.example.igro;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.igro.Controller.Helper;
import com.example.igro.Models.SensorData.SensorDataValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SensorGraphFragment extends Fragment {
    private static final String PARAM = "type";

    private Helper helper = new Helper(getContext(), FirebaseAuth.getInstance());
    private String sensorType;
    private int dataLimit;
    GraphView graphView;
    String xAxisTitle;

    TextView sensorTypeTextView;
    private List<SensorDataValue> sensorDataList = new ArrayList<>();
    DatabaseReference db;

    ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            for(DataSnapshot snap : dataSnapshot.getChildren()){
                SensorDataValue sensorDataValue = snap.getValue(SensorDataValue.class);
                sensorDataList.add(sensorDataValue);
            }
            populateGraph();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };

    public SensorGraphFragment() {
        // Required empty public constructor
    }

    public static SensorGraphFragment newInstance(String sensorType, int dataLimit){
        SensorGraphFragment fragment = new SensorGraphFragment();
        Bundle passData = new Bundle();
        passData.putString(PARAM, sensorType);
        fragment.sensorType = sensorType;
        fragment.dataLimit = dataLimit;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor_graph, container, false);
        graphView = (GraphView) view.findViewById(R.id.sensorDataGraph);

        sensorTypeTextView = view.findViewById(R.id.graphTypeTextView);
        sensorTypeTextView.setText(sensorType);

        helper.setSharedPreferences(getContext());
        db = FirebaseDatabase.getInstance().getReference().child(helper.retrieveGreenhouseID()+"/Data");
        retrieveSensorDataFromDB();

        return view;
    }

    /**
     * Populate with data
     */
    void populateGraph(){

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for(SensorDataValue data: sensorDataList){
            long t = data.getTime();
            Date time = new Date(t);

            Double y = data.getValue();
            if (helper.retrieveTemperatureMetric() == false &&
                    sensorType.equals("TEMPERATURE-F")) { //convert to fahrenheit
                y = Double.parseDouble(helper.celsiusFahrenheitConversion(y.toString()));
            }
            graphView.getGridLabelRenderer().setVerticalAxisTitle(xAxisTitle);
            series.appendData(new DataPoint(time.getTime(),y), true, 500);
        }
        graphView.addSeries(series);

        //Use time as x-axis
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getContext(),new SimpleDateFormat("dd-MM HH:mm")));
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        graphView.getGridLabelRenderer().setLabelHorizontalHeight(200);


        //make the graph scrollable and scalable
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalable(true);

        //Stop listener
        db.removeEventListener(eventListener);
    }

    void retrieveSensorDataFromDB(){
        if(sensorType.equals("TEMPERATURE-C")) {
            db.child("TemperatureSensor1").orderByChild("time").limitToLast(dataLimit).addValueEventListener(eventListener);
            xAxisTitle = "Celcius";
        }

        else if (sensorType.equals("TEMPERATURE-F")) {
            db.child("TemperatureSensor1").orderByChild("time").limitToLast(dataLimit).addValueEventListener(eventListener);
            xAxisTitle = "Fahrenheit";
        }
        else if(sensorType.equals("UV")){
            db.child("UVSensor1").orderByChild("time").limitToLast(dataLimit).addValueEventListener(eventListener);
            xAxisTitle = "Index";
        }

        else if(sensorType.equals("HUMIDITY")) {
            db.child("HumiditySensor1").orderByChild("time").limitToLast(dataLimit).addValueEventListener(eventListener);
            xAxisTitle = "%";
        }

        else if(sensorType.equals("MOISTURE")) {
            db.child("SoilSensor1").orderByChild("time").limitToLast(dataLimit).addValueEventListener(eventListener);
            xAxisTitle = "%";
        }
    }
}
