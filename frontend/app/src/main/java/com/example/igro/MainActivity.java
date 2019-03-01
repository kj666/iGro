package com.example.igro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;





public class MainActivity extends AppCompatActivity {

    private Button humidityButton;
    private Button moistureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        humidityButton = (Button) findViewById(R.id.humiditybutton);
        moistureButton = (Button) findViewById(R.id.moisturebutton);

        humidityButton.setOnClickListener( new View.OnClickListener(){
            @Override
                    public void onClick (View v){
                openHumidityActivity();
            }
        });

        moistureButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick (View v){
                openMoistureActivity();
            }
        });


    }

    public void openHumidityActivity(){
        Intent intent = new Intent(this,HumidityActivity.class);
        startActivity(intent);
    }

    public void openMoistureActivity(){
        Intent intent = new Intent(this,MoistureActivity.class);
        startActivity(intent);
    }
}
