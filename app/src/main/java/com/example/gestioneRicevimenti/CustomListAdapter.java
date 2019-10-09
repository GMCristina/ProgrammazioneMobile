package com.example.gestioneRicevimenti;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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

    private ArrayList<String> eventStatus;

    private String previous = "";



    public CustomListAdapter(Activity context, ArrayList<String> date, ArrayList<String> event, ArrayList<String> hours, ArrayList<String> id, ArrayList<String> stato){

        super(context,R.layout.events_listview_layout, date);
        this.context = context;

        this.eventDate = date;
        this.eventName = event;
        this.eventHours = hours;
        this.eventId = id;
        this.eventStatus = stato;
    }



    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Log.i("GETVIEW",Integer.toString(position));


        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.events_listview_layout, null);

        //this code gets references to objects in the events_listview_layout.xml file
        TextView separatorText = (TextView) rowView.findViewById(R.id.separator);
        TextView eventText = (TextView) rowView.findViewById(R.id.event);
        TextView oraText = (TextView) rowView.findViewById(R.id.ora);
        TextView status = (TextView) rowView.findViewById(R.id.status);

        //this code sets the values of the objects to values from the arrays
        separatorText.setText(eventDate.get(position));
        if(eventName!=null)
            eventText.setText(eventName.get(position));
        oraText.setText(eventHours.get(position));

        if (position > 0 && (eventDate.get(position-1).equals(eventDate.get(position)))) {
            separatorText.setVisibility(View.GONE);
        }

        switch (eventStatus.get(position)) {
            case "2": // richiesto
                status.setText("Richiesto");
                break;
            case "3": // rifiutato
                status.setText("Rifiutato");
                break;
            case "1": // confermato
                status.setText("Prenotato");
                break;
            case "4": // confermato
                status.setText("Approvato");
                break;
            case "0": // libero
                status.setText("Libero");
              //  status.setTextColor(Color.GREEN);
                break;
            case "5":
                status.setText("Eliminato"); //eliminato da studente
                break;

            case "6":
                status.setText("Deprecato");
                break;
        }

       // previous_position = position;
        return rowView;

    }

    public String getOggetto(int position) {
        String st = eventId.get(position);
        return st;
    }

    public String getStatus (int position){
        String s = eventStatus.get(position);
        return s;
    }

}
