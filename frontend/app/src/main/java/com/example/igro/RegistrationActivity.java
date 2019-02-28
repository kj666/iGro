package com.example.igro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
* This class represents the registration section of the application. Every new user is
* required to enter their valid info and this data is then stored onto the database
 */
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
        setContentView(R.layout.activity_registration);
        //Creating a link to widgets on user interface
        userName = findViewById(R.id.userNameText);
        userEmail = findViewById(R.id.userEmailText);
        userPassword = findViewById(R.id.userPasswordText);
        userPasswordConfirmation = findViewById(R.id.confirmUserPasswordText);
        signUpButton = findViewById(R.id.singUpButton);
        cancelButton = findViewById(R.id.cancelButton);
        //Allowing entry fields to be modified
        userName.setFocusable(true);
        userEmail.setFocusable(true);
        userPassword.setFocusable(true);
        userPasswordConfirmation.setFocusable(true);
        //Providing functionality
        signUpButton.setOnClickListener(new View.OnClickListener() {
            // TODO 2019-02-27
            // Return to login activity after signUpButton is clicked AND registration is
            // successful. For now, its set to main activity
            @Override
            public void onClick(View v) {
                if (registerUser()) { //registration successful
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Registration Successful", Toast.LENGTH_LONG);
                    toast.show();
                    finish(); //returns to the activity that called it (should be login activity)
                } else { //registration unsuccessful
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Registration Failed", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /*
     * This function will register the user into a database if the inputted information is valid
     */
    boolean registerUser() {
        if (isValidUser() == false) {
            return false;
        } else {
            // TODO 2019-02-27
            // Store the data into the database
            // Confirm if logic to set to false makes sense
            userName.setFocusable(false);
            userEmail.setFocusable(false);
            userPassword.setFocusable(false);
            userPasswordConfirmation.setFocusable(false);
            return true;
        }
    }

    /*
     * This function will determine if all inputted information is correct by calling the validifier
     * functions for each entry type (e.g. email, password)
     */
    boolean isValidUser() {
        if (isValidEmail(userEmail.getText().toString()) && isValidPassword()) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * This function will determine if the email entered is valid
     */
    boolean isValidEmail(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    /*
    * This function checks to see if the initial password and the confirmation password are the
    * same.
     */
    boolean isValidPassword() {
        return userPassword.getText().toString()
                .equals(userPasswordConfirmation.getText().toString());
    }

}
