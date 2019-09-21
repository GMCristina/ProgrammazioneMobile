package com.example.gestioneRicevimenti;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentEventDialog extends Dialog implements View.OnClickListener {

    Context con;
    String id_ricevimento;

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
        TextView docente = findViewById(R.id.docente);
        TextView corso = findViewById(R.id.corso);
        TextView data = findViewById(R.id.data);
        TextView inizio = findViewById(R.id.inizio);
        TextView fine = findViewById(R.id.fine);
        TextView oggetto = findViewById(R.id.oggetto);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonElimina:

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
}
