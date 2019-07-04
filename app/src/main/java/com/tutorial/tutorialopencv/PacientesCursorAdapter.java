package com.tutorial.tutorialopencv;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class PacientesCursorAdapter extends CursorAdapter {

    private PacientesDbAdapter dbAdapter = null ;

    public PacientesCursorAdapter(Context context, Cursor c) {
        super(context, c);
        dbAdapter = new PacientesDbAdapter(context);
        dbAdapter.open();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view ;

        tv.setText(cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_NOMBRE)));
        tv.append(" " + cursor.getString(cursor.getColumnIndex(PacientesDbAdapter.P_COLUMNA_APELLIDO)));

    }
}
