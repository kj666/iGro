package com.example.igro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.igro.Controller.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.example.igro.Controller.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PollingFrequencyDialogFragment extends AppCompatDialogFragment {

    EditText PollingFrequencyEditText;

    private Helper helper = new Helper(getContext(), FirebaseAuth.getInstance());
    String pollingfrequency;
    int pollingfrequencyInt;
    int equivalentpollingfrequency;
    DatabaseReference pollingDB;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        helper.setSharedPreferences(getContext());
        pollingDB = FirebaseDatabase.getInstance().getReference(helper.retrieveGreenhouseID()+"/SensorConfig/poll");
        retrievePollData();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_polling_frequency,null);

        builder.setView(view);
        builder.setTitle("");
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pollingfrequency = PollingFrequencyEditText.getText().toString();
                pollingfrequencyInt = Integer.parseInt(pollingfrequency);
                equivalentpollingfrequency = pollingfrequencyInt * 1000;
                pollingDB.setValue(equivalentpollingfrequency);

                //listener.applyPollingFrequency(pollingfrequency);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        PollingFrequencyEditText = view.findViewById(R.id.pollingEditText);

        // Create the AlertDialog object and return it
        return builder.create();

    }

    public void retrievePollData() {

        pollingDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastpollfrequencyMs = dataSnapshot.getValue().toString();
                int lastpollfrequencyInt = Integer.parseInt(lastpollfrequencyMs);
                int lastpollfrequencySec = lastpollfrequencyInt/1000;
                String lastpollfrequency = String.valueOf(lastpollfrequencySec);
                PollingFrequencyEditText.setText(lastpollfrequency);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
