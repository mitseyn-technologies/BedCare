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

public class List_Patient_Eliminar extends ListActivity {

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

    private void borrar(final long id)
    {
        /**
         * Borramos el registro con confirmación
         */
        AlertDialog.Builder dialogEliminar = new AlertDialog.Builder(this);

        dialogEliminar.setIcon(android.R.drawable.ic_dialog_alert);
        dialogEliminar.setTitle(getResources().getString(R.string.eliminar_titulo));
        dialogEliminar.setMessage(getResources().getString(R.string.eliminar_mensaje));
        dialogEliminar.setCancelable(false);

        dialogEliminar.setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int boton) {
                dbAdapter.delete(id);
                Toast.makeText(List_Patient_Eliminar.this, R.string.eliminar_confirmacion, Toast.LENGTH_SHORT).show();
                /**
                 * Devolvemos el control
                 */
                setResult(RESULT_OK);
                finish();
                startActivity(getIntent());
            }
        });

        dialogEliminar.setNegativeButton(android.R.string.no, null);

        dialogEliminar.show();

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        borrar(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.paciente, menu);
        return true;
    }

    private void go_ToMenuPatient()
    {
        Intent to_MenuPatient = new Intent(List_Patient_Eliminar.this,Menu_Patient.class);
        startActivity(to_MenuPatient);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMenuPatient();

    }
}
