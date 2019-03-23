package com.example.igro;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.igro.Controller.Helper;
import com.example.igro.Models.SensorData.SensorData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class SensorDataTableFragment extends Fragment {
    private static final String PARAM = "type";

    private String sensorType;
    ListView listView;
    private List<SensorData> sensorDataList = new ArrayList<>();

    public SensorDataTableFragment() {
        // Required empty public constructor
    }

    public static SensorDataTableFragment newInstance(String sensorType){
        SensorDataTableFragment fragment = new SensorDataTableFragment();
        Bundle passData = new Bundle();
        passData.putString(PARAM, sensorType);
        fragment.sensorType = sensorType;
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

        retrieveSensorDataFromDB();
        return view;
    }

    void populateTable(){
        SensorDataListAdapter sensorDataListAdapter = new SensorDataListAdapter(sensorDataList, sensorType);
        listView.setAdapter(sensorDataListAdapter);
    }
    void retrieveSensorDataFromDB(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("data");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snap : dataSnapshot.getChildren()){
                    SensorData sensorData = snap.getValue(SensorData.class);
                    Log.d("FIREBASE", sensorData.getTime()+"");
                    sensorDataList.add(sensorData);
                }
                populateTable();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        db.addValueEventListener(eventListener);
    }

    class SensorDataListAdapter extends BaseAdapter {

        private List<SensorData> sensorDataList;
        private String sensorType;

        public SensorDataListAdapter(List<SensorData> sensorDataList, String sensorType) {
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

            if(sensorType.equals("TEMPERATURE-C"))
                sensorData.setText(sensorDataList.get(position).getTemperatureC()+"");
            else if (sensorType.equals("TEMPERATURE-F"))
                sensorData.setText(sensorDataList.get(position).getTemperatureF()+"");
            else if(sensorType.equals("UV"))
                sensorData.setText(sensorDataList.get(position).getUv()+"");
            else if(sensorType.equals("HUMIDITY"))
                sensorData.setText(sensorDataList.get(position).getHumidity()+"");
            else if(sensorType.equals("MOISTURE"))
                sensorData.setText(sensorDataList.get(position).getSoil()+"");

            return convertView;
        }
    }
}
