package com.example.ecowise.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.NoCopySpan;
import android.util.Log;

import com.airbnb.lottie.animation.content.Content;

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

    //metodos para los usuarios
    public void insertarUsuario(String userId, String nombre, String email, String fotoPerfil){
        try{
            ContentValues values = new ContentValues();
            values.put("userId", userId);
            values.put("nombre", nombre);
            values.put("email", email);
            values.put("fotoPerfil", fotoPerfil);
            db.insertOrThrow("usuarios", null, values);
        }catch (SQLiteException e){
            Log.e("ERROR", "Error al insertar el usuario: " + e.getMessage());
        }
    }

    public Cursor recuperarUsuarios(){
        return db.rawQuery("SELECT * FROM usuarios", null);
    }

    public void actualizarUsuario(String userId, String nombre, String email, String fotoPerfil) {
        try {
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("email", email);
            values.put("fotoPerfil", fotoPerfil);

            db.update("usuarios", values, "userId = ?", new String[]{userId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al actualizar el usuario: " + e.getMessage());
        }
    }

    public void eliminarUsuario(String userId) {
        try {
            db.delete("usuarios", "userId = ?", new String[]{userId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al eliminar el usuario: " + e.getMessage());
        }
    }

    public Cursor obtenerPresupuestoYGastos(String userId) {
        String query = "SELECT p.presupuestoMax, p.gastoActual, g.categoria AS gastoCategoria, g.importe AS gastoImporte " +
                "FROM presupuestos p " +
                "JOIN gastos g ON p.userId = g.userId " +
                "WHERE p.userId = ?";
        return db.rawQuery(query, new String[]{userId});
    }

    public Cursor obtenerNotasYRecordatorios(String userId) {
        String query = "SELECT n.titulo AS notaTitulo, n.contenido, r.titulo AS recordatorioTitulo, r.descripcion, r.fecha " +
                "FROM notas n " +
                "JOIN recordatorios r ON n.userId = r.userId " +
                "WHERE n.userId = ?";
        return db.rawQuery(query, new String[]{userId});
    }

    //metodos para los gastos

    public void insertarGasto(String gastoId, String userId, double importe, String fecha, String categoria) {
        try {
            ContentValues values = new ContentValues();
            values.put("gastoId", gastoId);
            values.put("userId", userId);
            values.put("importe", importe);
            values.put("fecha", fecha);
            values.put("categoria", categoria);

            db.insertOrThrow("gastos", null, values);
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al insertar el gasto: " + e.getMessage());
        }
    }

    public Cursor obtenerGastos() {
        return db.rawQuery("SELECT * FROM gastos", null);
    }

    public Cursor obtenerGastosPorUsuario(String userId) {
        return db.rawQuery("SELECT * FROM gastos WHERE userId = ?", new String[]{userId});
    }

    public void actualizarGasto(String gastoId, double importe, String fecha, String categoria) {
        try {
            ContentValues values = new ContentValues();
            values.put("importe", importe);
            values.put("categoria", categoria);
            values.put("fecha", fecha);

            db.update("gastos", values, "gastoId = ?", new String[]{gastoId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al actualizar el gasto: " + e.getMessage());
        }
    }

    public void eliminarGasto(String gastoId) {
        try {
            db.delete("gastos", "gastoId = ?", new String[]{gastoId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al eliminar el gasto: " + e.getMessage());
        }
    }

    public void insertarMeta(String metaId, String titulo, double importeObjetivo, double importeAhorrado, String fechaLimite, String estado, double progresoMeta, String userId) {
        try {
            ContentValues values = new ContentValues();
            values.put("metaId", metaId);
            values.put("titulo", titulo);
            values.put("importeObjetivo", importeObjetivo);
            values.put("importeAhorrado", importeAhorrado);
            values.put("fechaLimite", fechaLimite);
            values.put("estado", estado);
            values.put("progresoMeta", progresoMeta);
            values.put("userId", userId);

            db.insertOrThrow("metas", null, values);
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al insertar la meta: " + e.getMessage());
        }
    }

    public Cursor obtenerMetasPorUsuario(String userId) {
        return db.rawQuery("SELECT * FROM metas WHERE userId = ?", new String[]{userId});
    }

    public void actualizarMeta(String metaId, double importeAhorrado, double progresoMeta, String estado) {
        try {
            ContentValues values = new ContentValues();
            values.put("importeAhorrado", importeAhorrado);
            values.put("progresoMeta", progresoMeta);
            values.put("estado", estado);

            db.update("metas", values, "metaId = ?", new String[]{metaId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al actualizar la meta: " + e.getMessage());
        }
    }

    public void eliminarMeta(String metaId) {
        try {
            db.delete("metas", "metaId = ?", new String[]{metaId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al eliminar la meta: " + e.getMessage());
        }
    }

    public void insertarNota(String notaId, String titulo, String contenido, String fechaCreacion, String userId) {
        try {
            ContentValues values = new ContentValues();
            values.put("notaId", notaId);
            values.put("titulo", titulo);
            values.put("contenido", contenido);
            values.put("fechaCreacion", fechaCreacion);
            values.put("userId", userId);

            db.insertOrThrow("notas", null, values);
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al insertar la nota: " + e.getMessage());
        }
    }

    public Cursor obtenerNotasPorUsuario(String userId) {
        return db.rawQuery("SELECT * FROM notas WHERE userId = ?", new String[]{userId});
    }

    public void actualizarNota(String notaId, String titulo, String contenido) {
        try {
            ContentValues values = new ContentValues();
            values.put("titulo", titulo);
            values.put("contenido", contenido);

            db.update("notas", values, "notaId = ?", new String[]{notaId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al actualizar la nota: " + e.getMessage());
        }
    }

    public void eliminarNota(String notaId) {
        try {
            db.delete("notas", "notaId = ?", new String[]{notaId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al eliminar la nota: " + e.getMessage());
        }
    }

    public void insertarPresupuesto(String presupuestoId, double gastoActual, double presupuestoMax, String userId) {
        try {
            ContentValues values = new ContentValues();
            values.put("presupuestoId", presupuestoId);
            values.put("gastoActual", gastoActual);
            values.put("presupuestoMax", presupuestoMax);
            values.put("userId", userId);

            db.insertOrThrow("presupuestos", null, values);
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al insertar el presupuesto: " + e.getMessage());
        }
    }

    public Cursor obtenerPresupuestosPorUsuario(String userId) {
        return db.rawQuery("SELECT * FROM presupuestos WHERE userId = ?", new String[]{userId});
    }

    public void actualizarPresupuesto(String presupuestoId, double gastoActual, double presupuestoMax) {
        try {
            ContentValues values = new ContentValues();
            values.put("gastoActual", gastoActual);
            values.put("presupuestoMax", presupuestoMax);

            db.update("presupuestos", values, "presupuestoId = ?", new String[]{presupuestoId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al actualizar el presupuesto: " + e.getMessage());
        }
    }

    public void eliminarPresupuesto(String presupuestoId) {
        try {
            db.delete("presupuestos", "presupuestoId = ?", new String[]{presupuestoId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al eliminar el presupuesto: " + e.getMessage());
        }
    }

    public void insertarRecordatorio(String recordatorioId, String titulo, String descripcion, double importe, String fecha, String frecuencia, String userId) {
        try {
            ContentValues values = new ContentValues();
            values.put("recordatorioId", recordatorioId);
            values.put("titulo", titulo);
            values.put("descripcion", descripcion);
            values.put("importe", importe);
            values.put("fecha", fecha);
            values.put("frecuencia", frecuencia);
            values.put("userId", userId);

            db.insertOrThrow("recordatorios", null, values);
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al insertar el recordatorio: " + e.getMessage());
        }
    }

    public Cursor obtenerRecordatoriosPorUsuario(String userId) {
        return db.rawQuery("SELECT * FROM recordatorios WHERE userId = ?", new String[]{userId});
    }

    public void actualizarRecordatorio(String recordatorioId, String titulo, String descripcion, double importe, String fecha, String frecuencia) {
        try {
            ContentValues values = new ContentValues();
            values.put("titulo", titulo);
            values.put("descripcion", descripcion);
            values.put("importe", importe);
            values.put("fecha", fecha);
            values.put("frecuencia", frecuencia);

            db.update("recordatorios", values, "recordatorioId = ?", new String[]{recordatorioId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al actualizar el recordatorio: " + e.getMessage());
        }
    }

    public void eliminarRecordatorio(String recordatorioId) {
        try {
            db.delete("recordatorios", "recordatorioId = ?", new String[]{recordatorioId});
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error al eliminar el recordatorio: " + e.getMessage());
        }
    }
}
