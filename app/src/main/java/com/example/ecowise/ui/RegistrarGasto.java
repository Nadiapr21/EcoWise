package com.example.ecowise.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecowise.adapter.GastoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
import com.example.ecowise.classes.Gasto;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Map;

public class RegistrarGasto extends AppCompatActivity {
    private ArrayList<Gasto> listaGastos;
    private Button btnGuardar;
    private EditText etImporte, etCategoria, etFecha;
    private RecyclerView recyclerViewGastos;
    private GastoAdapter gastosAdapter;
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
        recyclerViewGastos = findViewById(R.id.recyclerViewGastos);
        recyclerViewGastos.setLayoutManager(new LinearLayoutManager(this));
        listaGastos = new ArrayList<>();
        gastosAdapter = new GastoAdapter(RegistrarGasto.this, listaGastos);
        recyclerViewGastos.setAdapter(gastosAdapter);

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

            DatePickerDialog datePickerDialog = new DatePickerDialog(RegistrarGasto.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                            String fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                            etFecha.setText(fechaSeleccionada);
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnGuardar.setOnClickListener(v -> {
            String importeIngresado = etImporte.getText().toString();
            String categoriaIngresada = etCategoria.getText().toString();
            String fechaSeleccionada = etFecha.getText().toString();

            if (!importeIngresado.isEmpty() && !categoriaIngresada.isEmpty() && !fechaSeleccionada.isEmpty()) {
                try {

                    double importe = Double.parseDouble(importeIngresado);


                    agregarGasto(importe, categoriaIngresada, fechaSeleccionada);


                    actualizarGastoPresupuesto(importe);


                    limpiarCampos();

                } catch (NumberFormatException e) {

                    Toast.makeText(RegistrarGasto.this, "Por favor, ingresa un importe válido", Toast.LENGTH_SHORT).show();
                }
            } else {

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
                            Toast.makeText(RegistrarGasto.this, "Error al cargar los gastos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        listaGastos.clear();
                        if (value != null) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Gasto gasto = document.toObject(Gasto.class);
                                if (gasto != null) {
                                    gasto.setId(document.getId());
                                    listaGastos.add(gasto);
                                }
                            }
                        }
                        gastosAdapter.notifyDataSetChanged();
                    }
                });
    }


    public void volverAInicio() {
        Intent intent = new Intent(RegistrarGasto.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public RegistrarGasto() {
        listaGastos = new ArrayList<>();
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
                    listaGastos.add(new Gasto(importe, categoria, fecha));
                    actualizarGastoPresupuesto(importe);
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error al añadir el gasto: " + e.getMessage());
                });
    }

    private void actualizarGastoPresupuesto(double nuevoImporte) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();

        if (usuarioActual != null) {
            String userID = usuarioActual.getUid();
            DocumentReference presupuestoRef = db.collection("presupuestos").document(userID);

            presupuestoRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long gastoActualLong = documentSnapshot.getLong("gastoActual");
                    if (gastoActualLong != null) {
                        int gastoActual = gastoActualLong.intValue();
                        gastoActual += nuevoImporte;


                        Map<String, Object> actualizacion = new HashMap<>();
                        actualizacion.put("gastoActual", gastoActual);

                        presupuestoRef.set(actualizacion, SetOptions.merge());
                    }
                }
            }).addOnFailureListener(e -> Log.w("Firestore", "Error al actualizar gasto actual", e));
        }
    }

    //Metodo para eliminar un gasto cogiendo la categoría
    public void eliminarGasto(String id) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gastos")
                .document(String.valueOf(id))
                .delete()
                .addOnSuccessListener(aVoid -> System.out.println("Gasto eliminado correctamente"))
                .addOnFailureListener(e -> System.out.println("Error al eliminar el gasto: " + e.getMessage()));

    }

//    public void modificarGasto(String id, double nuevoImporte, String nuevaCategoria, String nuevaFecha) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//        HashMap<String, Object> actualizacionGasto = new HashMap<>();
//        actualizacionGasto.put("id", id);
//        actualizacionGasto.put("importe", nuevoImporte);
//        actualizacionGasto.put("categoria", nuevaCategoria);
//        actualizacionGasto.put("fecha", nuevaFecha);
//
//
//        db.collection("gastos").document(id)
//                .update(actualizacionGasto)
//                .addOnSuccessListener(aVoid -> System.out.println("Gasto modificado correctamente"))
//                .addOnFailureListener(e -> System.out.println("Error al modificar el gasto: " + e.getMessage()));
//    }

}