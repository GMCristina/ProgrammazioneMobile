package com.example.gestioneRicevimenti;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

public class ProfEventDialog extends Dialog implements View.OnClickListener{

    Context con;

    public ProfEventDialog (@NonNull Context context) {
        super(context);
        con = context;
    }
    @Override
    public void onClick(View v) {

    }
}
