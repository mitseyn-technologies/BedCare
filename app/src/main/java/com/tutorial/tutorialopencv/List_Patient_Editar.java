package com.tutorial.tutorialopencv;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class List_Patient_Editar extends ListActivity {

    public static final String P_MODO  = "modo" ;
    public static final int P_EDITAR = 553 ;

    private PacientesDbAdapter dbAdapter;
    private Cursor cursor;
    private PacientesCursorAdapter pacienteAdapter;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patient_eliminar);

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

    private void editar(long id)
    {
        // Llamamos a la Actividad HipotecaFormulario indicando el modo visualización y el identificador del registro
        Intent i = new Intent(List_Patient_Editar.this, EditPatient.class);
        i.putExtra(P_MODO, P_EDITAR);
        i.putExtra(PacientesDbAdapter.P_COLUMNA_ID, id);

        startActivityForResult(i, P_EDITAR);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        editar(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.paciente, menu);
        return true;
    }

    private void go_ToMenuPatient()
    {
        Intent to_MenuPatient = new Intent(List_Patient_Editar.this,Menu_Patient.class);
        startActivity(to_MenuPatient);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMenuPatient();

    }
}
