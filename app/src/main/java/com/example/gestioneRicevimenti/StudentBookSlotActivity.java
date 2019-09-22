package com.example.gestioneRicevimenti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class StudentBookSlotActivity extends AppCompatActivity {

    Spinner spdocente;
    ArrayAdapter <String> spinnerAdapter;
    ListView listslot;
    CustomListAdapter listAdapter;

    String id_docente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_book_slot);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spdocente = findViewById(R.id.spinnerdocente);
        listslot = findViewById(R.id.slotList);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(StudentBookSlotActivity.this, StudentNewEventActivity.class);
                startActivity(j);
            }
        });

      //  DownloadSpinnerDocente downloadspinnerdocente = new DownloadSpinnerDocente ();
      //  downloadspinnerdocente.execute(spdocente);

        spdocente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private class DownloadSpinnerDocente extends AsyncTask< Spinner, Void, JSONObject> {

        Spinner sp;

        @Override
        protected JSONObject doInBackground(Spinner... spinners) {
            HttpURLConnection client = null;
            JSONObject json_data = ReadResponse.convert2JSON("");
            sp = spinners[0];
            String file = getPackageName() + "login_file";
            SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
            String id_studente = sp.getString("id_utente", null);
            if(id_studente != null){
                try {
                    URL url = new URL("http://pmapp.altervista.org/elenco_docenti.php?" + "id=" + id_studente);
                    // php per estrarre tutti i docenti dello studente
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("GET");
                    client.setDoInput(true);
                    InputStream in = client.getInputStream();
                    String json_string = ReadResponse.readStream(in);
                    json_data = ReadResponse.convert2JSON(json_string);
                } catch (IOException e) {
                    e.printStackTrace();
                    json_data = null;
                }
                finally{
                    if (client!= null){
                        client.disconnect();
                    }
                }
            }

            return json_data;
        }

        @Override
        protected void onPostExecute(JSONObject json_data) {
            ArrayList<String> spinnerDocenteArray = new ArrayList<>();

            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        spinnerDocenteArray.add(value.getString("nome") + value.getString("cognome"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            spinnerAdapter = new ArrayAdapter<String>(StudentBookSlotActivity.this, R.layout.spinner_row,spinnerDocenteArray);
            sp.setAdapter(spinnerAdapter);
        }
    }

    private class DownloadSlot extends AsyncTask<ListView, Void, JSONObject> {

        ListView list;

        @Override
        protected JSONObject doInBackground(ListView... listviews) {
            HttpURLConnection client = null;
            JSONObject json_data = ReadResponse.convert2JSON("");
            list = listviews[0];

            if(id_docente != null){
                try {
                    URL url = new URL("http://pmapp.altervista.org/elenco_slot.php?" + "id=" + id_docente);
                    // php per estrarre tutti gli slot del docente
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("GET");
                    client.setDoInput(true);
                    InputStream in = client.getInputStream();
                    String json_string = ReadResponse.readStream(in);
                    json_data = ReadResponse.convert2JSON(json_string);
                } catch (IOException e) {
                    e.printStackTrace();
                    json_data = null;
                }
                finally{
                    if (client!= null){
                        client.disconnect();
                    }
                }
            }

            return json_data;
        }

        @Override
        protected void onPostExecute(JSONObject json_data) {
            ArrayList<String> eventDateArray = new ArrayList<>();
            ArrayList<String> eventNameArray = new ArrayList<>();
            ArrayList<String> eventHoursArray = new ArrayList<>();
            ArrayList<String> eventIdArray = new ArrayList<>();

            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        eventDateArray.add(value.getString("giorno"));
                        eventNameArray.add(value.getString("nome") + " " + value.getString("cognome"));
                        eventHoursArray.add(value.getString("inizio") + " - " + value.getString("fine"));
                        eventIdArray.add(value.getString("id_ricevimento"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            listAdapter = new CustomListAdapter(StudentBookSlotActivity.this, eventDateArray, eventNameArray, eventHoursArray, eventIdArray);
            list.setAdapter(listAdapter);
        }
    }


}



