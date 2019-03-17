package com.example.igro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

public class SensorDataActivity extends AppCompatActivity {

    TextView historicalSensorDataTextView;

    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        historicalSensorDataTextView = (TextView)findViewById(R.id.historicalSensorDataTextView);

        Intent intent = getIntent();
        String sensorType = intent.getStringExtra("SensorType");
        String pageTitle = "HISTORICAL " + sensorType + " SENSOR DATA";

        historicalSensorDataTextView.setText(pageTitle);


        double y,x;
        x=-5;

        GraphView graph =findViewById(R.id.graph);
        series=new LineGraphSeries<>();
        for(int i=0; i<500; i++){
            x=x+0.1;
            y=Math.sin(x);
            series.appendData(new DataPoint(x,y),true,500);
        }
        graph.addSeries(series);


    }
}
