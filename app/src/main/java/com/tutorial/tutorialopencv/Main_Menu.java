package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main_Menu extends AppCompatActivity {

    private Button btn_Pacientes, btn_Config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__menu);
        setTitle(R.string.mainmenu);

        InitElements();
        CreateElements();

    }

    private void InitElements()
    {

        btn_Pacientes = findViewById(R.id.btnPacientes);
        btn_Config = findViewById(R.id.btnConfig);
    }

    private void CreateElements()
    {
        btn_Pacientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_toPacientes();

            }
        });

        btn_Config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void go_toPacientes()
    {
        Intent toPatient = new Intent(Main_Menu.this,Menu_Patient.class);
        startActivity(toPatient);
        this.finish();

    }
}
