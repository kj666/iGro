package com.example.igro;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class TemperatureActivity extends AppCompatActivity {

    private boolean celcius_pressedGreen =true;
    private boolean fahrenheit_pressedGreen =false;
    private boolean celcius_pressedOut =true;
    private boolean fahrenheit_pressedOut =false;

    private Button celciusGreen;
    private Button fahrenheitGreen;
    private Button celciusOut;
    private Button fahrenheitOut;
    private TextView numberGreen;
    private TextView numberOut;
    LineGraphSeries<DataPoint> series;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_temperature);
        double y,x=-5;
        numberGreen =(TextView) findViewById(R.id.indoorTempView);
        numberOut=(TextView) findViewById(R.id.outTempView);
        celciusGreen =(Button) findViewById(R.id.celciusGreenButton);
        fahrenheitGreen =(Button) findViewById(R.id.fahrenheitGreenButton);
        celciusOut =(Button) findViewById(R.id.celciusOutButton);
        fahrenheitOut =(Button) findViewById(R.id.fahrenheitOutButton);


        //from fahrenheitGreen to celciusGreen
        celciusGreen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(fahrenheit_pressedGreen) {
                    for(int i=0;i<1;i++) {
                        Integer degrees = Integer.parseInt(numberGreen.getText().toString());
                        Integer a = (Integer)Math.round((degrees - 32) * 5 / 9);
                        numberGreen.setText(Integer.toString(a));
                    }
                    celcius_pressedGreen = true;
                    fahrenheit_pressedGreen =false;
                }
            }
        });
        // from celcius to fahrenheit
        fahrenheitGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(celcius_pressedGreen) {
                    for(int i=0;i<1;i++) {
                        Integer degrees = Integer.parseInt(numberGreen.getText().toString());
                        Integer a = degrees * 9 / 5 + 32;
                        numberGreen.setText(Integer.toString(a));
                    }
                    fahrenheit_pressedGreen = true;
                    celcius_pressedGreen =false;
                }
            }
        });

        //from fahrenheit to celcius
        celciusOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(fahrenheit_pressedOut) {
                    for(int i=0;i<1;i++) {
                        Integer degrees = Integer.parseInt(numberOut.getText().toString());
                        Integer a = (Integer)Math.round((degrees - 32) * 5 / 9);
                        numberOut.setText(Integer.toString(a));
                    }
                    celcius_pressedOut = true;
                    fahrenheit_pressedOut =false;
                }
            }
        });
        // from celciusGreen to fahrenheitGreen
        fahrenheitOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(celcius_pressedOut) {
                    for(int i=0;i<1;i++) {
                        Integer degrees = Integer.parseInt(numberOut.getText().toString());
                        Integer a = degrees * 9 / 5 + 32;
                        numberOut.setText(Integer.toString(a));
                    }
                    fahrenheit_pressedOut = true;
                    celcius_pressedOut =false;
                }
            }
        });


        GraphView graph =findViewById(R.id.graphTempCardView);
        series=new LineGraphSeries<>();
        for(int i=0; i<500; i++){
            x=x+0.1;
            y=Math.sin(x);
            series.appendData(new DataPoint(x,y),true,500);
        }
         graph.addSeries(series);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
