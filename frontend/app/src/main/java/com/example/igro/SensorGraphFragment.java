package com.example.igro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SensorGraphFragment extends Fragment {
    private static final String PARAM = "type";

    private String sensorType;
    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    TextView sensorTypeTextView;
    private List<SensorData> sensorDataList = new ArrayList<>();


    public SensorGraphFragment() {
        // Required empty public constructor
    }

    public static SensorGraphFragment newInstance(String sensorType){
        SensorGraphFragment fragment = new SensorGraphFragment();
        Bundle passData = new Bundle();
        passData.putString(PARAM, sensorType);
        fragment.sensorType = sensorType;
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor_graph, container, false);
        graphView = (GraphView) view.findViewById(R.id.sensorDataGraph);

        sensorTypeTextView = view.findViewById(R.id.graphTypeTextView);
        sensorTypeTextView.setText(sensorType);

        retrieveSensorDataFromDB();
        return view;
    }

    /**
     * Populate with data
     */
    void populateGraph(){

        series = new LineGraphSeries<>();

        for(SensorData data: sensorDataList){
            long t = data.getTime();
            Date time = new Date(t);

            double y = 0;
            if(sensorType.equals("TEMPERATURE"))
                y = data.getTemperatureC();
            else if(sensorType.equals("UV"))
                y = data.getUv();
            else if(sensorType.equals("HUMIDITY"))
                y = data.getHumidity();
            else if(sensorType.equals("MOISTURE"))
                y = data.getSoil();

            series.appendData(new DataPoint(time.getTime(),y), true, 500);
        }
        graphView.addSeries(series);

        //Use time as x-axis
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getContext(),new SimpleDateFormat("dd-MM HH:mm")));
        graphView.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graphView.getGridLabelRenderer().setLabelHorizontalHeight(200);

        //make the graph scrollable and scalable
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScalable(true);
    }

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
            }
        };
        db.addValueEventListener(eventListener);
    }
}
