package com.example.igro;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {
    // TODO: 2019-02-27
    // add dependency in android manifest file to login activity
    protected EditText userName;
    protected EditText userEmail;
    protected EditText userPassword;
    protected EditText userPasswordConfirmation;
    protected Button signUpButton;
    protected Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userName = findViewById(R.id.userNameText);
        userEmail = findViewById(R.id.userEmailText);
        userPassword = findViewById(R.id.userPasswordText);
        userPasswordConfirmation = findViewById(R.id.confirmUserPasswordText);
        signUpButton = findViewById(R.id.singUpButton);
        cancelButton = findViewById(R.id.cancelButton);

        setContentView(R.layout.activity_registration);
    }

    /*
    * This function will determine if the email entered is valid
     */
    final static boolean validEmail(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}
