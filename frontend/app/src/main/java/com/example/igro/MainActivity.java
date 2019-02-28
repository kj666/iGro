package com.example.igro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {
    private Button temperature;
    private Button number;
    private Button celcius;
    private Button fahrenheit;
    private boolean celcius_pressed=true;
    private boolean fahrenheit_pressed=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temperature=(Button) findViewById(R.id.temp_button);
        number=(Button) findViewById(R.id.tempNumberView);
        celcius=(Button) findViewById(R.id.celciusOutButton);
        fahrenheit=(Button) findViewById(R.id.fahrenheitOutButton);


        //from fahrenheit to celcius
        celcius.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(fahrenheit_pressed) {
                    for(int i=0;i<1;i++) {
                        Integer degrees = Integer.parseInt(number.getText().toString());
                         Integer a = (Integer)Math.round((degrees - 32) * 5 / 9);
                        number.setText(Integer.toString(a));
                    }
                    celcius_pressed = true;
                    fahrenheit_pressed=false;
                }
            }
        });
        // from celcius to fahrenheit
        fahrenheit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(celcius_pressed) {
                    for(int i=0;i<1;i++) {
                        Integer degrees = Integer.parseInt(number.getText().toString());
                        Integer a = degrees * 9 / 5 + 32;
                        number.setText(Integer.toString(a));
                    }
                    fahrenheit_pressed = true;
                    celcius_pressed=false;
                }
            }
        });

        //opening the Temperature view when the temperature text is clicked
        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperature();
            }
        });

        //opening the Temperature view when the temperature number is clicked
        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperature();
            }
        });

    }
    public void openTemperature(){
        Intent intent=new Intent(this,TemperatureActivity.class);
        startActivity(intent);
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
