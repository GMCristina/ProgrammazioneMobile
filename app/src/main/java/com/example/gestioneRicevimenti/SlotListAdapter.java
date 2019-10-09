package com.example.gestioneRicevimenti;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SlotListAdapter extends ArrayAdapter<String> {

    private Activity context;

    private ArrayList<String> eventIdRicevimento;

    private ArrayList<String> eventDate;

    private ArrayList<String> eventHours;

    private String previous = "";
    private int previous_position = -1;


    public SlotListAdapter(Activity context, ArrayList<String> id_ricevimento, ArrayList<String> date, ArrayList<String> hours){

        super(context,R.layout.slots_listview_layout, id_ricevimento);
        this.context = context;

        this.eventIdRicevimento = id_ricevimento;
        this.eventDate = date;
        this.eventHours = hours;
    }


    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i("GETVIEW", Integer.toString(position));
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.slots_listview_layout, null);


        //this code gets references to objects in the events_listview_layout.xml file
        TextView separatorText = (TextView) rowView.findViewById(R.id.separator2);
        TextView oraText = (TextView) rowView.findViewById(R.id.ora2);

        //this code sets the values of the objects to values from the arrays
        separatorText.setText(eventDate.get(position));
        oraText.setText(eventHours.get(position));

        if (previous_position < position) { // scroll down
            if (previous.equals(eventDate.get(position))) {
                separatorText.setVisibility(View.GONE);
            }
            previous = eventDate.get(position);
        } else { // scroll up
            if (position > 0 && (eventDate.get(position-1).equals(eventDate.get(position)))) {
                separatorText.setVisibility(View.GONE);
            }
        }

        /*
        if(previous.equals(eventDate.get(position))){
            separatorText.setVisibility(View.GONE);
        }
        previous = eventDate.get(position);
         */



        previous_position = position;
        return rowView;

    }

    public String getIdRicevimento(int position) {
        String st = eventIdRicevimento.get(position);
        return st;
    }
}
