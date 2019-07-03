package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Menu_Patient extends AppCompatActivity {

    private Button btn_Search,btn_Edit,btn_Create,btn_delete;

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
        btn_delete =findViewById(R.id.deletePatient);

        dbHelper dbHelper = new dbHelper(getBaseContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();


        Toast.makeText(getBaseContext(), "Base de datos preparada", Toast.LENGTH_LONG).show();

    }

    private void ActiosElement()
    {

        btn_Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_ToAddPatient();
            }
        });

        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_ToListPatient();
            }
        });
    }

    private void go_ToAddPatient()
    {
        Intent to_AddPatient = new Intent(Menu_Patient.this, AddPatient.class);
        startActivity(to_AddPatient);
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
        Intent to_ListPatient = new Intent(Menu_Patient.this,List_Patient.class);
        startActivity(to_ListPatient);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMainMenu();
    }
}
