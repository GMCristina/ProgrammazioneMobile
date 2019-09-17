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

    private ArrayList<String> eventName;

    private ArrayList<String> eventDate;

    public CustomListAdapter(Activity context, ArrayList<String> event, ArrayList<String> date){

        super(context,R.layout.events_listview_layout, event);
        this.context = context;
        this.eventName = event;
        this.eventDate = date;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.events_listview_layout, null);

        //this code gets references to objects in the events_listview_layout.xml file
        TextView eventText = (TextView) rowView.findViewById(R.id.event);
        TextView oraText = (TextView) rowView.findViewById(R.id.ora);

        //this code sets the values of the objects to values from the arrays
        eventText.setText(eventName.get(position));
        oraText.setText(eventDate.get(position));

        return rowView;

    }
}
