package com.example.gestioneRicevimenti;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etmatricola;
    EditText etpsw;

    String matricola;
    String passw;

    ConnectionReceiver receiver;


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
            setContentView(R.layout.login);

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);


            etmatricola = findViewById(R.id.matricola);
            etpsw = findViewById(R.id.password);
            Button btnaccedi = findViewById(R.id.btnAccedi);
            btnaccedi.setOnClickListener(this);

        } else {
            switch (sp.getString("tipo_utente","")){
                case "s":
                    Intent i = new Intent (LoginActivity.this ,StudentHomePageActivity.class);
                    startActivity(i);
                    break;
                case "p":
                    Intent j = new Intent (LoginActivity.this ,ProfHomePageActivity.class);
                    startActivity(j);
                    break;
                default:
            }

        }
    }


    @Override
    public void onClick(View v) {
        matricola = etmatricola.getText().toString().trim();
        passw = etpsw.getText().toString().trim();
        if(receiver.CheckConnection(LoginActivity.this)) {

            //LoginRequest loginTask = new LoginRequest();
            //loginTask.execute();

            HttpURLConnection client = null;
            JSONObject json_data = null;
            String data = "matricola=" + matricola + "&password=" + passw;
            try {
                URL url = new URL("http://pmapp.altervista.org/login.php?" + data);
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                client.setDoInput(true);
                InputStream in = client.getInputStream();
                String json_string = ReadResponse.readStream(in);
                json_data = ReadResponse.convert2JSON(json_string);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (client != null) {
                    client.disconnect();
                }
            }

            if (json_data != null) {
                Iterator<String> iter = json_data.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        JSONObject value = json_data.getJSONObject(key);
                        String file = getPackageName() + "login_file";
                        SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
                        SharedPreferences.Editor e = sp.edit();
                        e.putString("matricola", matricola);
                        e.putString("password", passw);
                        e.putString("id_utente", value.getString("id"));
                        e.putString("tipo_utente", value.getString("tipo"));
                        e.apply();
                        switch (sp.getString("tipo_utente", "")) {
                            case "s":
                                Intent i = new Intent(LoginActivity.this, StudentHomePageActivity.class);
                                startActivity(i);
                                break;
                            case "p":
                                Intent j = new Intent(LoginActivity.this, ProfHomePageActivity.class);
                                startActivity(j);
                                break;
                            default:
                        }
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            } else {
                Toast.makeText(this, "LoginActivity fallito, matricola o/e password errate!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoginRequest extends AsyncTask< Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            HttpURLConnection client = null;
            JSONObject json_data = null;
            if((!TextUtils.isEmpty(matricola)) && (!TextUtils.isEmpty(passw))){
                try {
                    URL url = new URL("http://pmapp.altervista.org/login_post.php");
                    client = (HttpURLConnection) url.openConnection();
                    client.setRequestMethod("POST");
                    client.setDoOutput(true);
                    client.setDoInput(true);
                    OutputStream out = new BufferedOutputStream(client.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    String req = URLEncoder.encode("matricola", "UTF-8")
                            + "=" + URLEncoder.encode(matricola, "UTF-8");
                    req += "&" + URLEncoder.encode("password", "UTF-8") + "="
                            + URLEncoder.encode(passw, "UTF-8");
                    writer.write(req);
                    writer.flush();
                    writer.close();
                    out.close();
                    InputStream in = client.getInputStream();
                    String json_string = ReadResponse.readStream(in);
                    json_data = ReadResponse.convert2JSON(json_string);
                } catch (Exception e) {
                    e.printStackTrace();
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
                            String file = getPackageName() + "login_file";
                            SharedPreferences sp = getSharedPreferences(file, Context.MODE_PRIVATE);
                            SharedPreferences.Editor e = sp.edit();
                            e.putString("matricola", matricola);
                            e.putString("password", passw);
                            e.putString("id_utente", value.getString("id"));
                            e.putString("tipo_utente", value.getString("tipo"));
                            e.apply();
                            switch (sp.getString("tipo_utente", "")) {
                                case "s":
                                    Intent i = new Intent(LoginActivity.this, StudentHomePageActivity.class);
                                    startActivity(i);
                                    break;
                                case "p":
                                    Intent j = new Intent(LoginActivity.this, ProfHomePageActivity.class);
                                    startActivity(j);
                                    break;
                                default:
                            }
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "LoginActivity fallito, matricola o/e password errate!", Toast.LENGTH_SHORT).show();
                }
            }
        }
}
