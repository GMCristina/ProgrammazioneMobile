package com.example.gestioneRicevimenti;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class ProfEventDialog extends Dialog implements View.OnClickListener{

    Context con;
    String id_ricevimento;
    String status;
    String azione;
    ProgressBar pbDownload;

    public ProfEventDialog (@NonNull Context context) {
        super(context);
        con = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prof_event_dialog_layout);
        setTitle("Dettagli Ricevimento");
        Button btnFine = findViewById(R.id.btnFine);
        Button btnElimina = findViewById(R.id.btnElimina);
        Button btnConferma = findViewById(R.id.btnConferma);
        Button btnRifiuta = findViewById(R.id.btnRifiuta);
        TextView tvStudente = findViewById(R.id.tvStudente);
        TextView tvCorso = findViewById(R.id.tvCorso);
        TextView tvOggetto = findViewById(R.id.tvOggetto);
        TextView txtStudente = findViewById(R.id.txtStudente);
        TextView txtCorso = findViewById(R.id.txtCorso);
        TextView txtData = findViewById(R.id.txtData);
        TextView txtInizio = findViewById(R.id.txtInizio);
        TextView txtFine = findViewById(R.id.txtFine);
        TextView txtOggetto = findViewById(R.id.txtOggetto);

        pbDownload = findViewById(R.id.pbProfDialog);

        btnElimina.setOnClickListener(this);
        btnConferma.setOnClickListener(this);
        btnRifiuta.setOnClickListener(this);
        btnFine.setOnClickListener(this);



        switch (status){
            case "0":
                tvStudente.setVisibility(View.GONE);
                txtStudente.setVisibility(View.GONE);
                tvCorso.setVisibility(View.GONE);
                txtCorso.setVisibility(View.GONE);
                tvOggetto.setVisibility(View.GONE);
                txtOggetto.setVisibility(View.GONE);
                btnConferma.setVisibility(View.GONE);
                btnRifiuta.setVisibility(View.GONE);
                break;

            case "1":
                btnConferma.setVisibility(View.GONE);
                btnRifiuta.setVisibility(View.GONE);
                break;

            case "2":
                btnElimina.setVisibility(View.GONE);
                break;

            case "3":
                btnConferma.setVisibility(View.GONE);
                btnRifiuta.setVisibility(View.GONE);
                break;

            case "4":
                btnConferma.setVisibility(View.GONE);
                btnRifiuta.setVisibility(View.GONE);
                break;
            case "5":
                btnConferma.setVisibility(View.GONE);
                btnRifiuta.setVisibility(View.GONE);
                break;

            case "6":
                btnConferma.setVisibility(View.GONE);
                btnRifiuta.setVisibility(View.GONE);
                break;

            default:
                break;
        }

        DownloadEventDetail downloadEvent = new DownloadEventDetail(id_ricevimento);
        downloadEvent.execute(txtStudente,txtCorso,txtData,txtInizio,txtFine,txtOggetto);

    }

    @Override
    public void onClick(View v) {
        HandleEvent handleEvent;
        switch (v.getId()){
            case R.id.btnElimina:
                azione = "Elimina";
                handleEvent = new HandleEvent(azione);
                handleEvent.execute();
                dismiss();
                break;

            case R.id.btnConferma:
                azione = "Conferma";
                handleEvent = new HandleEvent(azione);
                handleEvent.execute();
                dismiss();
                break;

            case R.id.btnRifiuta:
                azione = "Rifiuta";
                handleEvent = new HandleEvent(azione);
                handleEvent.execute();
                dismiss();
                break;

            case R.id.btnFine:
                dismiss();
                break;
        }

    }

    public void getDataAndShow(String id, String stato){
        id_ricevimento = id;
        status = stato;
        this.show();
    }

    private class DownloadEventDetail extends AsyncTask<TextView, Void, JSONObject> {

        String id_ricevimento = null;
        TextView studente;
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
            studente = textViews[0];
            corso = textViews[1];
            data = textViews[2];
            inizio = textViews[3];
            fine = textViews[4];
            oggetto = textViews[5];
            if (id_ricevimento != null) {
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
                } finally {
                    if (client != null) {
                        client.disconnect();
                    }
                }
            }

            return json_data;
        }

        @Override
        protected void onPostExecute(JSONObject json_data) {

            if (json_data != null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        studente.setText(value.getString("snome") + " " + value.getString("scognome"));
                        data.setText(value.getString("giorno"));
                        inizio.setText(value.getString("inizio"));
                        fine.setText(value.getString("fine"));
                        corso.setText((value.getString("corso")));
                        oggetto.setText(value.getString("oggetto"));
                    } catch (JSONException e) {

                    }
                }
            }
            pbDownload.setVisibility(View.GONE);
        }
    }

    private class HandleEvent extends AsyncTask<Void, Void, String>{

        String azione = "";

        public HandleEvent(String action){
            super();
            this.azione = action;
        }

        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection client = null;
            String json_string = "";
            try {
                URL url = new URL("http://pmapp.altervista.org/gestione_ricevimento.php?" + "id=" + id_ricevimento + "&azione=" + azione);
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                client.setDoInput(true);
                InputStream in = client.getInputStream();
                json_string = ReadResponse.readStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally{
                if (client!= null){
                    client.disconnect();
                }
            }
            return json_string;
        }

        @Override
        protected void onPostExecute(String s) {
            switch (s){
                case "-1":
                    Toast.makeText(con, "Errore: operazione fallita (-1)", Toast.LENGTH_LONG).show();
                    break;

                default:
                    Toast.makeText(con, "Operazione eseguita!", Toast.LENGTH_LONG).show();
                    break;
            }


        }
    }

}
