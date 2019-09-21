package com.example.gestioneRicevimenti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class StudentNewEventActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayAdapter<String> spinnerAdapter;
    Spinner spdocente;
    Spinner spcorso;
    Spinner spdurata;

    String docente;
    String corso;
    String data;
    String inizio;
    String durata;
    String oggetto;

    EditText etdata;
    EditText etinizio;
    EditText etoggetto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_new_event);

        Button btnrichiedi = findViewById(R.id.btnrichiedi);
        Button btnannulla= findViewById(R.id.btnannulla);
        btnrichiedi.setOnClickListener(this);
        btnannulla.setOnClickListener(this);

        etdata = findViewById(R.id.newdata);
        etinizio = findViewById(R.id.newinizio);
        etoggetto = findViewById(R.id.newoggetto);

        spdocente = findViewById(R.id.spinnerdocente);
        spcorso = findViewById(R.id.spinnercorso);
        spdurata = findViewById(R.id.spinnerdurata);

       /* spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row);
        spinnerAdapter.add("prova1");
        spinnerAdapter.add("prova2");
        spinnerAdapter.add("prova3");
        spcorso.setAdapter(spinnerAdapter);
        spcorso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView item = view.findViewById(R.id.spinnerrow);
                corso = item.getText().toString();
                Log.i("Spinner:", corso);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                corso = "";
            }
        });



        */
       spdocente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               TextView item = view.findViewById(R.id.spinnerrow);
               docente = item.getText().toString();

             //  DownloadSpinnerData download_corso = new DownloadSpinnerData();
             //  download_corso.execute(spcorso);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

       spcorso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               TextView item = view.findViewById(R.id.spinnerrow);
               corso = item.getText().toString();
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

        spdurata.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                durata = spdurata.getSelectedItem().toString();
                Log.i("n",durata);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

      // DownloadSpinnerData download_docenti = new DownloadSpinnerData();
      // download_docenti.execute(spdocente);





    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnrichiedi:
                data = etdata.getText().toString();
                inizio = etinizio.getText().toString();
                oggetto = etoggetto.getText().toString();
                
                break;
            case R.id.btnannulla :
                this.finish();
                break;
        }
    }

    private class DownloadSpinnerData extends AsyncTask< Spinner, Void, JSONObject> {

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
                    URL url = new URL("http://pmapp.altervista.org/elenco_ricevimenti.php?" + "id=" + id_studente);
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
            ArrayList<String> spinnerDataArray = new ArrayList<>();

            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        spinnerDataArray.add(value.getString("spinner_data"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            spinnerAdapter = new ArrayAdapter<String>(StudentNewEventActivity.this, R.layout.spinner_row,spinnerDataArray);
            sp.setAdapter(spinnerAdapter);
        }
    }

}
