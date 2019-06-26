package com.tutorial.tutorialopencv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class List_Patient extends AppCompatActivity {

    private ListView listView;
    private String []StringArray;
    public ArrayList<Pacientes> list_Pacientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patient);

        StringArray = new String[8];
        list_Pacientes = new ArrayList<>(); // listado de pacientes prueba
        fillArray();


        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_list_patient,R.id.textview,StringArray);

        listView =(ListView) findViewById(R.id.ListPatient);
        listView.setAdapter(adapter);
    }

    private void fillArray()
    {
        Pacientes p = new Pacientes(0,"Roberto","Castillo","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 80, new Date(2010,03,14));

        Pacientes p1 = new Pacientes(1,"Carlos","Villagran","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 90, new Date(2009,03,14));

        Pacientes p2 = new Pacientes(2,"Ernesto","Belloni","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 97, new Date(2007,03,14));

        Pacientes p3 = new Pacientes(3,"Jose","Cazelli","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 84, new Date(2012,03,14));

        Pacientes p4 = new Pacientes(4,"Marcelo","Salas","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 78, new Date(2009,03,14));

        Pacientes p5 = new Pacientes(5,"Ivan","Zamorano","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 67, new Date(2015,03,14));

        Pacientes p6 = new Pacientes(6,"Xavi","Hernandez","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 69, new Date(2019,03,14));

        Pacientes p7 = new Pacientes(7,"Ernesto","Segoviano","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 91, new Date(2008,03,14));


        list_Pacientes.add(p);
        list_Pacientes.add(p1);
        list_Pacientes.add(p2);
        list_Pacientes.add(p3);
        list_Pacientes.add(p4);
        list_Pacientes.add(p5);
        list_Pacientes.add(p6);
        list_Pacientes.add(p7);
        fillStringArray();


    }

    private void fillStringArray()
    {

        for (int i = 0; i < StringArray.length;i++)
        {
            StringArray[i] = list_Pacientes.get(i).getID()+" "+list_Pacientes.get(i).getName() + " "+list_Pacientes.get(i).getApellido();
            Toast.makeText(getApplicationContext(), StringArray[i],Toast.LENGTH_SHORT).show();
        }
    }
}
