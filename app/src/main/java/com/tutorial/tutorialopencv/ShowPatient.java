package com.tutorial.tutorialopencv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPatient extends Activity {

    private TextView txtName, txtApellido, txtID,txtCedula,txtEdad,
                     txtFecha,txtMedico,txtObservacion,txtCausa;
    private Button btn_StatePatient;
    private Button btn_DeletePatient;

    private Pacientes paciente;

    private PacientesDbAdapter dbAdapter;
    private Cursor cursor;
    //
    // Modo del formulario
    //
    private int modo ;

    //
    // Identificador del registro que se edita cuando la opción es MODIFICAR
    //
    private long id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();

        if (extra == null) return;

        InitElements();
        Actionelements();

        dbAdapter = new PacientesDbAdapter(this);
        dbAdapter.open();

        if (extra.containsKey(PacientesDbAdapter.P_COLUMNA_ID))
        {
            id = extra.getLong(PacientesDbAdapter.P_COLUMNA_ID);
            consultar(id);
        }

        //
        // Establecemos el modo del formulario
        //
        establecerModo(extra.getInt(List_Patient_Buscar.P_MODO));
    }

    private void InitElements()
    {
        //Botones
        btn_StatePatient = findViewById(R.id.btnState);
        btn_DeletePatient = findViewById(R.id.btnDelete);
        //TextView
        txtID = findViewById(R.id.txt_id);
        txtName = findViewById(R.id.txt_name);
        txtApellido = findViewById(R.id.txt_apellido);
        txtCedula = findViewById(R.id.txt_cedula);
        txtEdad = findViewById(R.id.txt_edad);
        txtFecha = findViewById(R.id.txt_fechaIngreso);
        txtMedico = findViewById(R.id.txt_medico);
        txtObservacion = findViewById(R.id.txt_observacion);
        txtCausa = findViewById(R.id.txt_CausaP);

    }

    private void Actionelements()
    {
        btn_StatePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_toImageProcessing();
            }
        });

        btn_DeletePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrar(id);
            }
        });
    }

    private void establecerModo(int m)
    {
        this.modo = m ;

        if (modo == List_Patient_Buscar.P_VISUALIZAR)
        {
            this.setTitle(txtName.getText().toString());
            this.setEdicion(false);
        }
    }
    private void consultar(long id)
    {
        //
        // Consultamos el centro por el identificador
        //
        cursor = dbAdapter.getRegistro(id);
        txtID.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_ID)));

        txtName.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_NOMBRE)));
        txtApellido.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_APELLIDO)));
        txtCedula.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_CEDULA)));
        txtEdad.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_EDAD)));
        txtFecha.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_FECHA)));
        txtMedico.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_MEDICO)));
        txtObservacion.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_OBSERVACION)));
        txtCausa.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_CAUSA)));

    }

    private void setEdicion(boolean opcion)
    {
        txtName.setEnabled(opcion);
        txtApellido.setEnabled(opcion);
        txtCedula.setEnabled(opcion);
        txtEdad.setEnabled(opcion);
        txtFecha.setEnabled(opcion);
        txtMedico.setEnabled(opcion);
        txtObservacion.setEnabled(opcion);
        txtCausa.setEnabled(opcion);
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
                Toast.makeText(ShowPatient.this, R.string.eliminar_confirmacion, Toast.LENGTH_SHORT).show();
                /**
                 * Devolvemos el control
                 */
                setResult(RESULT_OK);
                finish();
            }
        });

        dialogEliminar.setNegativeButton(android.R.string.no, null);

        dialogEliminar.show();

    }

    private void go_toImageProcessing()
    {
        Intent go_ImageP = new Intent(ShowPatient.this,imageProcessing.class);
        startActivity(go_ImageP);
        this.finish();
    }

    private void go_toListPatient()
    {
        Intent go_ListP= new Intent(ShowPatient.this, List_Patient_Buscar.class);
        startActivity(go_ListP);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_toListPatient();
    }
}
