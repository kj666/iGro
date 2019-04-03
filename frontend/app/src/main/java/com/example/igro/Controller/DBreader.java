package com.example.igro.Controller;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBreader {

    private DatabaseReference db;

    public DBreader(DatabaseReference db) {
        this.db = FirebaseDatabase.getInstance().getReference();
    }



}
