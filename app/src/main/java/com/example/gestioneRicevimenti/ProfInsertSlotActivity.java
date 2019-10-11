package com.example.gestioneRicevimenti;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;

public class ProfInsertSlotActivity extends AppCompatActivity implements View.OnClickListener {

    DatePickerDialog d;
    TimePicker tp;

    Spinner spdurataTot;
    Spinner spdurataSlot;

    Button btndata;

    ConnectionReceiver receiver;

    String id_prof;
    String data;
    String inizio;
    String durataTot;
    String durataSlot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prof_insert_slot);

        receiver = new ConnectionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        Button btninserisci = findViewById(R.id.btnInserisci);
        Button btnannulla= findViewById(R.id.btnAnnulla);
        btndata = findViewById(R.id.btndata);
        spdurataTot = findViewById(R.id.spinnerdurataTot);
        spdurataSlot = findViewById(R.id.spinnerdurataSlot);
        btninserisci.setOnClickListener(this);
        btnannulla.setOnClickListener(this);

        durataTot = getResources().getStringArray(R.array.durataTot)[0];
        String[] st = durataTot.split(" ");
        if(st[1].equals("h"))
            durataTot = Integer.toString((int)(Float.parseFloat(st[0]) * 60));
        else
            durataTot = st[0];

        durataSlot = getResources().getStringArray(R.array.durataSlot)[0];
        String[] st1 = durataSlot.split(" ");
        if(st1[1].equals("h"))
            durataSlot = Integer.toString((Integer.parseInt(st1[0])) * 60);
        else
            durataSlot = st1[0];



        spdurataTot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                durataTot = spdurataTot.getSelectedItem().toString();
                String[] str = durataTot.split(" ");
                if(str[1].equals("h"))
                    durataTot = Integer.toString((int)(Float.parseFloat(str[0]) * 60));
                else
                    durataTot = str[0];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spdurataSlot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                durataSlot = spdurataSlot.getSelectedItem().toString();
                String[] str1 = durataSlot.split(" ");
                if(str1[1].equals("h"))
                    durataSlot = Integer.toString((Integer.parseInt(str1[0])) * 60);
                else
                    durataSlot = str1[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String file = getPackageName() + "login_file";
        SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
        id_prof = sp.getString("id_utente", "no_id");

        Calendar c = Calendar.getInstance();
        btndata.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + Integer.toString(c.get(Calendar.MONTH)+1) + "/" + c.get(Calendar.YEAR));
        btndata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.show();
            }
        });

        d = new DatePickerDialog(ProfInsertSlotActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                data = dayOfMonth + "-" + Integer.toString(month+1) + "-" + year;
                btndata.setText(data);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        d.getDatePicker().setMinDate(c.getTimeInMillis());

        data = c.get(Calendar.DAY_OF_MONTH) + "-" +  Integer.toString(c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR);

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
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnInserisci:
                if(receiver.CheckConnection(ProfInsertSlotActivity.this)) {
                    InsertSlot insert = new InsertSlot();
                    insert.execute();
                }
                this.finish();
                break;
            case R.id.btnAnnulla:
                this.finish();
                break;
        }

    }

    private class InsertSlot extends AsyncTask< Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection client = null;
            String res_code = "";
            JSONObject json_data = ReadResponse.convert2JSON("");

            if ((!TextUtils.isEmpty(id_prof)) && (!TextUtils.isEmpty(data)) && (!TextUtils.isEmpty(inizio)) && (!TextUtils.isEmpty(durataSlot))&& (!TextUtils.isEmpty(durataTot))) {
                res_code = "1";
                try {
                    URL url = new URL("http://pmapp.altervista.org/inserimento_slot.php");
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("POST");

                    client.setDoOutput(true);
                    client.setDoInput(true);

                    OutputStream out = new BufferedOutputStream(client.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    String req = URLEncoder.encode("id_docente", "UTF-8")
                            + "=" + URLEncoder.encode(id_prof, "UTF-8");
                    req += "&" + URLEncoder.encode("data", "UTF-8") + "="
                            + URLEncoder.encode(data, "UTF-8");
                    req += "&" + URLEncoder.encode("inizio", "UTF-8") + "="
                            + URLEncoder.encode(inizio, "UTF-8");
                    req += "&" + URLEncoder.encode("durata", "UTF-8") + "="
                            + URLEncoder.encode(durataTot, "UTF-8");
                    req += "&" + URLEncoder.encode("durata_slot", "UTF-8") + "="
                            + URLEncoder.encode(durataSlot, "UTF-8");
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

            switch (s) {
                case "-1":
                    Toast.makeText(ProfInsertSlotActivity.this, "Inserimento fallita: riprova (-1)", Toast.LENGTH_LONG).show();
                    break; //richiesta fallita

                case "1":
                    Toast.makeText(ProfInsertSlotActivity.this, "Inserimento effettuata correttamente", Toast.LENGTH_LONG).show();
                    break; // richiesta effettuata

                case "":
                    Toast.makeText(ProfInsertSlotActivity.this, "Inserimento fallita: dati mancanti", Toast.LENGTH_LONG).show();
                    break; // mancano dati

                default:
                    break;
            }


        }
    }
}
