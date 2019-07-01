package com.tutorial.tutorialopencv;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbHelper extends SQLiteOpenHelper {

    private static int version = 1;
    private static String name = "PacientesDb";
    private static SQLiteDatabase.CursorFactory factory;

    private String consult = "CREATE TABLE PACIENTES(" +
            "_id INTEGER PRIMARY KEY," +
            "p_nombre TEXT NOT NULL," +
            "p_apellido TEXT," +
            "p_cedula TEXT," +
            "p_medico TEXT," +
            "p_edad INTEGER," +
            "p_fecha_ingreso TEXT," +
            "p_observacion TEXT," +
            "p_causa_postracion TEXT)";

    public dbHelper(Context context) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(this.getClass().toString(),"Creando Base de datos");

        db.execSQL(consult);

        db.execSQL( "CREATE UNIQUE INDEX p_nombre ON PACIENTES(p_nombre ASC)" );
        db.execSQL( "CREATE UNIQUE INDEX p_apellido ON PACIENTES(p_apellido ASC)" );

        db.execSQL("INSERT INTO PACIENTES(_id, p_nombre,p_apellido,p_cedula,p_medico,p_edad,p_fecha_ingreso,p_observacion,p_causa_postracion) " +
                    "VALUES(1,'Roberto','Castillo','4.234.333-k','Federico Straus','82','2 de Marzo 2015','Precaución zona derecha','Inmovilidad en piernas')");
        db.execSQL("INSERT INTO PACIENTES(_id, p_nombre) " +
                    "VALUES(2,'Camilo','Espinoza','5.234.333-1','Federico Straus','72','2 de Marzo 2010','Se encuentra con buen estado de animo','Inmovilidad en piernas')");
        db.execSQL("INSERT INTO PACIENTES(_id, p_nombre) " +
                    "VALUES(3,'Maria','Flores','7.234.333-6','Federico Straus','92','2 de Marzo 2009','Precaución zona abdominal','Inmovilidad en piernas')");



        Log.i(this.getClass().toString(), "Datos iniciales HIPOTECA insertados");

        Log.i(this.getClass().toString(), "Base de datos creada");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
