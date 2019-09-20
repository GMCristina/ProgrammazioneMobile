package com.example.gestioneRicevimenti;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
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
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class StudentHomePageActivity extends AppCompatActivity {

    ListView list;
    HttpURLConnection client = null;
    DownloadEvent downloadevent;
    CustomListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            list = findViewById(R.id.eventList);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                }
            });

            final SwipeRefreshLayout swipe = findViewById(R.id.swipeRefreshLayout);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.i("REFRESH:","refresh");
                    downloadevent = new DownloadEvent();
                    downloadevent.execute(list);
                    listAdapter.notifyDataSetChanged();
                    swipe.setRefreshing(false);

                }
            });
           // downloadevent = new DownloadEvent();
           // downloadevent.execute(list);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadevent = new DownloadEvent();
        downloadevent.execute(list);
        Log.i("RESUME:","onresume");
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
            case R.id.swipeRefreshLayout:
                downloadevent = new DownloadEvent();
                downloadevent.execute(list);
                break;

            default: Log.i ("MENU","Default switch item");
        }
        return true;
    }

    private class DownloadEvent extends AsyncTask<ListView, Void, JSONObject> {

        ListView list;

        private JSONObject convert2JSON(String json_data){
            JSONObject obj = null;
            try {
                obj = new JSONObject(json_data);
                Log.d("My App", obj.toString());
            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + json_data + "\"");
            }
            return obj;
        }

        @Override
        protected JSONObject doInBackground(ListView... listViews) {
            HttpURLConnection client = null;
            JSONObject json_data = convert2JSON("");
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
                    json_data = convert2JSON(json_string);
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
            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        eventDateArray.add(value.getString("giorno"));
                        eventNameArray.add(value.getString("nome") + " " + value.getString("cognome"));
                        eventHoursArray.add(value.getString("inizio") + " - " + value.getString("fine"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            listAdapter = new CustomListAdapter(StudentHomePageActivity.this, eventDateArray, eventNameArray, eventHoursArray);
            list.setAdapter(listAdapter);
        }
    }

}
