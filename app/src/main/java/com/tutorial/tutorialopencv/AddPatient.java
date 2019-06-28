package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class AddPatient extends AppCompatActivity {

    private EditText medico, fecha,nombre,apellido,cedula,edad,postracion,observacions;
    private Button btnIngresar;
    private Pacientes paciente, p_New;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_patient);
        setTitle(R.string.addPatient);
        ///paciente = new Pacientes(this);
        p_New = new Pacientes();
        InitElements();
        ActionsElements();

    }



    private void InitElements() {

        medico = findViewById(R.id.edit_medico);
        fecha = findViewById(R.id.edit_fecha);
        nombre = findViewById(R.id.edit_nombre);
        apellido = findViewById(R.id.editApellido);
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

                p_New.setID(12);
                p_New.setName(nombre.getText().toString());
                p_New.setApellido(apellido.getText().toString());
                p_New.setCedula(cedula.getText().toString());
                p_New.setMedico_aCargo(medico.getText().toString());
                p_New.setEdad(Integer.parseInt(edad.getText().toString()));
                p_New.setFecha_ingreso(fecha.getText().toString());
                p_New.setObservacion(observacions.getText().toString());
                p_New.setCausa_postracion(postracion.getText().toString());

                //To pass:
                Bundle bundle = new Bundle();
                Intent extra = new Intent(getApplicationContext(),List_Patient.class);
                bundle.putSerializable("MyNewPerson", p_New);
                extra.putExtras(bundle);
                startActivity(extra);


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
