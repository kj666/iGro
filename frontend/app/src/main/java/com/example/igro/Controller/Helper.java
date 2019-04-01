package com.example.igro.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.igro.LoginActivity;
import com.example.igro.Models.SensorData.SensorDataValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;


/**
 * This class contains functions that are used across the entire app
 */
public class Helper {

    //Retrieve the current context of the app
    Context context;
    //Firebase authentication
    FirebaseAuth firebaseAuth;
    //Firebase user
    FirebaseUser user;

    protected SharedPreferences sharedPreferences;

    public Helper(Context contexts, FirebaseAuth firebaseAuth) {
        this.context = contexts;
        this.firebaseAuth = firebaseAuth;
//        this.user = firebaseAuth.getCurrentUser();

    }

    public void setSharedPreferences(Context context){
        sharedPreferences = context.getSharedPreferences("greenhouse",Context.MODE_PRIVATE);
    }

    public void saveGreenHouseID(String greenhouseID){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("GreenhouseID", greenhouseID);
        editor.commit();
    }

    public String retrieveGreenhouseID(){
        return sharedPreferences.getString("GreenhouseID", null);
    }

    public void resetGreenhouse(){
        saveGreenHouseID("");
    }

    /**
     * Go to specified activity
     * @param goTo
     */
    public void goToActivity(Class goTo) {
        Intent test = new Intent(context, goTo);
        context.startActivity(test);
    }

    /**
     * Check firebase user authentication
     * @return
     */
    public FirebaseUser checkAuthentication() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // current user validated
        } else {
            goToActivity(LoginActivity.class);
        }

        return currentUser;
    }

    /**
     * Signout a firebase user
     */
    public void signout(){
        firebaseAuth.signOut();
        resetGreenhouse();
    }


    /**
     * Convert unix time to human readable time
     * @param timestamp
     */
    public static String convertTime(long timestamp){

        String dateReadable = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(timestamp));
        return dateReadable;
    }

    public static double retrieveRange(String sensorType, DataSnapshot dataSnapshot){
        double limitRange;
        if(dataSnapshot.child(sensorType).getValue() != null)
            limitRange = Double.parseDouble(dataSnapshot.child(sensorType).getValue().toString());
        else
            limitRange = 0.0;
        return limitRange;
    }



}
