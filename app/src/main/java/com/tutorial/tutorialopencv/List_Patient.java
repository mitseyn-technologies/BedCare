package com.tutorial.tutorialopencv;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class List_Patient extends AppCompatActivity {

    private static final String TAG = List_Patient.class.getSimpleName();;
    private ListView listView;
    private String []StringArray;
    public ArrayList<Pacientes> list_Pacientes;
    private Pacientes p, pacienteFromAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_patient);




        InputStream XmlFileInputStream = getResources().openRawResource(R.raw.pacientes); // getting XM1

        p = new Pacientes(this);


            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                Pacientes p_new = (Pacientes)bundle.getSerializable("MyNewPerson");
                p.addPaciente(p_new);
            }
        p.readFile(XmlFileInputStream);


        list_Pacientes = p.getList_Pacientes();
        StringArray = new String[list_Pacientes.size()];
        fillStringArray();


        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.activity_list_patient,R.id.textview,StringArray);

        listView =findViewById(R.id.ListPatient);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                go_toShowPatient(list_Pacientes.get(position));
            }

        });
    }

    private void go_toShowPatient(Pacientes p)
    {

        Intent to_ShowPatient= new Intent(List_Patient.this,ShowPatient.class);
        to_ShowPatient.putExtra("parametro", p);
        startActivity(to_ShowPatient);
        this.finish();
    }



    private void fillStringArray()
    {

        for (int i = 0; i < StringArray.length;i++)
        {
            StringArray[i] = list_Pacientes.get(i).getID()+" "+list_Pacientes.get(i).getName() + " "+list_Pacientes.get(i).getApellido();
        }
    }

    private void go_ToMenuMatient()
    {
        Intent to_MenuPatient = new Intent(List_Patient.this,Menu_Patient.class);
        startActivity(to_MenuPatient);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        go_ToMenuMatient();

    }
}
