package com.example.igro;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/*
* This class represents the registration section of the application. Every new user is
* required to enter their valid info and this data is then stored onto the database
 */
public class RegistrationActivity extends AppCompatActivity {
    // TODO: 2019-02-27
    // add dependency in android manifest file to login activity
    // When initializing your Activity, check to see if the user is currently signed in.
    // |||||WHERE DO I CHECK THE ABOVE|||||||
    // |||||MAYBE ON EVERY ACTIVITY?|||||||||
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef; // Don't think that this is necessary
    private String TAG = "RegistrationActivity"; //Used for debugging purposes
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
        FirebaseApp.initializeApp(this); // This will probably be needed to move to main activity
        mAuth = FirebaseAuth.getInstance();
        //mStorageRef = FirebaseStorage.getInstance().getReference();
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
                registerUser();
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
    void registerUser() {
        // TODO 2019-02-28
        // Decide on what mods are needed for isValidUser()
        if (isValidUser()) {

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Registration Failed", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // TODO 2019-02-28
                                // Store user info into the database if valid
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Registration Successful", Toast.LENGTH_LONG);
                                toast.show();
                                finish();
                            } else {
                                // sign in fails
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        task.getException().toString(), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    });
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
