package com.example.gestioneRicevimenti;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionReceiver extends BroadcastReceiver {
    Boolean previous = true;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        final boolean isConnected = netinfo!=null && netinfo.isConnectedOrConnecting();
        String file = context.getPackageName() + "login_file";
        SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean("isConnected", isConnected);
        e.apply();

        if(!isConnected && previous!=isConnected) {
            AlertDialog.Builder adbuilder = new AlertDialog.Builder(context);
            adbuilder.setTitle("Connessione Internet assente");
            adbuilder.setMessage("Attiva la connesione dati e riprova");
            adbuilder.setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                }
            });
            AlertDialog alertdialog = adbuilder.create();
            alertdialog.show();
            previous = isConnected;
        }
    }

    Boolean CheckConnection (Context context){
        String file = context.getPackageName() + "login_file";
        SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        Boolean isConnected = sp.getBoolean("isConnected", true);
        if(!isConnected){
            AlertDialog.Builder adbuilder = new AlertDialog.Builder(context);
            adbuilder.setTitle("Connessione Internet assente");
            adbuilder.setMessage("Attiva la connesione dati e riprova");
            adbuilder.setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog alertdialog = adbuilder.create();
            alertdialog.show();

        }
        return isConnected;
    }
}
