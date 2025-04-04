package com.example.ecowise.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecowise.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetalleNotaActivity extends AppCompatActivity {

    private EditText etTituloDetalle, etContenidoDetalle;
    private Button btnGuardarCambios, btnEliminarNota;
    private FirebaseFirestore db;
    private String notaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_nota);


        db = FirebaseFirestore.getInstance();
        etTituloDetalle = findViewById(R.id.etTituloDetalle);
        etContenidoDetalle = findViewById(R.id.etContenidoDetalle);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnEliminarNota = findViewById(R.id.btnEliminarNota);


        notaId = getIntent().getStringExtra("notaId");

        if (notaId != null) {
            cargarNota();
        }

        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        btnEliminarNota.setOnClickListener(v -> eliminarNota());

        etContenidoDetalle.setOnFocusChangeListener((v, hasFocus) ->{
            if (hasFocus){
                etContenidoDetalle.setMaxLines(Integer.MAX_VALUE);
            }else{
                etContenidoDetalle.setMaxLines(6);
            }

            etContenidoDetalle.setMovementMethod(new ScrollingMovementMethod());
        });



    }

    private void eliminarNota() {
        db.collection("notas").document(notaId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DetalleNotaActivity.this, "Nota eliminada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(DetalleNotaActivity.this, "Error al eliminar la nota", Toast.LENGTH_SHORT).show());
    }

    private void guardarCambios() {

        String titulo = etTituloDetalle.getText().toString().trim();
        String contenido = etContenidoDetalle.getText().toString().trim();


        if (TextUtils.isEmpty(titulo)) {
            etTituloDetalle.setError("El título es obligatorio");
            return;
        }
        if (TextUtils.isEmpty(contenido)) {
            etContenidoDetalle.setError("El contenido es obligatorio");
            return;
        }

        db.collection("notas").document(notaId)
                .update("titulo", titulo, "contenido", contenido)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DetalleNotaActivity.this, "Nota actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(DetalleNotaActivity.this, "Error al actualizar la nota", Toast.LENGTH_SHORT).show());

    }

    private void cargarNota() {

        db.collection("notas").document(notaId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        String titulo = documentSnapshot.getString("titulo");
                        String contenido = documentSnapshot.getString("contenido");

                        etTituloDetalle.setText(titulo);
                        etContenidoDetalle.setText(contenido);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(DetalleNotaActivity.this, "Error al cargar nota", Toast.LENGTH_SHORT).show());


    }
    }
