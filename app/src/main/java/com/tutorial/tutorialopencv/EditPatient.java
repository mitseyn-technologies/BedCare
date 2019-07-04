package com.tutorial.tutorialopencv;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class EditPatient extends AppCompatActivity {

    private EditText medico, fecha,nombre,apellido,cedula,edad,postracion,observacions;
    private Button btnGuardarCambios, btnCancelarCambios;
    private Pacientes paciente, p_New;
    private PacientesDbAdapter dbAdapter;
    private Cursor cursor;

    //Modo del formulario
    private int modo;

    //Identificador del registro que se edita cuando la opcion es MODIFICAR
    private long id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.edit_patient);

        Intent intent = getIntent();
        Bundle extra = intent.getExtras();

        if (extra == null) return;

        InitElements();
        ActionsElements();

        //Obtenemos el identificador del regustro si viene indicado
        if(extra.containsKey(PacientesDbAdapter.P_COLUMNA_ID))
        {
            id = extra.getLong(PacientesDbAdapter.P_COLUMNA_ID);
        }

        //Cargamos los datos anteriores
        ObtenerDatos(id);

        //Establecemos el modo del formulario
        establecerModo(extra.getInt(Menu_Patient.P_MODO));
    }

    private void InitElements() {

        medico = findViewById(R.id.edit_medico);
        fecha = findViewById(R.id.edit_fecha);
        nombre = findViewById(R.id.edit_nombre);
        apellido = findViewById(R.id.edit_apellido);
        cedula = findViewById(R.id.edit_cedula);
        edad = findViewById(R.id.edit_edad);
        observacions = findViewById(R.id.edit_observacion);
        postracion = findViewById(R.id.edit_causa);

        btnGuardarCambios = findViewById(R.id.btnCancelar);
        btnCancelarCambios = findViewById(R.id.btnGuardarCambios);

        dbAdapter = new PacientesDbAdapter(this);
        dbAdapter.open();
    }

    private void ActionsElements()
    {
        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
                go_ToMenuPatient();
            }
        });

        btnCancelarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });
    }

    private void ObtenerDatos(long id)
    {
        //Obtenemos los datos del formulario
        ContentValues reg = new ContentValues();

        //Se obtiene el objeto
        reg.get(PacientesDbAdapter.P_COLUMNA_NOMBRE);
        //Ahora ha yque sacar el nombre dentro de dicho objeto
    }



    private void establecerModo(int m)
    {
        this.modo = Menu_Patient.P_EDITAR;
        this.setTitle(nombre.getText().toString());
        this.setEdicion(true);
    }

    private void setEdicion(boolean opcion)
    {
        nombre.setEnabled(opcion);
        apellido.setEnabled(opcion);
        cedula.setEnabled(opcion);
        edad.setEnabled(opcion);
        fecha.setEnabled(opcion);
        medico.setEnabled(opcion);
        observacions.setEnabled(opcion);
        postracion.setEnabled(opcion);

        cursor = dbAdapter.getRegistro(id);
        nombre.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_APELLIDO)));
        apellido.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_APELLIDO)));
        cedula.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_CEDULA)));
        edad.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_EDAD)));
        fecha.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_FECHA)));
        medico.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_MEDICO)));
        observacions.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_OBSERVACION)));
        postracion.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_CAUSA)));

    }

    private void guardar()
    {
        //Obtenemos los datos del formulario
        ContentValues reg = new ContentValues();

        reg.put(PacientesDbAdapter.P_COLUMNA_NOMBRE, nombre.getText().toString());
        reg.put(PacientesDbAdapter.P_COLUMNA_APELLIDO, apellido.getText().toString());
        reg.put(PacientesDbAdapter.P_COLUMNA_CEDULA, cedula.getText().toString());
        reg.put(PacientesDbAdapter.P_COLUMNA_EDAD, edad.getText().toString());
        reg.put(PacientesDbAdapter.P_COLUMNA_FECHA, fecha.getText().toString());
        reg.put(PacientesDbAdapter.P_COLUMNA_MEDICO, medico.getText().toString());
        reg.put(PacientesDbAdapter.P_COLUMNA_OBSERVACION, observacions.getText().toString());
        reg.put(PacientesDbAdapter.P_COLUMNA_CAUSA, postracion.getText().toString());

        if(modo == Menu_Patient.P_EDITAR)
        {
            dbAdapter.update(reg);
            Toast.makeText(this, R.string.editar_confirmacion, Toast.LENGTH_SHORT).show();
        }

        //Devolvemos el control
        setResult(RESULT_OK);
        finish();
    }

    private void cancelar()
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void go_ToMenuPatient()
    {
        Intent to_MenuPatient = new Intent(EditPatient.this,Menu_Patient.class);
        startActivity(to_MenuPatient);
        this.finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMenuPatient();

    }

}
