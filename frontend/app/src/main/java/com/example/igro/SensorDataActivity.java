package com.example.igro;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class SensorDataActivity extends AppCompatActivity {

    //UI components
    TextView historicalSensorDataTextView;
    Button refreshButton;

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

        fragmentManager =  getSupportFragmentManager();

        historicalSensorDataTextView = (TextView) findViewById(R.id.historicalSensorDataTextView);
        refreshButton = findViewById(R.id.refreshActivityButton);

        Intent intent = getIntent();
        sensorType = intent.getStringExtra("SensorType");
        String pageTitle = "HISTORICAL " + sensorType + " SENSOR DATA";

        createGraphFrag(sensorType);

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
                    removeTableFrag();
                    createTableFrag(sensorType);
                }
                else {
                    removeGraphFrag();
                    createGraphFrag(sensorType);
                }
            }
        });

    }
    void createGraphFrag(String type){
        sensorGraphFragment = SensorGraphFragment.newInstance(type);
        fragmentTransaction = fragmentManager.beginTransaction().add(R.id.fragmentContainer, sensorGraphFragment);
        fragmentTransaction.addToBackStack("graph");
        fragmentTransaction.commit();
    }

    void removeGraphFrag(){
        fragmentManager.popBackStack("graph",1);
    }

    void createTableFrag(String type){
        sensorDataTableFragment = SensorDataTableFragment.newInstance(type);
        fragmentTransaction = fragmentManager.beginTransaction().add(R.id.fragmentContainer, sensorDataTableFragment);
        fragmentTransaction.addToBackStack("table");
        fragmentTransaction.commit();
    }

    void removeTableFrag(){
        fragmentManager.popBackStack("table",1);
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
                    createGraphFrag(sensorType);

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
                    createGraphFrag(sensorType);
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
