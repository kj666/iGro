package com.example.igro;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igro.Controller.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SensorDataActivity extends AppCompatActivity {

    private Helper helper = new Helper(this, FirebaseAuth.getInstance());
    DatabaseReference dataLimitDB;
    //UI components
    TextView historicalSensorDataTextView;
    Button refreshButton;
    EditText limitEditText;

    //boolean to decide if its table/graph
    boolean tableMode = false;
    String sensorType;

    //Fragments
    SensorGraphFragment sensorGraphFragment;
    SensorDataTableFragment sensorDataTableFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        helper.setSharedPreferences(getApplicationContext());
        dataLimitDB = FirebaseDatabase.getInstance().getReference().child(helper.retrieveGreenhouseID()+"/SensorConfig/DataLimit");
        fragmentManager =  getSupportFragmentManager();


        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        retrieveDataLimit();

        historicalSensorDataTextView = (TextView) findViewById(R.id.historicalSensorDataTextView);
        limitEditText = findViewById(R.id.hsSenDataLimitEditText);
        refreshButton = findViewById(R.id.refreshActivityButton);

        Intent intent = getIntent();
        sensorType = intent.getStringExtra("SensorType");
        String pageTitle = "HISTORICAL " + sensorType + " SENSOR DATA";

//        createGraphFrag(sensorType, setDataLimit());

        historicalSensorDataTextView.setText(pageTitle);

        if(sensorType.equals("TEMPERATURE-C")){

        } else if (sensorType.equals("TEMPERATURE-F")){

        } else if (sensorType.equals("HUMIDITY")) {

        }else if(sensorType.equals("MOISTURE")){

        }else if(sensorType.equals("UV")){

        }else{
            Toast.makeText(this, "ERROR: unKnown sensor type ", Toast.LENGTH_LONG ).show();
        }


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tableMode) {
                    setDataLimit();
                    removeTableFrag();
                    createTableFrag(sensorType);
                }
                else {
                    removeGraphFrag();
                    createGraphFrag(sensorType, setDataLimit());
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void retrieveDataLimit(){
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                limitEditText.setText(dataSnapshot.getValue().toString());
                createGraphFrag(sensorType, setDataLimit());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dataLimitDB.addValueEventListener(eventListener);
    }

    int setDataLimit(){
        int dataLimit;
        if(limitEditText.getText().toString().equals("")){
            dataLimit = Integer.parseInt(limitEditText.getText().toString());
        }else {
            dataLimit = Integer.parseInt(limitEditText.getText().toString());
        }

        dataLimitDB.setValue(dataLimit);
        return  dataLimit;
    }

    void createGraphFrag(String type, int dataLimit){
        sensorGraphFragment = SensorGraphFragment.newInstance(type, dataLimit);
        fragmentTransaction = fragmentManager.beginTransaction().add(R.id.fragmentContainer, sensorGraphFragment);
        fragmentTransaction.addToBackStack("graph");
        fragmentTransaction.commitAllowingStateLoss();
    }

    void removeGraphFrag(){
        fragmentManager.popBackStack("graph",fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    void createTableFrag(String type){
        sensorDataTableFragment = SensorDataTableFragment.newInstance(type);
        fragmentTransaction = fragmentManager.beginTransaction().add(R.id.fragmentContainer, sensorDataTableFragment);
        fragmentTransaction.addToBackStack("table");
        fragmentTransaction.commitAllowingStateLoss();
    }

    void removeTableFrag(){
        fragmentManager.popBackStack("table",fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.graph_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.polling_menu:
                openDialog();
                return true;


            case R.id.tableGraph_switch:
                if(tableMode) {
                    tableMode = false;
                    item.setChecked(false);
                    removeTableFrag();
                    createGraphFrag(sensorType, setDataLimit());

                }
                else {
                    tableMode = true;
                    item.setChecked(true);
                    removeGraphFrag();
                    createTableFrag(sensorType);
                }
                return true;

            case R.id.refreshButton:
                if(tableMode) {
                    removeTableFrag();
                    createTableFrag(sensorType);
                }
                else {
                    removeGraphFrag();
                    createGraphFrag(sensorType, setDataLimit());
                }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    public void openDialog(){
        PollingFrequencyDialogFragment dialog = new PollingFrequencyDialogFragment();
        dialog.show(getSupportFragmentManager(), "Polling dialog");
    }
}
