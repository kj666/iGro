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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/*
* This class represents the registration section of the application. Every new user is
* required to enter their valid info and this data is then stored onto the database
 */
public class RegistrationActivity extends AppCompatActivity {
    // TODO: 2019-03-06
    // add the provided firebase function to check if the user is logged into the app
    private FirebaseAuth mAuth;
    private String TAG = "RegistrationActivity"; //Used for debugging purposes
    protected EditText userName;
    protected EditText userEmail;
    protected EditText userPassword;
    protected EditText userPasswordConfirmation;
    protected Button signUpButton;
    protected Button cancelButton;
    FirebaseFirestore userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseApp.initializeApp(this); // This will probably be needed to move to main activity
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseFirestore.getInstance();
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
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Registration Successful", Toast.LENGTH_LONG);
                                toast.show();
                                FirebaseUser registeredUser = mAuth.getCurrentUser();
                                // sendEmailVerification(registeredUser);
                                addToUserDatabase(registeredUser);
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
        if (isValidEmail(userEmail.getText().toString()) && isValidPassword() && isValidName()) {
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

    boolean isValidName() {
        String nameUnderTest = userName.getText().toString();
        if (nameUnderTest == null || nameUnderTest.length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    void sendEmailVerification(final FirebaseUser registeredUser) {
        registeredUser.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, " Verification email sent to " +
                                    registeredUser.getEmail());
                            Toast.makeText(getApplicationContext(), "Verification email sent to " +
                                    registeredUser.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Failed to send verification email");
                        }
                    }
                });
    }

    /*
    * Function that adds a user object to the firestore database.
    * user object is tentatively structured as follows:
    * name
    * email
    * ID
     */
    void addToUserDatabase(FirebaseUser addThisUser) {
        Map<String, Object> user = new HashMap<>();
        // TODO 2019-03-08
        // Check if there could be some sort of concurrency or security issue from sourcing-
        // data from both the firebase user and the inputted data in the registration EditText-
        // widgets
        // TODO 2019-03-08
        // Figure out how to determine greenhouse ID and user role
        // so far, greenhouse ID = extra field on registration screen
        // user role = ?
        user.put("Name", userName.getText().toString());
        user.put("Email", addThisUser.getEmail());
        user.put("ID", addThisUser.getUid());
        user.put("Greenhouse ID#", "test");
        user.put("User Role", "user"); // admin or user

        userDatabase.collection("Users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG,"DocumentSnapshot added with ID" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}
