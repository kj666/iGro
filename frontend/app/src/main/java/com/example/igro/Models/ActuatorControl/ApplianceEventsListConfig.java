package com.example.igro.Models.ActuatorControl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.igro.R;


import java.util.List;

public class ApplianceEventsListConfig extends ArrayAdapter<ApplianceControlEvents>{

    private Activity context;

    private List<ApplianceControlEvents> applianceEventList;

    public ApplianceEventsListConfig(Activity context, List<ApplianceControlEvents> applianceEventList){
        super(context, R.layout.appliance_trigger_list_layout, applianceEventList);
        this.context = context;
        this.applianceEventList = applianceEventList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.appliance_trigger_list_layout, null, true);

        TextView listItemCounterTextView = (TextView)listViewItem.findViewById(R.id.listItemCounterTextView);
        TextView listItemDateTextView = (TextView)listViewItem.findViewById(R.id.listItemDateTextView);
        TextView listItemUserNameTextView = (TextView)listViewItem.findViewById(R.id.listItemUserNameTextView);
        TextView listItemOnOffStatusTextView = (TextView)listViewItem.findViewById(R.id.listItemOnOffStatusTextView);

        ApplianceControlEvents applianceEvent = applianceEventList.get(position);

        String onOffStr;
        Boolean onOff = applianceEvent.getEventOnOff();
        if(onOff){
            onOffStr = "ON";
        }else{
            onOffStr = "OFF";
        }

        listItemCounterTextView.setText(String.valueOf(position+1));
        listItemDateTextView.setText(applianceEvent.getEventDateTime());
        listItemUserNameTextView.setText( applianceEvent.getUserEmailWhoTriggeredEvent());
        listItemOnOffStatusTextView.setText(onOffStr);

        return listViewItem;
    }

}
