package com.tutorial.tutorialopencv;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class Menu_Patient extends AppCompatActivity {

    private ImageButton btn_Search,btn_Edit,btn_Create,btn_Delete;
    private Vibrator vibrator;

    public static final int P_CREAR = 552;
    public static final int P_EDITAR = 553 ;
    public static final String P_MODO = "modo";
    public long tiempo = 100; //en milisegundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_patient);
        setTitle(R.string.menuPatient);

        InitElements();
        ActiosElement();
    }


    private void InitElements()
    {
        btn_Search  =findViewById(R.id.searchPatient);
        btn_Create =findViewById(R.id.addPatient);
        btn_Edit = findViewById(R.id.editPatient);
        btn_Delete =findViewById(R.id.deletePatient);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        dbHelper dbHelper = new dbHelper(getBaseContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Toast.makeText(getBaseContext(), "Base de datos preparada", Toast.LENGTH_SHORT).show();
    }

    private void ActiosElement()
    {

        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(tiempo);
                }
                    go_ToAddPatient();

            }
        });

        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(tiempo);
                } go_ToListPatient();
            }
        });

        btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(tiempo);
                }
                go_ToListPatientDelete();
            }
        });

        btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(tiempo);
                }
                go_ToListPatientEdit();
            }
        });
    }

    private void go_ToAddPatient()
    {
        Intent to_AddPatient = new Intent(Menu_Patient.this, AddPatient.class);
        startActivity(to_AddPatient);
        Intent i = new Intent(Menu_Patient.this, AddPatient.class);
        i.putExtra(P_MODO, P_CREAR);
        startActivityForResult(i, P_CREAR);
        this.finish();
    }

    private void go_ToMainMenu()
    {
        Intent to_MainMenu = new Intent(Menu_Patient.this,Main_Menu.class);
        startActivity(to_MainMenu);
        this.finish();
    }
    private void go_ToListPatient()
    {
        Intent to_ListPatient = new Intent(Menu_Patient.this, List_Patient_Buscar.class);
        startActivity(to_ListPatient);
        this.finish();
    }
    private void go_ToListPatientDelete()
    {
        Intent to_ListPatient = new Intent(Menu_Patient.this, List_Patient_Eliminar.class);
        startActivity(to_ListPatient);
        this.finish();
    }
    private void go_ToListPatientEdit()
    {
        Intent to_ListPatient = new Intent(Menu_Patient.this, List_Patient_Editar.class);
        startActivity(to_ListPatient);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMainMenu();
    }
}
