package com.example.igro.Models.ActuatorControl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.igro.HumidityActivity;
import com.example.igro.MoistureActivity;
import com.example.igro.R;
import com.example.igro.TemperatureActivity;
import com.example.igro.UvIndexActivity;


import java.util.List;

public class HeaterEventConfig extends ArrayAdapter<HeaterControlEvents>{

    private Activity context;
    private TemperatureActivity tempContext;
    private HumidityActivity humContext;
    private MoistureActivity moistContext;
    private UvIndexActivity uvContext;

    private List<HeaterControlEvents> heaterEventList;

    public HeaterEventConfig(Activity context, List<HeaterControlEvents> heaterEventList){
        super(context, R.layout.appliance_trigger_list_layout, heaterEventList);
        this.context = context;
        this.heaterEventList = heaterEventList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.appliance_trigger_list_layout, null, true);

        TextView listItemCounterTextView = (TextView)listViewItem.findViewById(R.id.listItemCounterTextView);
        TextView listItemDateTextView = (TextView)listViewItem.findViewById(R.id.listItemDateTextView);
        TextView listItemOnOffStatusTextView = (TextView)listViewItem.findViewById(R.id.listItemOnOffStatusTextView);

        HeaterControlEvents heaterEvent = heaterEventList.get(position);

        String onOffStr;
        Boolean onOff = heaterEvent.getHeaterEventOnOff();
        if(onOff){
            onOffStr = "ON";
        }else{
            onOffStr = "OFF";
        }

        listItemCounterTextView.setText(String.valueOf(position));
        listItemDateTextView.setText(heaterEvent.getHeaterEventDateTime());
        listItemOnOffStatusTextView.setText(onOffStr);

        return listViewItem;
    }

}
