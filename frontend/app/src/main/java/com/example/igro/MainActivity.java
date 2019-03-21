package com.example.igro;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;


import com.example.igro.Controller.Helper;
import com.example.igro.Models.SensorData.SensorData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button temperatureTitleButton;
    private Button temperatureNumberButton;
    private Button temperatureCelsiusButton;
    private Button temperatureFahrenheitButton;

    private Button uvTitleButton;
    private Button uvNumberButton;

    private boolean celcius_pressed = true;
    private boolean fahrenheit_pressed = false;

    private Button moistureNumberButton;
    private Button moistureTitleButton;

    protected Button humidityTitleButton;
    protected TextView humidityNumberButton;

    private Helper helper = new Helper(this, FirebaseAuth.getInstance());

    public int tempD;
    public double UV;

    private FirebaseAuth mAuth; // authentication instance
    protected TextView userWelcomeMessage;
    private FirebaseUser currentUser;

    private List<SensorData> sensorDataList = new ArrayList<>();
    //create heater database reference
   // DatabaseReference DB = FirebaseDatabase.getInstance().getReference("data");
    /**
     * On create
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = helper.checkAuthentication();

        //Initialize all the UI elements
        initializeUI();

        userWelcomeMessage = findViewById(R.id.welcomeMessageText);
        String welcomeMessage = currentUser != null ? "Hi " + currentUser.getEmail() : "";
        userWelcomeMessage.setText(welcomeMessage);
      //  getHumData("1");
//        getTempData("1");
        retrieveHum();
        retrieveTemp();
        retrieveSM();
        retrieveUV();

        temperatureNumberButton.setText(sensorDataList.size()+"");

        //Temperature Celsius Button listener
        temperatureCelsiusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(fahrenheit_pressed) {
                    for(int i=0;i<1;i++) {
                        Double degrees = Double.parseDouble(temperatureNumberButton.getText().toString());
                        Double a = (degrees - 32) * 5 / 9;
                        temperatureNumberButton.setText(Double.toString(a));
                    }
                    celcius_pressed = true;
                    fahrenheit_pressed = false;
                }
            }
        });
        // convert temperatureCelsiusButton to temperatureFahrenheitButton
        temperatureFahrenheitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(celcius_pressed) {
                    for(int i=0;i<1;i++) {
                        Double degrees = Double.parseDouble(temperatureNumberButton.getText().toString());
                        Double a = degrees * 9 / 5 + 32;
                        temperatureNumberButton.setText(Double.toString(a));
                    }
                    fahrenheit_pressed = true;
                    celcius_pressed=false;
                }
            }
        });

        //opening the Temperature view when the temperature text is clicked
        temperatureTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(TemperatureActivity.class);
            }
        });
        //opening the Temperature view when the temperature number is clicked
        temperatureNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(TemperatureActivity.class);
            }
        });

        //opening the Uv index view  when the uvTitleButton text is clicked
        uvTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(UvIndexActivity.class);
            }
        });

        ////opening the uvTitleButton view when the uvTitleButton number is clicked
        uvNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(UvIndexActivity.class);
            }
        });
        humidityTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(HumidityActivity.class);
            }
        });
        humidityNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(HumidityActivity.class);
            }
        });
        moistureTitleButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v2){
                helper.goToActivity(MoistureActivity.class);
            }
        });
        moistureNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(MoistureActivity.class);
            }
        });
    }

    protected void initializeUI(){
        //Temperature View initialization
        temperatureTitleButton = (Button) findViewById(R.id.temp_button);
        temperatureNumberButton = (Button) findViewById(R.id.tempNumberView);
        temperatureCelsiusButton = (Button) findViewById(R.id.celciusOutButton);
        temperatureFahrenheitButton = (Button) findViewById(R.id.fahrenheitOutButton);

        //UV view initialization
        uvTitleButton = (Button) findViewById(R.id.uvButton);
        uvNumberButton = (Button)findViewById(R.id.uvNumberButton);

        //Humidity view initialization
        humidityTitleButton = (Button)findViewById(R.id.humidityButton);
        humidityNumberButton = (TextView) findViewById(R.id.humidityPercentView);

        //Moisture view initialization
        moistureTitleButton = (Button) findViewById(R.id.moistureButton);
        moistureNumberButton = (Button) findViewById(R.id.moisturePercentView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        helper.checkAuthentication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.checkAuthentication();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        helper.checkAuthentication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out:
                helper.signout();
                helper.goToActivity(LoginActivity.class);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    //Todo have to change this to real time database format
    //Get document from firestore



    void retrieveTemp(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    Log.d("FIREBASE", sensorData.getTime()+"");
                    temperatureNumberButton.setText(sensorData.getTemperatureC()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);

    }


    public double humD;

    //Get humidity document from firestore
    public void retrieveHum(){
        //Reference to humidity collection in firestore
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    Log.d("FIREBASE", sensorData.getHumidity()+"");
                    humidityNumberButton.setText(sensorData.getHumidity()+"");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);

    }
    void retrieveUV(){
       DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    Log.d("FIREBASE", sensorData.getUv()+"");

                  uvNumberButton.setText(sensorData.getUv()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);

    }

    void retrieveSM(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    Log.d("FIREBASE", sensorData.getSoilMoisture()+"");
                    moistureNumberButton.setText(sensorData.getSoilMoisture()+"");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.orderByKey().limitToLast(1).addValueEventListener(eventListener);

    }

}
