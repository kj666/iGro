package com.example.b_ngh.igro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {
private Button l;
    protected EditText UserEditText;
    protected EditText passwordEditText;
    //protected Login profile;
    private String User;
    private String Pass;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordEditText= findViewById(R.id.EditText2);

        UserEditText = findViewById(R.id.EditText1);
        l = (Button)findViewById(R.id.login);
        User=UserEditText.getText().toString();
        Pass =passwordEditText.getText().toString();


       // UserEditText.setText(profile.getUsername());



        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               Validate();

            }

        });}

        private void Validate() {
            //goToDashboard();

            if ( UserEditText.getText().toString() == "baltej") {
                goToDashboard();
            }
        }

    void goToDashboard()
    {
        Intent i = new Intent(MainActivity.this,Dashboard.class);
        startActivity(i);
    }


    }



