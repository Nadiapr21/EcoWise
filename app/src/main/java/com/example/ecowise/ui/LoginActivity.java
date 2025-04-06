package com.example.ecowise.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricPrompt;
import androidx.biometric.BiometricManager;

import android.credentials.CredentialManager;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.MotionEvent;

import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.GetPasswordOption;
import androidx.credentials.GetPublicKeyCredentialOption;
import androidx.credentials.PasswordCredential;
import androidx.credentials.PublicKeyCredential;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.example.ecowise.R;
import com.example.ecowise.sqlite.DBManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private Button btnIniciarSesion;
    private Button btnRegistro;
    private EditText etEmail;
    private EditText etPassword;
    private boolean isPasswordVisible = false; //Estado de la contraseña
    private ImageView ivHuellaDactilar;
    FirebaseUser user;
    private TextView tvForgotPassword;
    private DBManager dbManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbManager = new DBManager(this);
        dbManager.abrir(); //aqui se abre la base de datos
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();



        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnRegistro = findViewById(R.id.btnRegistro);
        etEmail = findViewById(R.id.etEmail);
        ivHuellaDactilar = findViewById(R.id.ivHuellaDactilar);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        setupBiometricAuthentication();
        etPassword = findViewById(R.id.etPassword);

        tvForgotPassword.setOnClickListener(v-> {
            mostrarDialogoRestablecerPassword();
        });


        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    Drawable drawableEnd = etPassword.getCompoundDrawablesRelative()[2]; //Obtiene el drawableEnd (ojo)
                    if (drawableEnd != null){
                        int drawableWidth = drawableEnd.getBounds().width();

                        if (event.getRawX() >= (etPassword.getRight() - drawableWidth - etPassword.getPaddingEnd())){
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
                etPassword.performClick(); // Realiza la acción de click en el EditText
                return true; //Devuelve true
            }

        });

        //OnclickListener para el boton iniciar sesión
        btnIniciarSesion.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (LoginActivity.validarLogin(email, password, this)) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d("TAG", "signInWithEmail:success");

                                if (user != null) {
                                    String userId = user.getUid();
                                    String nombre = user.getDisplayName();
                                    String fotoPerfil = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
                                    dbManager.abrir();
                                    dbManager.insertarUsuario(userId, nombre != null ? nombre : "Usuario", email, fotoPerfil);
                                    dbManager.cerrar();
                                    Log.d("FirebaseUser", "El userId del usuario actual es: " + userId);
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    recuperarDatos("recordatorios", userId, db);
                                    recuperarDatos("presupuestos", userId, db);
                                    recuperarDatos("notas", userId, db);
                                    recuperarDatos("metas", userId, db);
                                    recuperarDatos("gastos", userId, db);
                                }
                                startActivity(new Intent(this, MainActivity.class)); // Metodo que navega a la actividad principal
                            } else {
                                // Error en el inicio de sesión
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Exception exception = task.getException();
                                mostrarMensajeError(exception);
                            }
                        });
            }
        });

        //OnClickListener para el boton de registro
        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
            finish();
        });

        //OnClickListener para el ImageView de la huella dactilar
        ivHuellaDactilar.setOnClickListener(v -> biometricPrompt.authenticate(promptInfo));

    }

    private void mostrarDialogoRestablecerPassword() {

        final EditText correoEditText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restablecer Contraseña")
                .setMessage("Ingresa tu correo electrónico para recibir un enlace de restablecimiento.")
                .setView(correoEditText)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String correo = correoEditText.getText().toString().trim();
                    if (!correo.isEmpty()) {
                        enviarCorreoRestablecimiento(correo);
                    } else {
                        Toast.makeText(LoginActivity.this, "Por favor ingresa un correo válido.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void enviarCorreoRestablecimiento(String correo) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(correo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Te hemos enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Error al enviar el correo. Inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void recuperarDatos(String coleccion, String userId, FirebaseFirestore db) {
        db.collection(coleccion)
                .whereEqualTo("usuario_id", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("Firestore", "Datos de la colección " + coleccion + " recuperados correctamente.");
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                Map<String, Object> data = document.getData();
                                switch (coleccion) {
                                    case "recordatorios":
                                        String recordatorioId = document.getId();
                                        String tituloRecordatorio = (String) data.get("titulo");
                                        String descripcion = (String) data.get("descripcion");
                                        Double importeRecordatorio = (Double) data.get("importe");
                                        String fechaRecordatorio = (String) data.get("fecha");
                                        String frecuencia = (String) data.get("frecuencia");

                                        dbManager.insertarRecordatorio(recordatorioId, tituloRecordatorio, descripcion, importeRecordatorio, fechaRecordatorio, frecuencia, userId);
                                        break;

                                    case "presupuestos":
                                        String presupuestoId = document.getId();
                                        Double gastoActual = (Double) data.get("gasto_actual");
                                        Double presupuestoMax = (Double) data.get("presupuesto_max");

                                        dbManager.insertarPresupuesto(presupuestoId, gastoActual, presupuestoMax, userId);
                                        break;

                                    case "notas":
                                        String notaId = document.getId();
                                        String tituloNota = (String) data.get("titulo");
                                        String contenido = (String) data.get("contenido");
                                        String fechaCreacion = (String) data.get("fecha_creacion");

                                        dbManager.insertarNota(notaId, tituloNota, contenido, fechaCreacion, userId);
                                        break;

                                    case "metas":
                                        String metaId = document.getId();
                                        String tituloMeta = (String) data.get("titulo");
                                        Double importeObjetivo = (Double) data.get("importe_objetivo");
                                        Double importeAhorrado = (Double) data.get("importe_ahorrado");
                                        String fechaLimite = (String) data.get("fecha_limite");
                                        String estado = (String) data.get("estado");
                                        Double progresoMeta = (Double) data.get("progreso_meta");

                                        dbManager.insertarMeta(metaId, tituloMeta, importeObjetivo, importeAhorrado, fechaLimite, estado, progresoMeta, userId);
                                        break;

                                    case "gastos":
                                        String gastoId = document.getId();
                                        String categoria = (String) data.get("categoria");
                                        Double importeGasto = (Double) data.get("importe");
                                        String fechaGasto = (String) data.get("fecha");

                                        dbManager.insertarGasto(gastoId, userId, importeGasto, fechaGasto, categoria);
                                        break;

                                    default:
                                        Log.w("Firestore", "Colección desconocida: " + coleccion);
                                        break;
                                }
                            }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al recuperar datos de la colección " + coleccion, e);
                });
    }

    //Metodo para iniciar sesión con la huella dactilar
    private void setupBiometricAuthentication() {
        //Verificar si el dispositivo soporta biometría
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "La autenticación biométrica no está disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result); //Si la autenticación va bien
                Toast.makeText(LoginActivity.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                Log.d("Autenticación Biometrica", "Autenticación exitosa");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed(); //Si la autenticación falla
                Toast.makeText(LoginActivity.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString); //Si la autenticación da error
                Toast.makeText(LoginActivity.this, "Error: " + errString, Toast.LENGTH_SHORT).show();
            }


        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()  //Modelo de la información de la autenticación biometrica
                .setTitle("Autenticación Biométrica")
                .setSubtitle("Usa tu huella digital para iniciar sesión")
                .setNegativeButtonText("Cancelar")
                .build();
    }

    private void mostrarMensajeError(Exception exception) {
        String mensajeError = "Error al iniciar sesión.";
        if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            mensajeError = "Correo electrónico o contraseña incorrectos.";
        } else if (exception instanceof FirebaseAuthUserCollisionException) {
            mensajeError = "Usuario ya existe."; //Este error no es común en signInWithEmailAndPassword
        } else {
            Log.e("TAG", "Error de autenticación: ", exception); // Registra el error completo para depuración
        }
        Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show();

    }

    //Metodo para iniciar sesión
    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
            } else {
                // Si el inicio de sesión falla, mostrar un mensaje de error al usuario.
                Log.w("TAG", "signInWithEmail:error", task.getException());
                Toast.makeText(LoginActivity.this, "No vale", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Metodo para cerrar sesion
    public void logoutUser() {
        mAuth.signOut();
        dbManager.cerrar();
    }

    //Metodo para registrar a un nuevo usuario
    public void registrarUsuario(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Log.d("TAG", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    String userId = user.getUid();
                                    String nombre = user.getDisplayName() != null ? user.getDisplayName() : "Usuario";
                                    String fotoPerfil = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

                                    DBManager dbManager = new DBManager(this);
                                    dbManager.abrir();
                                    dbManager.insertarUsuario(userId, nombre, email, fotoPerfil);
                                    dbManager.cerrar();

                                    Log.d("TAG", "Usuario registrado en SQLite con userId: " + userId);
                                }
                            } else {
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Autenticación fallida.", Toast.LENGTH_SHORT).show();
                            }

                        }

                );
    }

    public static boolean validarLogin(String email, String password, Context context) {
        boolean esValido = true;

        //Validación de correo electrónico

        if (email == null || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {  //Patterns se usa para verificar el formato del correo electronico
            Toast.makeText(context, "El correo no es válido", Toast.LENGTH_SHORT).show();
            Toast.makeText(context, "Por favor, ingrese los datos solicitados", Toast.LENGTH_SHORT).show();
            esValido = false;
        }

        //Validación de la contraseña

        if (password == null || password.isEmpty() || password.length() < 6) {
            Toast.makeText(context, "Contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            esValido = false;
        }

        return esValido;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.cerrar();
    }
}