package com.example.gestioneRicevimenti;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class StudentNewEventActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayAdapter<String> spinnerAdapter;
    Spinner spcorso;
    Spinner spdurata;

    String id_docente;
    String id_studente;
    String id_corso;

    String docente;
    String corso;

    String data;
    String inizio;
    String durata;
    String oggetto = "";

    ArrayList<String> spinnerIdCorsiArray;


    EditText etoggetto;
    Button btndata;

    DatePickerDialog d;
    TimePicker tp;

    ConnectionReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_new_event);

        receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        Button btnrichiedi = findViewById(R.id.btnInserisci);
        Button btnannulla= findViewById(R.id.btnAnnulla);
        btnrichiedi.setOnClickListener(this);
        btnannulla.setOnClickListener(this);

        spinnerIdCorsiArray = new ArrayList<>();


        etoggetto = findViewById(R.id.newoggetto);

        btndata = findViewById(R.id.btndata);
        spcorso = findViewById(R.id.spinnercorso);
        spdurata = findViewById(R.id.spinnerdurata);




        durata = getResources().getStringArray(R.array.durataricevimento)[0];
        String[] st = durata.split(" ");
        if(st[1].equals("h"))
            durata = Integer.toString((Integer.parseInt(st[0])) * 60);
        else
            durata = st[0];

        TextView twDocente = findViewById(R.id.txtDocente);

        // id_studente

        String file = getPackageName() + "login_file";
        SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
        id_studente = sp.getString("id_utente", "no_id");

        Calendar c = Calendar.getInstance();
        btndata.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + Integer.toString(c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.YEAR));
        btndata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.show();
            }
        });

        etoggetto = findViewById(R.id.newoggetto);

      etoggetto.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

          }

          @Override
          public void afterTextChanged(Editable s) { //nb ogni volta che modifica ma onFocus non funziona
              oggetto = etoggetto.getText().toString();
          }
      });

        d = new DatePickerDialog(StudentNewEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                data = dayOfMonth + "/" + Integer.toString(month+1) + "/" + year;
                btndata.setText(data);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        d.getDatePicker().setMinDate(c.getTimeInMillis());

        data = c.get(Calendar.DAY_OF_MONTH) + "/" +  Integer.toString(c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);

        // id_docente

        Intent i = getIntent();
        Bundle b = i.getBundleExtra("id");
        id_docente = b.getString("id_docente");
        docente = b.getString("docente");

        twDocente.setText(docente);

        tp = findViewById(R.id.timepicker);
        tp.setIs24HourView(true);
        tp.setHour(c.get(Calendar.HOUR_OF_DAY));
        tp.setMinute(c.get(Calendar.MINUTE));
        inizio = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);



        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                inizio = Integer.toString(hourOfDay) + ":" + Integer.toString(minute);

            }
        });

        spcorso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               TextView item = view.findViewById(R.id.spinnerrow);
               corso = item.getText().toString();
               id_corso = spinnerIdCorsiArray.get(position);
               corso = spinnerIdCorsiArray.get(position);
           }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });

        spdurata.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                durata = spdurata.getSelectedItem().toString();
                String[] st = durata.split(" ");
                if(st[1].equals("h"))
                    durata = Integer.toString((Integer.parseInt(st[0])) * 60);
                else
                    durata = st[0];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        DownloadSpinnerCorso download_docenti = new DownloadSpinnerCorso();
        download_docenti.execute(spcorso);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInserisci:
                if(receiver.CheckConnection(StudentNewEventActivity.this)) {
                    SlotRequest slot_request = new SlotRequest();
                    slot_request.execute();
                    this.finish();
                }
                break;
            case R.id.btnAnnulla:
                this.finish();
                break;
        }
    }

    private class DownloadSpinnerCorso extends AsyncTask< Spinner, Void, JSONObject> {

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
                    URL url = new URL("http://pmapp.altervista.org/elenco_corsi_studente_docente.php?" + "id_studente=" + id_studente + "&id_professore=" + id_docente);
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
            ArrayList<String> spinnerCorsiArray = new ArrayList<>();

            if(json_data!=null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        spinnerCorsiArray.add(value.getString("nome"));
                        spinnerIdCorsiArray.add(value.getString("id_corso"));
                    } catch (JSONException e) {

                    }
                }
            }

            if(!spinnerIdCorsiArray.isEmpty()) {
                corso = spinnerIdCorsiArray.get(0);
            }
            spinnerAdapter = new ArrayAdapter<String>(StudentNewEventActivity.this, R.layout.spinner_row,spinnerCorsiArray);
            sp.setAdapter(spinnerAdapter);
        }
    }

    private class SlotRequest extends AsyncTask< Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection client = null;
            String res_code = "";
            JSONObject json_data = ReadResponse.convert2JSON("");

            String file = getPackageName() + "login_file";
            SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
            String id_studente = sp.getString("id_utente", null);

            if((!TextUtils.isEmpty(id_studente)) && (!TextUtils.isEmpty(id_docente)) && (!TextUtils.isEmpty(id_corso)) && (!TextUtils.isEmpty(data)) && (!TextUtils.isEmpty(inizio)) && (!TextUtils.isEmpty(durata))) {

               try {
                    URL url = new URL("http://pmapp.altervista.org/richiesta_nuovo_ricevimento.php");
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("POST");

                    client.setDoOutput(true);
                    client.setDoInput(true);

                    OutputStream out = new BufferedOutputStream(client.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    String req = URLEncoder.encode("id_docente", "UTF-8")
                            + "=" + URLEncoder.encode(id_docente, "UTF-8");
                    req += "&" + URLEncoder.encode("id_studente", "UTF-8") + "="
                            + URLEncoder.encode(id_studente, "UTF-8");
                    req += "&" + URLEncoder.encode("id_corso", "UTF-8") + "="
                            + URLEncoder.encode(id_corso, "UTF-8");
                    req += "&" + URLEncoder.encode("data", "UTF-8") + "="
                            + URLEncoder.encode(data, "UTF-8");
                    req += "&" + URLEncoder.encode("inizio", "UTF-8") + "="
                            + URLEncoder.encode(inizio, "UTF-8");
                    req += "&" + URLEncoder.encode("durata", "UTF-8") + "="
                            + URLEncoder.encode(durata, "UTF-8");
                    req += "&" + URLEncoder.encode("oggetto", "UTF-8") + "="
                            + URLEncoder.encode(oggetto, "UTF-8");

                    writer.write(req);
                    writer.flush();
                    writer.close();
                    out.close();

                    InputStream in = client.getInputStream();
                    res_code = ReadResponse.readStream(in).trim();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (client != null) {
                        client.disconnect();
                    }
                }
            }
            return res_code;
        }

        @Override
        protected void onPostExecute(String s) {
            switch (s){
                case "1":
                    Toast.makeText(StudentNewEventActivity.this, "Richiesta effettuata correttamente", Toast.LENGTH_LONG).show();
                    break;

                case "-2":
                    Toast.makeText(StudentNewEventActivity.this, "Richiesta fallita: riprova (-2)", Toast.LENGTH_LONG).show();
                    break;

                case "-3":
                    Toast.makeText(StudentNewEventActivity.this, "Richiesta fallita: Hai già richiesto un ricevimento con questo docente in questo giorno", Toast.LENGTH_LONG).show();
                    break;


                case "":
                    Toast.makeText(StudentNewEventActivity.this, "Richiesta fallita: dati mancanti", Toast.LENGTH_LONG).show();
                    break;

                default:
                   break;
            }


        }
    }


}
