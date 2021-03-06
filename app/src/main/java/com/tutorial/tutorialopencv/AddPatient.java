package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPatient extends AppCompatActivity {

    private EditText medico, fecha,nombre,apellido,cedula,edad,postracion,observacions;
    private Button btnIngresarPaciente, btnCancelarIngreso;
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
        setContentView(R.layout.add_patient);
        //setTitle(R.string.addPatient);

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

        btnIngresarPaciente = findViewById(R.id.btnCancelar);
        btnCancelarIngreso = findViewById(R.id.btnGuardarCambios);

        dbAdapter = new PacientesDbAdapter(this);
        dbAdapter.open();
    }

    private void ActionsElements()
    {
        btnIngresarPaciente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
                go_ToMenuPatient();
            }
        });

        btnCancelarIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });
    }

    private void establecerModo(int m)
    {
        this.modo = Menu_Patient.P_CREAR;
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

        if(modo == Menu_Patient.P_CREAR)
        {
            dbAdapter.insert(reg);
            Toast.makeText(this, R.string.crear_confirmacion, Toast.LENGTH_SHORT).show();
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
        Intent to_MenuPatient = new Intent(AddPatient.this,Menu_Patient.class);
        startActivity(to_MenuPatient);
        this.finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMenuPatient();

    }

}
