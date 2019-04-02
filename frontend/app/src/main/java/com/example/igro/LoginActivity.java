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

import com.example.igro.Controller.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class LoginActivity extends AppCompatActivity {
    //UI component
    private Button loginUserButton;
    protected EditText UserEditText;
    protected EditText passwordEditText;
    protected Button registerUserButton;

    private FirebaseAuth mAuth; //authentication instance
    private String TAG = "LoginActivity";
    //protected Login profile;
    private String userName;
    private String password;

    private Helper helper = new Helper(this, FirebaseAuth.getInstance());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initiating authentication instance
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
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
                //Validate that inputs are not empty
                if(userName.equals("") || password.equals("")){
                    Toast.makeText(LoginActivity.this, "Login fields are empty",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Validate(userName, password);

                    //get token
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(!task.isSuccessful()){
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }
                            //Get new instance ID token
                            String token = task.getResult().getToken();

                            //toast
                            String msg = getString(R.string.msg_token_fmt, token);
                            Log.d(TAG, msg);
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.goToActivity(RegistrationActivity.class);
            }
        });
    }

    /**
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
                    Toast.makeText(LoginActivity.this, "Authentication Success.",
                            Toast.LENGTH_SHORT).show();

                    //Use function in Controller.Helper to go to dashboard activity
                    helper.goToActivity(MainActivity.class);
                } else {
                    //sign in failed
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication Failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
