package com.example.igro.Models.ActuatorControl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.igro.R;


import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.LongToIntFunction;

import io.opencensus.common.Timestamp;

public class ApplianceEventsListConfig extends ArrayAdapter<ApplianceControlEvents>{

    private int timeBeforeLastTrigger = 0;
    private  String timeSinceStr;
    Long timeSinceLastTriggerSeconds;
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
        TextView listItemUserNameTextView = listViewItem.findViewById(R.id.listItemUserNameTextView);
        TextView listItemTimeSinceLastTriggerTextView = listViewItem.findViewById(R.id.listItemTimeSinceLastTriggerTextView);
        TextView listItemOnOffStatusTextView = (TextView)listViewItem.findViewById(R.id.listItemOnOffStatusTextView);

        ApplianceControlEvents applianceEvent = applianceEventList.get(position);

        String onOffStr;
        Boolean onOff = applianceEvent.getEventOnOff();
        if(onOff){
            onOffStr = "ON";
        }else{
            onOffStr = "OFF";
        }

        Long thisTrigger = applianceEvent.getEventUnixEpoch();
        Long lastTrigger = applianceEvent.getPreviousEventUnixEpoch();
        if(!lastTrigger.toString().isEmpty()){
            timeSinceLastTriggerSeconds = thisTrigger-lastTrigger;

            timeSinceStr = timeSinceLastTriggerSeconds.toString();
        }else{
            lastTrigger = thisTrigger;
            timeSinceLastTriggerSeconds = thisTrigger-lastTrigger;

            timeSinceStr = timeSinceLastTriggerSeconds.toString();
        }


        Date timeSinceFormatted = new Date (timeSinceLastTriggerSeconds);
        SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss");
        String timeSinceLastTriggerString = sd.format(timeSinceFormatted);

        Long timeSinceMinutes = timeSinceLastTriggerSeconds/60;
        Long remainderSeconds = timeSinceLastTriggerSeconds - timeSinceMinutes*60;
        Long timeSinceHours = timeSinceMinutes/60;
        Long remainderMinutes = timeSinceMinutes - timeSinceHours*60;

       String timeSinceLastTriggerStr = timeSinceLastTriggerSeconds.toString();
       String timeSinceMinutesStr = timeSinceMinutes.toString();
       String timeSinceHoursStr = timeSinceHours.toString();
       String remainderSecondsStr = remainderSeconds.toString();
       String remainderMinutesStr = remainderMinutes.toString();
// string time diff formated by hand
        String timeSinceFormattedByHand = timeSinceHoursStr+"hr "+remainderMinutesStr+"m "+remainderSecondsStr+"s";

        listItemCounterTextView.setText(String.valueOf(position+1));
        listItemDateTextView.setText(applianceEvent.getEventDateTime());
        listItemUserNameTextView.setText(applianceEvent.getEventUserEmail());
       listItemTimeSinceLastTriggerTextView.setText(timeSinceFormattedByHand);
        listItemOnOffStatusTextView.setText(onOffStr);

        return listViewItem;
    }

}
