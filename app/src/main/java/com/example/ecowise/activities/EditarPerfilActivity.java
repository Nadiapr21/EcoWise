package com.example.ecowise.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecowise.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {
    private EditText etNombre, etCorreo;
    private ImageView ivPerfil;
    private Button btnGuardar, btnCambiarFoto;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;


    private static final int REQUEST_IMAGE_PICK = 1;
    private Uri imagenSeleccionadaUri;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    int REQUEST_STORAGE_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        ivPerfil = findViewById(R.id.ivPerfil);
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);

        cargarDatosUsuario();

        btnGuardar.setOnClickListener(view -> guardarCambios());


        btnCambiarFoto.setOnClickListener(view -> {

            abrirGaleria();

        });

    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            imagenSeleccionadaUri = data.getData();
            ivPerfil.setImageURI(imagenSeleccionadaUri);
            guardarFotoSharedPreferences(imagenSeleccionadaUri);
        }
    }

    private void guardarCambios() {
        String nombre = etNombre.getText().toString();
        String correo = etCorreo.getText().toString();
        guardarFotoSharedPreferences(imagenSeleccionadaUri);
        if (nombre.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            if (imagenSeleccionadaUri != null) {
                guardarFotoSharedPreferences(imagenSeleccionadaUri);

                Map<String, Object> userUpdates = new HashMap<>();
                userUpdates.put("nombre", nombre);
                userUpdates.put("email", correo);

                db.collection("usuarios").document(userId)
                        .update(userUpdates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
            }
        }
    }


    private void guardarFotoSharedPreferences(Uri uri){
        SharedPreferences sharedPreferences = getSharedPreferences("Perfil", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fotoPerfil", uri.toString());
        editor.apply();
    }

    private void cargarDatosUsuario() {
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            db.collection("usuarios").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            etNombre.setText(documentSnapshot.getString("nombre"));
                            etCorreo.setText(documentSnapshot.getString("email"));
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
        }
    }
}