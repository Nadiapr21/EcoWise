package com.example.ecowise.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecowise.R;
import com.example.ecowise.classes.Meta;
import com.google.firebase.firestore.FirebaseFirestore;

public class NuevaMetaActivity extends AppCompatActivity {
    private EditText etTituloMeta, etDescripcionMeta, etImporteObjetivo, etFechaLimiteMeta, etEstado;
    private Button btnGuardarMeta;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_meta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etTituloMeta = findViewById(R.id.etTituloMeta);
        etDescripcionMeta = findViewById(R.id.etDescripcionMeta);
        etImporteObjetivo = findViewById(R.id.etImporteObjetivo);
        etFechaLimiteMeta = findViewById(R.id.etFechaLimiteMeta);
        etEstado = findViewById(R.id.etEstado);
        btnGuardarMeta = findViewById(R.id.btnGuardarMeta);

        db = FirebaseFirestore.getInstance();

        btnGuardarMeta.setOnClickListener(v -> {
            String titulo = etTituloMeta.getText().toString();
            String importeObjetivo = etDescripcionMeta.getText().toString();
            String importeAhorrado = etImporteObjetivo.getText().toString();
            String fechaLimite = etFechaLimiteMeta.getText().toString();
            boolean enProgreso = Boolean.parseBoolean(etEstado.getText().toString());

            if (!titulo.isEmpty() && !importeObjetivo.isEmpty() && !importeAhorrado.isEmpty() && !fechaLimite.isEmpty()) {
                Meta nuevaMeta = new Meta(titulo, Double.parseDouble(importeObjetivo), Double.parseDouble(importeAhorrado), fechaLimite, enProgreso);
                db.collection("metas")
                        .add(nuevaMeta)
                        .addOnSuccessListener(documentReference -> {
                            String id = documentReference.getId();
                            nuevaMeta.setId(id);

                            db.collection("metas").document(id)
                                    .update("id", id)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(NuevaMetaActivity.this, "Meta guardada", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(NuevaMetaActivity.this, "Error al guardar la meta", Toast.LENGTH_SHORT).show();
                                    });
                        });
            }
        });
    }
}