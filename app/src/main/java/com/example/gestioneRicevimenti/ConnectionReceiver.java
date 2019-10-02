package com.example.gestioneRicevimenti;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();
        final boolean isConnected = netinfo!=null && netinfo.isConnectedOrConnecting();
        if(!isConnected){
            AlertDialog.Builder adbuilder = new AlertDialog.Builder(context);
            adbuilder.setTitle("Connessione Internet assente");
            adbuilder.setMessage("Attiva la connesione dati e riprova");
            adbuilder.setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    }
            });

            final AlertDialog alertdialog = adbuilder.create();
            alertdialog.show();
            alertdialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NetworkInfo netinfo = cm.getActiveNetworkInfo();
                    final boolean isConnected = netinfo!=null && netinfo.isConnectedOrConnecting();
                    if(isConnected) {
                        alertdialog.dismiss();
                    }
                }
            });
        }
            //Toast.makeText(context, "Connessione Internet assente",Toast.LENGTH_SHORT).show();
    }
}
