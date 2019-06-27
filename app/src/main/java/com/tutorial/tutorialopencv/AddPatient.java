package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddPatient extends AppCompatActivity {

    private EditText medico, fecha,nombre,apellido,cedula,edad,postracion,observacions;
    private Button btnIngresar;
    private Pacientes paciente;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_patient);
        setTitle(R.string.addPatient);


        InitElements();
        ActionsElements();

    }



    private void InitElements() {

        medico = findViewById(R.id.edit_medico);
        fecha = findViewById(R.id.edit_fecha);
        nombre = findViewById(R.id.edit_nombre);
        cedula = findViewById(R.id.edit_cedula);
        edad = findViewById(R.id.edit_edad);
        observacions = findViewById(R.id.edit_observacion);
        postracion = findViewById(R.id.edit_postracion);

        btnIngresar = findViewById(R.id.btnIngresar);
    }

    private void ActionsElements()
    {
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               paciente = new Pacientes(
                        9,nombre.getText().toString(),"",cedula.getText().toString(),observacions.getText().toString(),postracion.getText().toString(),
                        medico.getText().toString(),Integer.parseInt(edad.getText().toString()),fecha.getText().toString()
                );

               Intent dataToList = new Intent(AddPatient.this,List_Patient.class);
                dataToList.putExtra("parametro", paciente);
            }
        });
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
