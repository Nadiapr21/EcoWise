package com.example.ecowise.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecowise.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NuevaNotaActivity extends AppCompatActivity {

    private EditText etTitulo;
    private EditText etContenido;
    private Button btnGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nueva_nota);

        etTitulo = findViewById(R.id.etTitulo);
        etContenido = findViewById(R.id.etContenido);
        btnGuardar = findViewById(R.id.btnGuardar);
        db = FirebaseFirestore.getInstance();

        btnGuardar.setOnClickListener(v-> { guardarNota(); });
}

    private void guardarNota() {
        //Obtener los datos ingresados por el usuario
        String titulo = etTitulo.getText().toString().trim();
        String contenido = etContenido.getText().toString().trim();

        //Validar que los campos no se queden vacios
        if(TextUtils.isEmpty(titulo)){
            etTitulo.setError("El título es obligatorio");
            return;
        }
        if(TextUtils.isEmpty(contenido)){
            etContenido.setError("El contenido es obligatorio");
            return;
        }

        //Crear fechaCreacion

        String fechaCreacion = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        //Mapa para los datos
        Map<String, Object> nota = new HashMap<>();
        nota.put("titulo", titulo);
        nota.put("contenido", contenido);
        nota.put("fechaCreacion", fechaCreacion);
        
        //Guardar la nota en firebase
        
        db.collection("notas")
                .add(nota)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar la nota", Toast.LENGTH_SHORT).show());
    }
    }
