package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ShowPatient extends AppCompatActivity {

    private TextView txtName, txtApellido, txtID,txtCedula,txtEdad,txtFecha,txtMedico,txtObservacion,txtCausa;
    private Button btn_StatePatient;
    private Pacientes paciente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_patient);
        setTitle(R.string.showPatient);

        InitElements();
        Actionelements();
    }

    private void InitElements()
    {
        //Boton
        btn_StatePatient = findViewById(R.id.btnState);
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

        paciente = (Pacientes)getIntent().getExtras().getSerializable("parametro");

        //Fill texto

        txtID.setText(String.valueOf(paciente.getID()));
        txtName.setText(paciente.getName());
        txtApellido.setText(paciente.getApellido());
        txtCedula.setText(paciente.getCedula());
        txtEdad.setText(String.valueOf(paciente.getEdad()));
        txtFecha.setText(paciente.getFecha_ingreso().toString());
        txtMedico.setText(paciente.getMedico_aCargo());
        txtObservacion.setText(paciente.getObservacion());
        txtCausa.setText(paciente.getCausa_postracion());

        Toast.makeText(getApplicationContext(),paciente.getName(),Toast.LENGTH_LONG).show();
    }

    private void Actionelements()
    {
        btn_StatePatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_toImageProcessing();
            }
        });
    }

    private void go_toImageProcessing()
    {
        Intent go_ImageP = new Intent(ShowPatient.this,imageProcessing.class);
        startActivity(go_ImageP);
        this.finish();
    }

    private void go_toListPatient()
    {
        Intent go_ListP= new Intent(ShowPatient.this, List_Patient.class);
        startActivity(go_ListP);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_toListPatient();
    }
}
