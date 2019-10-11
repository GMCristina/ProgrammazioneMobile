package com.example.gestioneRicevimenti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etmatricola;
    EditText etpsw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        String matricola = etmatricola.getText().toString().trim();
        String passw = etpsw.getText().toString().trim();
        HttpURLConnection client = null;
        JSONObject json_data = null;
        String data = "matricola=" + matricola + "&password=" + passw;
        try {
            URL url = new URL("http://pmapp.altervista.org/login.php?"+ data);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            client.setDoInput(true);
            InputStream in = client.getInputStream();
            String json_string = ReadResponse.readStream(in);
            json_data = convert2JSON(json_string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if (client!= null){
                client.disconnect();
            }
        }

        if(json_data!=null) {
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
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        } else {
            Toast.makeText(this, "LoginActivity fallito, matricola o/e password errate!", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject convert2JSON(String json_data){
        JSONObject obj = null;
        try {
            obj = new JSONObject(json_data);
            // Log.d("My App", obj.toString());
        } catch (Throwable t) {
            // Log.e("My App", "Could not parse malformed JSON: \"" + json_data + "\"");
        }
        return obj;
    }

}
