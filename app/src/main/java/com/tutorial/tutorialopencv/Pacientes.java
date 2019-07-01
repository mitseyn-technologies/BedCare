package com.tutorial.tutorialopencv;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import static android.provider.Telephony.Mms.Part.FILENAME;


@SuppressWarnings("serial")
public class Pacientes implements Serializable {

    private static final String TAG = imageProcessing.class.getSimpleName();

    private String name;
    private String apellido;
    private String observacion;
    private String causa_postracion;
    private String medico_aCargo;
    private int edad;
    private int ID;
    private String fecha_ingreso;
    private String cedula;//Añadir al constructor
    private InputStream fileFromTxt;
    public ArrayList<Pacientes> list_Pacientes;

    public int id_total = 0;

    private Context context;

    private BufferedReader reader;



    public Pacientes( Context context)
    {
        this.context = context;
        list_Pacientes = new ArrayList<>();

    }

    public Pacientes()
    {

    }

    public Pacientes(int ID, String name, String apellido, String cedula,String medico,
                     int edad, String ingreso, String observacion, String causa_postracion)
    {
        this.ID = ID;
        this.name = name;
        this.apellido = apellido;
        this.cedula = cedula;
        this.medico_aCargo = medico;
        this.edad = edad;
        this.fecha_ingreso = ingreso;
        this.observacion = observacion;
        this.causa_postracion = causa_postracion;

    }



    public ArrayList<Pacientes> getList_Pacientes() {
        return list_Pacientes;
    }

    public void setList_Pacientes(ArrayList<Pacientes> list_Pacientes) {
        this.list_Pacientes = list_Pacientes;
    }

    public void addPaciente(Pacientes p)
    {
        list_Pacientes.add(p);
    }

    public Pacientes getPaciente(int id){
        return  list_Pacientes.get(id);
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getCausa_postracion() {
        return causa_postracion;
    }

    public void setCausa_postracion(String causa_postracion) {
        this.causa_postracion = causa_postracion;
    }

    public String getMedico_aCargo() {
        return medico_aCargo;
    }

    public void setMedico_aCargo(String medico_aCargo) {
        this.medico_aCargo = medico_aCargo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }


}
