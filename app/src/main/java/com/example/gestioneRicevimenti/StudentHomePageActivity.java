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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class StudentHomePageActivity extends AppCompatActivity {

    ConnectionReceiver receiver;

    ListView list;
    HttpURLConnection client = null;
    DownloadEvent downloadevent;
    CustomListAdapter listAdapter;
    StudentEventDialog sed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        String file = getPackageName() + "login_file";
        SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
        String idutente = sp.getString("id_utente", "no_id");
        if (idutente.equals("no_id")) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
        } else {

            setContentView(R.layout.activity_student_home_page_activity);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(StudentHomePageActivity.this, StudentBookSlotActivity.class);
                    startActivity(i);
                }
            });



            list = findViewById(R.id.slotList);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String id = listAdapter.getOggetto(i);
                    sed = new StudentEventDialog(StudentHomePageActivity.this);
                    sed.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            downloadevent = new DownloadEvent();
                            downloadevent.execute(list);
                            listAdapter.notifyDataSetChanged();
                        }
                    });
                    sed.dataShow(id);
                    Window w = sed.getWindow();
                    w.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            });




            final SwipeRefreshLayout swipe = findViewById(R.id.swipeRefreshLayout2);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if(receiver.CheckConnection(StudentHomePageActivity.this)) {
                        downloadevent = new DownloadEvent();
                        downloadevent.execute(list);
                        listAdapter.notifyDataSetChanged();
                    }
                    swipe.setRefreshing(false);

                }
            });
            downloadevent = new DownloadEvent();
            downloadevent.execute(list);

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("OnResume", "onresume");
        downloadevent = new DownloadEvent();
        downloadevent.execute(list);
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
                if(receiver.CheckConnection(StudentHomePageActivity.this)) {
                    downloadevent = new DownloadEvent();
                    downloadevent.execute(list);
                }
                break;
            case R.id.home:
                break;

            default: Log.i ("MENU","Default switch item");
        }
        return true;
    }

    private class DownloadEvent extends AsyncTask<ListView, Void, JSONObject> {

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
                    URL url = new URL("http://pmapp.altervista.org/elenco_ricevimenti.php?" + "id=" + id);
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
            ArrayList<String> eventStatusArray = new ArrayList<>();

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
                        eventStatusArray.add(value.getString("stato"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            listAdapter = new CustomListAdapter(StudentHomePageActivity.this, eventDateArray, eventNameArray, eventHoursArray, eventIdArray, eventStatusArray);
            if(list != null)
                list.setAdapter(listAdapter);
        }
    }

}
