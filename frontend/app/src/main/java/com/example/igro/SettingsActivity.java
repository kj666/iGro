package com.example.igro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.igro.Controller.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox cToFSwitch;
    private Helper helper = new Helper(this, FirebaseAuth.getInstance());
    private FirebaseUser currentUser;
    private TextView currentTemperatureMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        helper.setSharedPreferences(getApplicationContext());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        cToFSwitch = findViewById(R.id.switchTemperatureMetricCheckBox);
        currentTemperatureMetric = findViewById(R.id.currentTemperatureMetricTextView);
        currentTemperatureMetric.setText("Current Temperature Metric is: " + returnMetricUsed());
        cToFSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.saveTemperatureSettings(!helper.retrieveTemperatureMetric());
                currentTemperatureMetric.setText("Current Temperature Metric is: "
                        + returnMetricUsed());
            }
        });
    }

    private String returnMetricUsed() {
        if (helper.retrieveTemperatureMetric()) {
            return "Celsius";
        } else {
            return "Fahrenheit";
        }
    }
}
