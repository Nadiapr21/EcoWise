package com.example.ecowise.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecowise.adapter.IngresoAdapter;
import com.example.ecowise.classes.Ingreso;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.Date;

import java.util.ArrayList;

public class RegistrarIngreso extends AppCompatActivity {
    private ArrayList<Ingreso> listaIngresos;
    private Button btnGuardar;
    private EditText etImporte, etCategoria, etFecha;
    private RecyclerView recyclerViewIngresos;
    private IngresoAdapter ingresoAdapter;
    private Button fabVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_gasto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializo el recyclerview
        recyclerViewIngresos = findViewById(R.id.recyclerViewGastos);
        recyclerViewIngresos.setLayoutManager(new LinearLayoutManager(this));
        listaIngresos = new ArrayList<>();
        ingresoAdapter = new IngresoAdapter(RegistrarIngreso.this, listaIngresos);
        recyclerViewIngresos.setAdapter(ingresoAdapter);

        cargarDatosFirestore();

        etImporte = findViewById(R.id.etImporte);
        etCategoria = findViewById(R.id.etCategoria);
        etFecha = findViewById(R.id.etFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        fabVolver = findViewById(R.id.fabVolver);


        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrarIngreso.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Mostrar la fecha seleccionada en el formato: dd/MM/yyyy
                            String fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                            etFecha.setText(fechaSeleccionada);  // Establecer la fecha en el EditText
                        }
                    }, year, month, day);
            datePickerDialog.show();  // Mostrar el DatePickerDialog
        });

        btnGuardar.setOnClickListener(v -> {
            String importeIngresado = etImporte.getText().toString();
            String categoriaIngresada = etCategoria.getText().toString();
            String fechaSeleccionada = etFecha.getText().toString();

            if (!importeIngresado.isEmpty() && !categoriaIngresada.isEmpty() && !fechaSeleccionada.isEmpty()) {
                try {


                    // Convertir el importe a tipo double
                    double importe = Double.parseDouble(importeIngresado);
                    // Crear un Intent para devolver el gasto registrado
                    Intent intent = new Intent();
                    intent.putExtra("importe", importe);
                    intent.putExtra("categoria", categoriaIngresada);
                    intent.putExtra("fecha", fechaSeleccionada);

                    // Establecer el resultado y finalizar la actividad
                    setResult(RESULT_OK, intent);
                    agregarGasto(importe, categoriaIngresada, fechaSeleccionada);
                    limpiarCampos();

                } catch (NumberFormatException e) {
                    // Mensaje en caso de formato inválido
                    Toast.makeText(RegistrarIngreso.this, "Por favor, ingresa un importe válido", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Mensaje en caso de campos vacíos
                Toast.makeText(this, "Por favor, ingrese todos los datos solicitados", Toast.LENGTH_SHORT).show();
            }
        });

        fabVolver.setOnClickListener(v -> volverAInicio());
    }

    private void limpiarCampos() {
        etImporte.setText("");
        etCategoria.setText("");
        etFecha.setText("");
    }

    private void cargarDatosFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gastos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(RegistrarIngreso.this, "Error al cargar los gastos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        listaIngresos.clear();
                        if (value != null) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Ingreso ingreso = document.toObject(Ingreso.class);
                                if (ingreso != null) {
                                    ingreso.setId(document.getId()); // Asignar el ID del documento a la instancia de Gasto
                                    listaIngresos.add(ingreso);
                                }
                            }
                        }
                        ingresoAdapter.notifyDataSetChanged();
                    }
                });
    }


    public void volverAInicio() {
        Intent intent = new Intent(RegistrarIngreso.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public RegistrarIngreso() { //Constructor para la lista de gastos
        listaIngresos = new ArrayList<>();
    }

    //Método para agregar un nuevo gasto
    public void agregarGasto(double importe, String categoria, String fecha) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> gastos = new HashMap<>();
        gastos.put("importe", importe);
        gastos.put("categoria", categoria);
        gastos.put("fecha", fecha);

        db.collection("gastos")
                .add(gastos)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Gasto añadido con ID: " + documentReference);
                    listaIngresos.add(new Ingreso(importe, categoria, fecha));
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al añadir el gasto: " + e.getMessage());
                });
    }

    //Metodo para eliminar un gasto cogiendo la categoría
    public void eliminarIngreso(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("ingresos")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Ingreso eliminado correctamente"))
                .addOnFailureListener(e -> System.out.println("Error al eliminar el ingreso: " + e.getMessage()));

    }

    public void modificarIngreso(String id, double nuevoImporte, String nuevaCategoria, String nuevaFecha) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Datos actualizados
        HashMap<String, Object> actualizacionIngreso = new HashMap<>();
        actualizacionGasto.put("id", id);
        actualizacionGasto.put("importe", nuevoImporte);
        actualizacionGasto.put("categoria", nuevaCategoria);
        actualizacionGasto.put("fecha", nuevaFecha);

        // Actualizar el documento
        db.collection("gastos").document(id)
                .update(actualizacionGasto)
                .addOnSuccessListener(aVoid -> System.out.println("Gasto modificado correctamente"))
                .addOnFailureListener(e -> System.out.println("Error al modificar el gasto: " + e.getMessage()));
    }

}