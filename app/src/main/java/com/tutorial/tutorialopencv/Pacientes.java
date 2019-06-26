package com.tutorial.tutorialopencv;

import java.util.ArrayList;
import java.util.Date;

public class Pacientes {

    private String name;
    private String apellido;
    private String observacion;
    private String causa_postracion;
    private String medico_aCargo;
    private int edad;
    private int ID;
    private Date fecha_ingreso;
    private String cedula;//Añadir al constructor

    public Pacientes(){

    }



    public Pacientes(int ID, String name, String apellido, String observacion, String causa_postracion,
                     String medico, int edad, Date ingreso)
    {
        this.ID = ID;
        this.name = name;
        this.apellido = apellido;
        this.observacion = observacion;
        this.causa_postracion = causa_postracion;
        this.medico_aCargo = medico;
        this.edad = edad;
        this.fecha_ingreso = ingreso;

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

    public Date getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(Date fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }


}
