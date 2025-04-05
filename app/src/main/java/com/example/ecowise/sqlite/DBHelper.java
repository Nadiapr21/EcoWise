package com.example.ecowise.sqlite;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {  //Aqui defino la estructura de la base de datos en los métodos onCreate() y onUpgrade()

    private static final String DATABASE_NAME = "db_ecowise.db";
    private static final int DATABASE_VERSION = 2;

    private final String rutaDB;
    private Context context;

    private static DBHelper dbHelper;

    public DBHelper(@NonNull Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.rutaDB = context.getDatabasePath(DATABASE_NAME).getPath();
        this.context = context;

    }

    public static synchronized DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios (" +
                "userId TEXT PRIMARY KEY, " +
                "nombre TEXT, " +
                "email TEXT, " +
                "fotoPerfil TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS gastos (" +
                "gastoId TEXT PRIMARY KEY, " +
                "userId TEXT, " +
                "importe REAL, " +
                "fecha TEXT, " +
                "categoria TEXT, " +
                "FOREIGN KEY(userId) REFERENCES usuarios(userId))");

        db.execSQL("CREATE TABLE IF NOT EXISTS metas (" +
                "metaId TEXT PRIMARY KEY, " +
                "titulo TEXT, " +
                "importeObjetivo REAL, " +
                "importeAhorrado REAL, " +
                "fechaLimite TEXT, " +
                "estado TEXT, " +
                "progresoMeta REAL, " +
                "userId TEXT, " +
                "FOREIGN KEY(userId) REFERENCES usuarios(userId))");

        db.execSQL("CREATE TABLE IF NOT EXISTS notas (" +
                "notaId TEXT PRIMARY KEY, " +
                "titulo TEXT, " +
                "contenido TEXT, " +
                "fechaCreacion TEXT, " +
                "userId TEXT, " +
                "FOREIGN KEY(userId) REFERENCES usuarios(userId))");

        db.execSQL("CREATE TABLE IF NOT EXISTS presupuestos (" +
                "presupuestoId TEXT PRIMARY KEY, " +
                "gastoActual REAL, " +
                "presupuestoMax REAL, " +
                "userId TEXT, " +
                "FOREIGN KEY(userId) REFERENCES usuarios(userId))");

        db.execSQL("CREATE TABLE IF NOT EXISTS recordatorios (" +
                "recordatorioId TEXT PRIMARY KEY, " +
                "titulo TEXT, " +
                "descripcion TEXT, " +
                "importe REAL, " +
                "fecha TEXT, " +
                "frecuencia TEXT, " +
                "userId TEXT, " +
                "FOREIGN KEY(userId) REFERENCES usuarios(userId))");

        Log.d("DBHelper", "Tablas creadas");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS gastos");
        db.execSQL("DROP TABLE IF EXISTS metas");
        db.execSQL("DROP TABLE IF EXISTS notas");
        db.execSQL("DROP TABLE IF EXISTS presupuestos");
        db.execSQL("DROP TABLE IF EXISTS recordatorios");
        onCreate(db);
    }

    public String getDBPath(){
        return rutaDB;
    }

    public void checkycopiarDB(){
        try{
            File archivoDB = new File(rutaDB);

            if (!archivoDB.exists()){
                copiarDB();
            }
        }catch (IOException e){
            throw new RuntimeException("Error al copiar la base de datos", e);
        }
    }

    public void copiarDB() throws IOException{
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
}
