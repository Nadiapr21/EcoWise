package com.example.ecowise.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.NoCopySpan;
import android.util.Log;

import java.sql.SQLException;

public class DBManager {  //Aqui manejo las operaciones que interactuan con la base de datos usando el objeto dbHelper
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void abrir(){
        db = dbHelper.getWritableDatabase();
    }
    public void cerrar(){
        dbHelper.close();
    }

    public void insertarDatos(String name) {

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase(); // Abre la base de datos
            ContentValues values = new ContentValues();
            values.put("name", name);
            db.insertOrThrow("users", null, values);
        } catch (SQLiteException e) {
            Log.e("DB_ERROR", "Error inserting data: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Cierra la base de datos
            }
        }
    }

    public Cursor fetchData() {  //Abre la base de datos solo para la lectura //Recuperar los datos
        Cursor cursor = null;
        try{
            cursor = db.rawQuery("SELECT * FROM usuarios", null);
        }catch (SQLiteException e){
            Log.e("DB_ERROR", "Error recuperando los datos: " + e.getMessage());
        }
        return cursor;
    }
}
