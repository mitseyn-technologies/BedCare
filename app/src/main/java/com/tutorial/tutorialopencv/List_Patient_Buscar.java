package com.tutorial.tutorialopencv;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

public class List_Patient_Buscar extends ListActivity {

    public static final String P_MODO  = "modo" ;
    public static final int P_VISUALIZAR = 551 ;

    private PacientesDbAdapter dbAdapter;
    private Cursor cursor;
    private PacientesCursorAdapter pacienteAdapter;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patient_buscar);

        lista =  findViewById(android.R.id.list);

        dbAdapter = new PacientesDbAdapter(this);
        dbAdapter.open();

        consultar();
    }


    private void consultar()
    {
        cursor = dbAdapter.getCursor();
        startManagingCursor(cursor);
        pacienteAdapter = new PacientesCursorAdapter(this, cursor);
        lista.setAdapter(pacienteAdapter);
    }

    private void visualizar(long id)
    {
        // Llamamos a la Actividad HipotecaFormulario indicando el modo visualización y el identificador del registro
        Intent i = new Intent(List_Patient_Buscar.this, ShowPatient.class);
        i.putExtra(P_MODO, P_VISUALIZAR);
        i.putExtra(PacientesDbAdapter.P_COLUMNA_ID, id);

        startActivityForResult(i, P_VISUALIZAR);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        visualizar(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.paciente, menu);
        return true;
    }

    private void go_ToMenuMatient()
    {
        Intent to_MenuPatient = new Intent(List_Patient_Buscar.this,Menu_Patient.class);
        startActivity(to_MenuPatient);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMenuMatient();

    }
}
