package com.example.igro;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import java.util.Calendar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Button temperature;
    private Button uv;
    private Button uvNumber;
    private Button number;
    private Button celcius;
    private Button fahrenheit;
    private boolean celcius_pressed=true;
    private boolean fahrenheit_pressed=false;

    private Button moistureNumber;
    private Button moistureButton;

    private Button tempNumberButton;
    private Button logout;
    private String UserN;
    private String UserP;

    protected Button humidityTitle;
    protected TextView humNumber;

    public int tempD;

    //Get document from firestore
    public void getTempData(String id){
        //Reference to collection in firestore
        CollectionReference tempRef = FirebaseFirestore.getInstance().collection("temperature");

        tempRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        tempD = Integer.parseInt(document.getString("temp"));

                        Log.d("WORKING", ""+tempD);
                    }
                    else{
                        Log.d("ERROR", "Cannot get Temperature");
                    }
                }
                number.setText(tempD+"");
            }
        });
    }

    public int humD;

    //Get humidity document from firestore
    public void getHumData(String id){
        //Reference to humidity collection in firestore
        CollectionReference humRef = FirebaseFirestore.getInstance().collection("humidity");
        humRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        humD = Integer.parseInt(document.getString("humValue"));

                        Log.d("SUCCESS", ""+humD);
                    }
                    else{
                        Log.d("ERROR", "Cannot Retreave Humidity");
                    }
                }
                humNumber.setText(humD+"");
            }
        });
    }


    Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dashboard dash = new Dashboard();
        int c= dash.getCounter();
        if (c==0){
           Intent j = new Intent(MainActivity.this, Dashboard.class);
           startActivity(j);}
        //logout =  (Button) findViewById(R.id.logout);
        temperature=(Button) findViewById(R.id.temp_button);
        number=(Button) findViewById(R.id.tempNumberView);

        getTempData("1");
        number=(Button) findViewById(R.id.tempNumberView);
        celcius=(Button) findViewById(R.id.celciusOutButton);
        fahrenheit=(Button) findViewById(R.id.fahrenheitOutButton);
        uv=(Button) findViewById(R.id.uvButton);
        uvNumber=(Button)findViewById(R.id.uvNumberButton);

        humidityTitle = (Button)findViewById(R.id.humidityButton);
        humNumber = (TextView) findViewById(R.id.humidityPercentView);
        moistureButton = (Button) findViewById(R.id.moistureButton);
        moistureNumber = (Button) findViewById(R.id.moisturePercentView);

        getHumData("1");

        //from fahrenheit to celcius
        celcius.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(fahrenheit_pressed) {
                    for(int i=0;i<1;i++) {
                        Double degrees = Double.parseDouble(number.getText().toString());
                         Double a = (degrees - 32) * 5 / 9;
                        number.setText(Double.toString(a));
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
                        Double degrees = Double.parseDouble(number.getText().toString());
                        Double a = degrees * 9 / 5 + 32;
                        number.setText(Double.toString(a));
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



        //opening the Uv index view  when the uv text is clicked
        uv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUv();
            }
        });

        //opening the Temperature view when the temperature number is clicked
        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperature();
            }
        });
        ////opening the uv view when the uv number is clicked
        uvNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUv();
            }
        });

        humidityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHumidity();
            }
        });
        humNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHumidity();
            }
        });



        moistureButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick (View v2){
                openMoistureActivity();
            }
        });

        moistureNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMoistureActivity();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    public void openTemperature(){
        Intent tempIntent=new Intent(this,TemperatureActivity.class);
        startActivity(tempIntent);
    }
    public void openUv(){
        Intent intent=new Intent(this,UvIndexActivity.class);
        startActivity(intent);
    }

    public void openHumidity(){
        Intent humIntent=new Intent(this,HumidityActivity.class);
        startActivity(humIntent);
    }

    protected void goToRegistrationActivity() {
        Intent test = new Intent(MainActivity.this, RegistrationActivity.class);
        startActivity(test);
    }

    public void openMoistureActivity(){
        Intent intent2 = new Intent(this,MoistureActivity.class);
        startActivity(intent2);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.sign_out:
                Intent f = new Intent(MainActivity.this, Dashboard.class);
                startActivity(f);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

}
