package com.example.gestioneRicevimenti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
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

public class ProfHomePageActivity extends AppCompatActivity {

    ProfEventDialog sed;


    ConnectionReceiver receiver;

    ListView list;
    CustomListAdapter listAdapter;
    Spinner sp;

    ArrayList<String> eventDateArray;
    ArrayList<String> eventNameArray;
    ArrayList<String> eventHoursArray;
    ArrayList<String> eventIdArray;
    ArrayList<String> eventStatusArray;

    int[] stati_pos = {-1, 0, 1, 2, 4, 5};
    boolean first_download = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_home_page);

        receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sp = findViewById(R.id.spinnerStati);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = findViewById(R.id.slotList);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(receiver.CheckConnection(ProfHomePageActivity.this)) {
                    String id_ric = listAdapter.getOggetto(i);
                    String status = listAdapter.getStatus(i);
                    Log.i("CLICK",status + "-" + id_ric);
                    sed = new ProfEventDialog(ProfHomePageActivity.this);
                    sed.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            DownloadSlotProf downloadSlot = new DownloadSlotProf();
                            downloadSlot.execute(list);
                            listAdapter.notifyDataSetChanged();
                            sp.setSelection(0);
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                    sed.getDataAndShow(id_ric,status);
                    Window w = sed.getWindow();
                   // w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    w.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(receiver.CheckConnection(ProfHomePageActivity.this)) {
                    Intent i = new Intent(ProfHomePageActivity.this, ProfInsertSlotActivity.class);
                    startActivity(i);
                }
            }
        });

        final SwipeRefreshLayout swipe = findViewById(R.id.swipeRefreshLayout2);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(receiver.CheckConnection(ProfHomePageActivity.this)) {
                    DownloadSlotProf downloadSlot = new DownloadSlotProf();
                    downloadSlot.execute(list);
                    listAdapter.notifyDataSetChanged();
                    sp.setSelection(0);
                }
                swipe.setRefreshing(false);

            }
        });

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(position == 0){
                    if(first_download) {
                        DownloadSlotProf downloadSlot = new DownloadSlotProf();
                        downloadSlot.execute(list);
                        first_download = false;
                    } else {
                        listAdapter = new CustomListAdapter(ProfHomePageActivity.this, eventDateArray, eventNameArray, eventHoursArray, eventIdArray, eventStatusArray);
                        if(list != null)
                            list.setAdapter(listAdapter);
                    }
                } else {
                    ArrayList<String> filtredEventDateArray = new ArrayList<>();
                    ArrayList<String> filtredEventNameArray = new ArrayList<>();
                    ArrayList<String> filtredEventHoursArray = new ArrayList<>();
                    ArrayList<String> filtredEventIdArray = new ArrayList<>();
                    ArrayList<String> filtredEventStatusArray = new ArrayList<>();

                    for (int i = 0; i < eventIdArray.size(); i++) {
                        if (eventStatusArray.get(i).equals(Integer.toString(stati_pos[position]))) {
                            filtredEventIdArray.add(eventIdArray.get(i));
                            filtredEventNameArray.add(eventNameArray.get(i));
                            filtredEventDateArray.add(eventDateArray.get(i));
                            filtredEventHoursArray.add(eventHoursArray.get(i));
                            filtredEventStatusArray.add(eventStatusArray.get(i));
                        }
                    }

                    listAdapter = new CustomListAdapter(ProfHomePageActivity.this, filtredEventDateArray, filtredEventNameArray, filtredEventHoursArray, filtredEventIdArray, filtredEventStatusArray);
                    if (list != null)
                        list.setAdapter(listAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        DownloadSlotProf downloadSlot = new DownloadSlotProf();
        downloadSlot.execute(list);


    }

    @Override
    protected void onResume() {
        super.onResume();
        DownloadSlotProf download = new DownloadSlotProf();
        download.execute(list);
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
                SharedPreferences shared = getSharedPreferences(file, Context.MODE_PRIVATE);
                shared.edit().clear().apply();
                Intent i = new Intent(this,LoginActivity.class);
                startActivity(i);
                break;
            case R.id.info : break;
            case R.id.refresh:
                if(receiver.CheckConnection(ProfHomePageActivity.this)) {

                    DownloadSlotProf downloadSlot = new DownloadSlotProf();
                    downloadSlot.execute(list);
                    sp.setSelection(0);

                }
                break;
            case R.id.home:
                break;

            default: Log.i ("MENU","Default switch item");
        }
        return true;
    }

    private class DownloadSlotProf extends AsyncTask<ListView, Void, JSONObject> {

        ListView list;



        @Override
        protected JSONObject doInBackground(ListView... listViews) {
            HttpURLConnection client = null;
            JSONObject json_data = ReadResponse.convert2JSON("");
            list = listViews[0];
            String file = getPackageName() + "login_file";
            SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
            String id = sp.getString("id_utente", null);
            if(id != null){
                try {
                    URL url = new URL("http://pmapp.altervista.org/elenco_ricevimenti_prof.php?" + "id_professore=" + id);
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
            eventDateArray = new ArrayList<>();
            eventNameArray = new ArrayList<>();
            eventHoursArray = new ArrayList<>();
            eventIdArray = new ArrayList<>();
            eventStatusArray = new ArrayList<>();


            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        eventDateArray.add(value.getString("giorno"));
                        if(value.getString("stato").equals("0"))
                            eventNameArray.add("Nessuno studente");
                        else
                            eventNameArray.add(value.getString("nome") + " " + value.getString("cognome"));
                        eventHoursArray.add(value.getString("inizio") + " - " + value.getString("fine"));
                        eventIdArray.add(value.getString("id_ricevimento"));
                        eventStatusArray.add(value.getString("stato"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            listAdapter = new CustomListAdapter(ProfHomePageActivity.this, eventDateArray, eventNameArray, eventHoursArray, eventIdArray, eventStatusArray);
            if(list != null)
                list.setAdapter(listAdapter);
        }
    }
}
