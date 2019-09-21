package com.example.gestioneRicevimenti;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private Activity context;

    private ArrayList<String> eventDate;

    private ArrayList<String> eventName;

    private ArrayList<String> eventHours;

    private ArrayList<String> eventId;

    private String previous = "";

    public CustomListAdapter(Activity context, ArrayList<String> date, ArrayList<String> event, ArrayList<String> hours, ArrayList<String> id){

        super(context,R.layout.events_listview_layout, event);
        this.context = context;

        this.eventDate = date;
        this.eventName = event;
        this.eventHours = hours;
        this.eventId = id;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.events_listview_layout, null);

        //this code gets references to objects in the events_listview_layout.xml file
        TextView separatorText = (TextView) rowView.findViewById(R.id.separator);
        TextView eventText = (TextView) rowView.findViewById(R.id.event);
        TextView oraText = (TextView) rowView.findViewById(R.id.ora);

        //this code sets the values of the objects to values from the arrays
        separatorText.setText(eventDate.get(position));
        eventText.setText(eventName.get(position));
        oraText.setText(eventHours.get(position));

        if(previous.equals(eventDate.get(position))){
            separatorText.setVisibility(View.GONE);
        }
        previous = eventDate.get(position);



        return rowView;

    }

    public String getOggetto(int position) {
        String st = eventId.get(position);
        return st;
    }
}
