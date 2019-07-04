package com.tutorial.tutorialopencv;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PacientesDbAdapter extends Object {

    public static final String P_TABLA = "PACIENTES";
    public static final String P_COLUMNA_ID = "_id";
    public static final String P_COLUMNA_NOMBRE = "p_nombre";
    public static final String P_COLUMNA_APELLIDO = "p_apellido";
    public static final String P_COLUMNA_CEDULA = "p_cedula";
    public static final String P_COLUMNA_MEDICO = "p_medico";
    public static final String P_COLUMNA_EDAD = "p_edad";
    public static final String P_COLUMNA_FECHA = "p_fecha_ingreso";
    public static final String P_COLUMNA_OBSERVACION = "p_observacion";
    public static final String P_COLUMNA_CAUSA = "p_causa_postracion";

    private Context context;
    private dbHelper dbHelper;
    private SQLiteDatabase db;

    private String[] columnas = new String[]{ P_COLUMNA_ID,P_COLUMNA_NOMBRE,P_COLUMNA_APELLIDO,
                                              P_COLUMNA_CEDULA,P_COLUMNA_MEDICO,P_COLUMNA_EDAD,P_COLUMNA_FECHA,
                                              P_COLUMNA_OBSERVACION,P_COLUMNA_CAUSA};

    public PacientesDbAdapter(Context context)
    {
        this.context = context;
    }

    public PacientesDbAdapter open() throws SQLException
    {
        dbHelper = new dbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void cerrar()
    {
        dbHelper.close();
    }

    /**
     * Devuelve cursor con todos las columnas de la tabla
     */
    public Cursor getCursor() throws SQLException
    {
        Cursor c = db.query( true, P_TABLA, columnas, null, null, null, null, null, null);

        return c;
    }

    /**
     * Devuelve cursor con todos las columnas del registro
     */
    public Cursor getRegistro(long id) throws SQLException
    {
        Cursor c = db.query( true, P_TABLA, columnas, P_COLUMNA_ID + "=" + id, null, null, null, null, null);

        //Nos movemos al primer registro de la consulta
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public long delete(long id)
    {
        if (db == null)
            abrir();

        return db.delete(P_TABLA, "_id=" + id, null);
    }

    /**
     * Inserta los valores en un registro de la tabla
     */
    public long insert(ContentValues reg)
    {
        if (db == null)
            abrir();

        return db.insert(P_TABLA, null, reg);
    }

    public PacientesDbAdapter abrir() throws SQLException
    {
        dbHelper = new dbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Modificar el registro
     */
    public long update(ContentValues reg)
    {
        long result = 0;

        if (db == null)
            abrir();

        if (reg.containsKey(P_COLUMNA_ID))
        {
            //
            // Obtenemos el id y lo borramos de los valores
            //
            long id = reg.getAsLong(P_COLUMNA_ID);

            reg.remove(P_COLUMNA_ID);

            //
            // Actualizamos el registro con el identificador que hemos extraido
            //
            result = db.update(P_TABLA, reg, "_id=" + id, null);
        }
        return result;
    }
}
