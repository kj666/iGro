package com.example.igro.Controller;

import android.content.Context;
import android.content.Intent;

public class Helper {

    /**
     * This function goes from the current activity to the specified Activity
     * @param currentContext
     * @param goTo
     */
    static public void goTo(Context currentContext, Class goTo) {
        Intent test = new Intent(currentContext, goTo);
        currentContext.startActivity(test);
    }

    static public void Onclick(){

    }
}
