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
    protected Button temperatureTitle;
    protected Button tempNumber;

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
                tempNumber.setText(tempD+"");
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTitle=(Button) findViewById(R.id.tempButton);
        tempNumber=(Button) findViewById(R.id.tempNumberButton);
        getTempData("1");

        humidityTitle = (Button)findViewById(R.id.humButton);
        humNumber = (TextView) findViewById(R.id.humNumberTextView);
        getHumData("1");


        temperatureTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperature();
            }
        });
        tempNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTemperature();
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


    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    public void openTemperature(){
        Intent tempIntent=new Intent(this,TemperatureActivity.class);
        startActivity(tempIntent);
    }

    public void openHumidity(){
        Intent humIntent=new Intent(this,HumidityActivity.class);
        startActivity(humIntent);
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
