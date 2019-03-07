package com.example.igro;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity {
    private Button loginUserButton;
    private FirebaseAuth mAuth; //authentication instance
    protected EditText UserEditText;
    protected EditText passwordEditText;
    protected Button registerUserButton;
    private String TAG = "Dashboard";
    //protected Login profile;
    private String userName;
    private String password;
    static int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initiating authentication instance
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_dashboard);
        passwordEditText = findViewById(R.id.userPasswordEditText);
        UserEditText = findViewById(R.id.userNameEditText);
        loginUserButton = findViewById(R.id.loginUserButton);
        registerUserButton = findViewById(R.id.registerUserButton);

        // UserEditText.setText(profile.getUsername());


        loginUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = UserEditText.getText().toString();
                password = passwordEditText.getText().toString();
                Validate(userName, password);
            }
        });

        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistrationActivity();
            }
        });
    }

    /*
    * This function will authenticate user registration credentials by verifying if there is an
    * equivalent entry in the firebase user database
     */

    private void Validate(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //sign in succeed
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser validUser = mAuth.getCurrentUser();
                    Toast.makeText(Dashboard.this, "Authentication Success.",
                            Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                    // TODO 2019-03-07
                    // Direct valid user to the info screen
                } else {
                    //sign in failed
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(Dashboard.this, "Authentication Failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void goToMainActivity() {
        Intent i = new Intent(Dashboard.this, MainActivity.class);
        startActivity(i);
    }

    protected void goToRegistrationActivity() {
        Intent test = new Intent(Dashboard.this, RegistrationActivity.class);
        startActivity(test);
    }

    int getCounter(){
        return counter;
    }
    void setCounter(int i){
        counter =i;

    }
}
