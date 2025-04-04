package com.example.ecowise.sqlite;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {  //Aqui defino la estructura de la base de datos en los métodos onCreate() y onUpgrade()

    String crearSQL = "CREATE TABLE Presupuestos (id Int, nombre Text)";

    private static final String DATABASE_NAME = "gastos_ingresos.db";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crearSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void checkycopiarDB(){
        try{
            String rutaDB = context.getDatabasePath(DATABASE_NAME).getPath();
            File archivoDB = new File(rutaDB);

            if (!archivoDB.exists()){
                copiarDB(rutaDB);
            }
        }catch (IOException e){
            throw new RuntimeException("Error al copiar la base de datos", e);
        }
    }

    public void copiarDB(String rutaDB) throws IOException{
        InputStream inputStream = context.getAssets().open("databases/" + DATABASE_NAME);
        OutputStream outputStream = new FileOutputStream(rutaDB);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0){
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }

    public void insertarTransaccion(int usuarioID, String tipo, double importe, String fecha){
        SQLiteDatabase db = null;
        try{
        db = this.getWritableDatabase(); // Abre la base de datos para escritura
        ContentValues valores = new ContentValues();
        valores.put("id_usuario", usuarioID);
        valores.put("tipo", tipo);
        valores.put("importe", importe);
        valores.put("fecha", fecha);

        db.insertOrThrow("transacciones", null, valores); // Inserta los datos en la tabla "transacciones"
    }catch(SQLiteException e){
            Log.e("DB_ERROR", "Error al insertar en transacciones: " + e.getMessage());
        }finally {
            if (db != null && db.isOpen()){
                db.close(); // Asegura que la base de datos se cierra
            }
        }
        }
}
