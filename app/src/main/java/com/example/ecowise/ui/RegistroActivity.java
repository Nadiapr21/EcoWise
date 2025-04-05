package com.example.ecowise.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecowise.SecurePreferences;
import com.example.ecowise.sqlite.DBManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.os.Handler;
import android.os.Looper;

import com.example.ecowise.R;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etConfirmPassword;
    private Button btnConfirmar;
    private TextView tvTituloRegistro, tvSubtituloRegistro;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tvTituloRegistro = findViewById(R.id.tvTituloRegistro);
        tvSubtituloRegistro = findViewById(R.id.tvSubtituloRegistro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ObjectAnimator animacion1 = ObjectAnimator.ofFloat(tvTituloRegistro, "alpha", 0f, 1f); //Creo un ObjectAnimator para la animación y lo asigno
        animacion1.setDuration(3000);
        animacion1.setInterpolator(new AccelerateDecelerateInterpolator()); //Efecto de aceleración y desaceleración
        animacion1.start();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                ObjectAnimator animacion2 = ObjectAnimator.ofFloat(tvSubtituloRegistro, "alpha", 0f, 1f);
                animacion2.setDuration(3000);
                animacion2.setInterpolator(new AccelerateDecelerateInterpolator());
                animacion2.start();
            }
        }, 3000);


        btnConfirmar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegistroActivity.this, "El correo electrónico no es válido", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty() || password.length() < 6) {
                Toast.makeText(RegistroActivity.this, "La contraseña debe de tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) { //Si no es igual que ConfirmPassword
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            intent.putExtra("nombre", nombre);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);

            crearUser(nombre, email, password);
        });

        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable drawableEnd = etPassword.getCompoundDrawablesRelative()[2]; //Obtiene el drawableEnd (ojo)
                    if (drawableEnd != null) {
                        int drawableWidth = drawableEnd.getBounds().width();

                        if (event.getRawX() >= (etPassword.getRight() - drawableWidth - etPassword.getPaddingEnd())) {
                            isPasswordVisible = !isPasswordVisible;

                            if (isPasswordVisible) {
                                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_show, 0); // Ícono de ojo abierto
                            } else {
                                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                etPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0); // Ícono de ojo cerrado
                            }
                            etPassword.setSelection(etPassword.getText().length());

                            return true;
                        }
                    }
                }
                return false;
            }

            public boolean performClick() {
                etPassword.performClick();
                return true;
            }
        });

        etConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Drawable drawableEnd = etConfirmPassword.getCompoundDrawablesRelative()[2]; //Obtiene el drawableEnd (ojo)
                    if (drawableEnd != null) {
                        int drawableWidth = drawableEnd.getBounds().width();

                        if (event.getRawX() >= (etConfirmPassword.getRight() - drawableWidth - etConfirmPassword.getPaddingEnd())) {
                            isPasswordVisible = !isPasswordVisible;

                            if (isPasswordVisible) {
                                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_show, 0); // Ícono de ojo abierto
                            } else {
                                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                etConfirmPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0); // Ícono de ojo cerrado
                            }
                            etConfirmPassword.setSelection(etConfirmPassword.getText().length());

                            return true;
                        }
                    }
                }
                return false;
            }

            public boolean performClick() {
                etConfirmPassword.performClick();
                return true;
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validarNombre(String nombre) {
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre no puede estar vacío.");
            etNombre.requestFocus();
            return false;
        }
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            etNombre.setError("El nombre solo puede contener letras y espacios.");
            etNombre.requestFocus();
            return false;
        }
        if (nombre.length() < 3) {
            etNombre.setError("El nombre debe tener al menos 3 caracteres.");
            etNombre.requestFocus();
            return false;
        }
        return true;
    }

    public void crearUser(String nombre, String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = auth.getCurrentUser().getUid();



                            Map<String, Object> datosIniciales = new HashMap<>();
                            datosIniciales.put("nombre", nombre);
                            datosIniciales.put("gastos", new ArrayList<>());
                            datosIniciales.put("userId", userId);
                            datosIniciales.put("email", email);

                            SimpleDateFormat sdf = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                sdf = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
                            }
                            String fechaCreacion = sdf.format(new Date());
                            datosIniciales.put("fechaCreacion", fechaCreacion);

                            db.collection("usuarios")
                                    .document(userId)
                                    .set(datosIniciales)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "Usuario añadido a Firestore con éxito");
                                            Toast.makeText(RegistroActivity.this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                                            saveUserDataToSecurePrefs(userId, email);

                                            DBManager dbManager = new DBManager(RegistroActivity.this);
                                            dbManager.abrir();
                                            dbManager.insertarUsuario(userId, nombre, email, null);
                                            dbManager.cerrar();

                                            Log.d("SQLite", "Usuario añadido a SQLite con éxito: " + userId);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error añadiendo al usuario a Firestore", e);
                                            Toast.makeText(RegistroActivity.this, "Error al crear al usuario. Por favor, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                Log.w("Auth", "Weak password", e);
                                Toast.makeText(RegistroActivity.this, "Password too weak. Try a stronger password.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                Log.w("Auth", "Invalid email", e);
                                Toast.makeText(RegistroActivity.this, "Email inválido.", Toast.LENGTH_SHORT).show();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Log.w("Auth", "User already exists", e);
                                Toast.makeText(RegistroActivity.this, "Usuario con este email ya existe.", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.w("Auth", "Generic error", e);
                                Toast.makeText(RegistroActivity.this, "Error al crear al usuario. Por favor, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    ;
                    private void saveUserDataToSecurePrefs(String userId, String email) {
                        SecurePreferences securePreferences = new SecurePreferences(RegistroActivity.this);
                        securePreferences.saveData("userId", userId);
                        securePreferences.saveData("email", email);
                    }
                });


    }

}

