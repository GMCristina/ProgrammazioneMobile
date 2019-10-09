package com.example.gestioneRicevimenti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class StudentEventDialog extends Dialog implements View.OnClickListener {

    Context con;
    String id_ricevimento;

    ProgressBar progBar;

    public StudentEventDialog(@NonNull Context context) {
        super(context);
        con = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_event_dialog_layout);
        setTitle("Dettagli Ricevimento");
        Button btnElimina = findViewById(R.id.buttonElimina);
        Button btnFine = findViewById(R.id.buttonFine);
        btnElimina.setOnClickListener(this);
        btnFine.setOnClickListener(this);

        progBar = findViewById(R.id.progressBar3);
        TextView docente = findViewById(R.id.docente);
        TextView corso = findViewById(R.id.corso);
        TextView data = findViewById(R.id.data);
        TextView inizio = findViewById(R.id.inizio);
        TextView fine = findViewById(R.id.fine);
        TextView oggetto = findViewById(R.id.oggetto);
        DownloadEventDetail ded = new DownloadEventDetail(id_ricevimento);
        ded.execute(docente, corso, data, inizio, fine, oggetto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonElimina:
                AlertDialog.Builder adbuilder = new AlertDialog.Builder(con);
                adbuilder.setTitle("Cancella Prenotazione");
                adbuilder.setMessage("Confermi di voler cancellare definitivamente la prenotazione effettuata?");
                adbuilder.setPositiveButton("Sì", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HttpURLConnection client = null;
                        try {
                            URL url = new URL("http://pmapp.altervista.org/cancella_ricevimento.php?" + "id=" + id_ricevimento);
                            client = (HttpURLConnection) url.openConnection();
                            client.setRequestMethod("GET");
                            client.setDoInput(true);
                            InputStream in = client.getInputStream();
                            String json_string = ReadResponse.readStream(in);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finally{
                            if (client!= null){
                                client.disconnect();
                            }
                        }
                        dismiss();
                    }
                });
                adbuilder.setNegativeButton("No", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertdialog = adbuilder.create();
                alertdialog.show();
                break;
            case R.id.buttonFine:
                dismiss();
                break;
        }

    }
    public void dataShow(String id){
        id_ricevimento = id;
        this.show();
    }


    private class DownloadEventDetail extends AsyncTask<TextView, Void, JSONObject> {

        String id_ricevimento = null;
        TextView docente;
        TextView corso;
        TextView data;
        TextView inizio;
        TextView fine;
        TextView oggetto;

        public DownloadEventDetail(String id_ricevimento) {
            super();
            this.id_ricevimento = id_ricevimento;
        }

        @Override
        protected JSONObject doInBackground(TextView... textViews) {
            HttpURLConnection client = null;
            JSONObject json_data = ReadResponse.convert2JSON("");
            docente = textViews[0];
            corso = textViews[1];
            data = textViews[2];
            inizio = textViews[3];
            fine = textViews[4];
            oggetto = textViews[5];
            if(id_ricevimento != null){
                try {
                    URL url = new URL("http://pmapp.altervista.org/ricevimento.php?" + "id_ricevimento=" + id_ricevimento);
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
                        docente.setText(value.getString("nome") + " " + value.getString("cognome"));
                        data.setText(value.getString("giorno"));
                        inizio.setText(value.getString("inizio"));
                        fine.setText(value.getString("fine"));
                        corso.setText((value.getString("corso")));
                        oggetto.setText(value.getString("oggetto"));
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            progBar.setVisibility(View.GONE);
        }
    }

}
