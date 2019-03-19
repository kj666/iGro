package com.example.igro.Controller;

import android.content.Context;
import android.content.Intent;

import com.example.igro.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    public Helper(Context context, FirebaseAuth firebaseAuth) {
        this.context = context;
        this.firebaseAuth = firebaseAuth;
        this.user = firebaseAuth.getCurrentUser();
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
    }


    /**
     * Convert unix time to human readable time
     * @param timestamp
     */
    public static String convertTime(long timestamp){

        String dateReadable = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(timestamp));

//        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
//        String heatOnTimeStampFormated = df.format(Calendar.getInstance().getTime());

        return dateReadable;
    }
}
