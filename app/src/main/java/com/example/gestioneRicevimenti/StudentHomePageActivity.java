package com.example.gestioneRicevimenti;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        CalendarView calendar = findViewById(R.id.calendarView);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String data = "date=" + Integer.toString(year) + "-" + Integer.toString(month+1) + "-" + Integer.toString(dayOfMonth);
                try {
                    URL url = new URL("http://pmapp.altervista.org/elenco_ricevimenti.php?" + data + "&" + "id=1");
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("GET");
                    client.setDoInput(true);
                    InputStream in = client.getInputStream();
                    String json_string = ReadResponse.readStream(in);
                    JSONObject json_data = convert2JSON(json_string);
                    fill_listview(json_data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally{
                    if (client!= null){
                        client.disconnect();
                    }
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.home_menu,menu);
       return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout : break;
            case R.id.info : break;
            default: Log.i ("MENU","Default switch item");
        }
        return true;
    }

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

    private void fill_listview(JSONObject json_data){
        ArrayList<String> eventNameArray = new ArrayList<>();
        ArrayList<String> eventDateArray = new ArrayList<>();
        if(json_data!=null) {
            Iterator<String> iter = json_data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONObject value = json_data.getJSONObject(key);
                    eventNameArray.add(value.getString("nome"));
                    eventDateArray.add(value.getString("cognome"));
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        }
        CustomListAdapter listAdapter = new CustomListAdapter(this, eventNameArray, eventDateArray);
            list.setAdapter(listAdapter);
    }
}
