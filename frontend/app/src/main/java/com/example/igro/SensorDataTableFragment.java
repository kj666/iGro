package com.example.igro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.igro.Controller.Helper;
import com.example.igro.Models.SensorData.SensorDataValue;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class SensorDataTableFragment extends Fragment {
    private static final String PARAM = "type";
    private Helper helper = new Helper(getContext(), FirebaseAuth.getInstance());
    private String sensorType;
    private int dataLimit;
    ListView listView;
    private List<SensorDataValue> sensorDataList = new ArrayList<>();

    public SensorDataTableFragment() {
        // Required empty public constructor
    }

    public static SensorDataTableFragment newInstance(String sensorType, int dataLimit){
        SensorDataTableFragment fragment = new SensorDataTableFragment();
        Bundle passData = new Bundle();
        passData.putString(PARAM, sensorType);
        fragment.sensorType = sensorType;
        fragment.dataLimit = dataLimit;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sensor_data_table, container, false);
        listView = view.findViewById(R.id.sensorDataListView);

        helper.setSharedPreferences(getContext());
        retrieveSensorDataFromDB();
        return view;
    }

    void populateTable(){
        SensorDataListAdapter sensorDataListAdapter = new SensorDataListAdapter(sensorDataList, sensorType);
        listView.setAdapter(sensorDataListAdapter);
    }
    void retrieveSensorDataFromDB(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(helper.retrieveGreenhouseID()+"/Data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorDataValue sensorDataValue = snap.getValue(SensorDataValue.class);
                    sensorDataList.add(sensorDataValue);
                }
                populateTable();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        if(sensorType.equals("TEMPERATURE-C"))
            db.child("TemperatureSensor1").limitToLast(dataLimit).addValueEventListener(eventListener);
        else if (sensorType.equals("TEMPERATURE-F"))
            db.child("TemperatureSensor1").limitToLast(dataLimit).addValueEventListener(eventListener);
        else if(sensorType.equals("UV"))
            db.child("UVSensor1").addValueEventListener(eventListener);
        else if(sensorType.equals("HUMIDITY"))
            db.child("HumiditySensor1").limitToLast(dataLimit).addValueEventListener(eventListener);
        else if(sensorType.equals("MOISTURE"))
            db.child("SoilSensor1").limitToLast(dataLimit).addValueEventListener(eventListener);
    }

    class SensorDataListAdapter extends BaseAdapter {

        private List<SensorDataValue> sensorDataList;
        private String sensorType;

        public SensorDataListAdapter(List<SensorDataValue> sensorDataList, String sensorType) {
            this.sensorDataList = sensorDataList;
            this.sensorType = sensorType;
        }

        @Override
        public int getCount() {
            return sensorDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.sensor_data_list_view,null);
            TextView sensorDate = convertView.findViewById(R.id.sensorDateTextView);
            TextView sensorData = convertView.findViewById(R.id.sensorDataTextView);

            sensorDate.setText(Helper.convertTime(sensorDataList.get(position).getTime()));
            if(!sensorType.equals("UV"))
                sensorData.setText(new DecimalFormat("####0.0").format(sensorDataList.get(position).getValue())+"");
            else
                sensorData.setText(new DecimalFormat("####0.00").format(sensorDataList.get(position).getValue())+"");

            return convertView;
        }
    }
}
