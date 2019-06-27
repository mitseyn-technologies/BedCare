package com.tutorial.tutorialopencv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


@SuppressWarnings("serial")
public class Pacientes implements Serializable {

    private String name;
    private String apellido;
    private String observacion;
    private String causa_postracion;
    private String medico_aCargo;
    private int edad;
    private int ID;
    private String fecha_ingreso;
    private String cedula;//Añadir al constructor
    public ArrayList<Pacientes> list_Pacientes;

    public int id_total = 0;


    public Pacientes(){
        list_Pacientes = fillArray();

    }


    public Pacientes(int ID, String name, String apellido, String cedula, String observacion, String causa_postracion,
                     String medico, int edad, String ingreso)
    {
        this.ID = ID;
        this.id_total = id_total + ID;
        this.name = name;
        this.apellido = apellido;
        this.observacion = observacion;
        this.causa_postracion = causa_postracion;
        this.medico_aCargo = medico;
        this.edad = edad;
        this.fecha_ingreso = ingreso;
        this.cedula = cedula;

    }

    private ArrayList<Pacientes> fillArray()
    {

        ArrayList<Pacientes> lista = new ArrayList<>();

        Pacientes p = new Pacientes(0,"Roberto","Castillo","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 80, "20/03/2009");

        Pacientes p1 = new Pacientes(1,"Carlos","Villa","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Manuel Padilla", 90, "20/03/2009");

        Pacientes p2 = new Pacientes(2,"Ernesto","Faundez","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 97, "20/03/2009");

        Pacientes p3 = new Pacientes(3,"Maria","Contreras","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Manuel Padilla", 84, "20/03/2009");

        Pacientes p4 = new Pacientes(4,"Marcelo","Vera","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Jose Salazar", 78, "20/03/2009");

        Pacientes p5 = new Pacientes(5,"Florencia","Zamora","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Jose Salazar", 67, "20/03/2009");

        Pacientes p6 = new Pacientes(6,"Petronila","Hernandez","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 69, "20/03/2009");

        Pacientes p7 = new Pacientes(7,"Ernesto","Flores","6.235.522-2","Son varias las causas, lo principal es que","Inmovilidad en piernas",
                "Igor Stravski", 91, "20/03/2009");


        lista.add(p);
        lista.add(p1);
        lista.add(p2);
        lista.add(p3);
        lista.add(p4);
        lista.add(p5);
        lista.add(p6);
        lista.add(p7);

        return lista;
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
