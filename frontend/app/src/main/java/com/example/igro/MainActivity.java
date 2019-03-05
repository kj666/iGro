package com.example.igro;

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
    private Button tempNumberButton;
    private Button logout;
    private String UserN;
    private String UserP;
    MainActivity() {
        UserN= " ";
        UserP = " ";
    }
    MainActivity(String name,String pass){
        UserN = name;
        UserP = pass;

    }


    public int tempD;
    //Reference to collection in firestore
    private CollectionReference tempRef = FirebaseFirestore.getInstance().collection("temperature");

    //Get document from firestore
    public void getTempData(String id){
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
                        Log.d("ERROR", "No such document");
                    }
                }
                number.setText(tempD+"");
            }
        });
    }





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
    }
    public void openTemperature(){
        Intent intent=new Intent(this,TemperatureActivity.class);
        startActivity(intent);
    }
    public void openUv(){
        Intent intent=new Intent(this,UvIndexActivity.class);
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
