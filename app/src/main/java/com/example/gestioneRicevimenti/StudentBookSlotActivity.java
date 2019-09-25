package com.example.gestioneRicevimenti;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    String id_docente = "";
    ArrayList<String> id_professori = new ArrayList<>();
    ArrayList<String> spinnerDocenteArray = new ArrayList<>();

    String docente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_book_slot);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spdocente = findViewById(R.id.spinnerdocente);
        listslot = findViewById(R.id.slotList);

        listslot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder adbuilder = new AlertDialog.Builder(StudentBookSlotActivity.this);
                adbuilder.setTitle("Prenota Slot");
                adbuilder.setMessage("Confermi di voler prenotare lo slot selezionato?");
                adbuilder.setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                adbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertdialog = adbuilder.create();
                alertdialog.show();

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!id_docente.equals("")) {
                    Intent j = new Intent(StudentBookSlotActivity.this, StudentNewEventActivity.class);
                    Bundle b = new Bundle();
                    b.putString("id_docente", id_docente);
                    b.putString("docente", docente);
                    j.putExtra("id", b);
                    startActivity(j);
                }
            }
        });

        DownloadSpinnerDocente downloadspinnerdocente = new DownloadSpinnerDocente ();
        downloadspinnerdocente.execute(spdocente);

        spdocente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id_docente = id_professori.get(position);
                docente = spinnerDocenteArray.get(position);
                DownloadSlot ds = new DownloadSlot();
                ds.execute(listslot);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final SwipeRefreshLayout swipe = findViewById(R.id.swipeRefreshLayout2);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DownloadSlot ds = new DownloadSlot();
                ds.execute(listslot);
                //listAdapter.notifyDataSetChanged();
                swipe.setRefreshing(false);

            }
        });


    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu,menu);
        if ( menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout :
                String file = getPackageName() + "login_file";
                SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
                sp.edit().clear().apply();
                Intent i = new Intent(this,LoginActivity.class);
                startActivity(i);
                break;
            case R.id.info : break;
            case R.id.refresh:
                DownloadSlot ds = new DownloadSlot();
                ds.execute(listslot);
                break;
            case R.id.home:
                Intent j = new Intent(StudentBookSlotActivity.this, StudentHomePageActivity.class);
                startActivity(j);
                break;

            default: Log.i ("MENU","Default switch item");
        }
        return true;
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
                    URL url = new URL("http://pmapp.altervista.org/professori.php?" + "id_studente=" + id_studente);
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

            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        id_professori.add(value.getString("id_professore"));
                        spinnerDocenteArray.add(value.getString("nome") + " " + value.getString("cognome"));
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
                    URL url = new URL("http://pmapp.altervista.org/ricevimenti_liberi.php?" + "id_professore=" + id_docente);
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
            ArrayList<String> eventIdRicevimentoArray = new ArrayList<>();
            ArrayList<String> eventGiornoArray = new ArrayList<>();
            ArrayList<String> eventInizioFineArray = new ArrayList<>();
            ArrayList<String> eventIdProfessoreArray = new ArrayList<>();

            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        eventIdRicevimentoArray.add(value.getString("id_ricevimento"));
                        eventIdProfessoreArray.add(value.getString("id_professore"));
                        eventGiornoArray.add(value.getString("giorno"));
                        eventInizioFineArray.add(value.getString("inizio") + " - " + value.getString("fine"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            listAdapter = new CustomListAdapter(StudentBookSlotActivity.this,   eventGiornoArray, null, eventInizioFineArray, eventIdProfessoreArray);
            list.setAdapter(listAdapter);
        }
    }


}



