package com.example.ecowise.activities;
import com.example.ecowise.R;
import com.example.ecowise.ui.MainActivity;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PresupuestoActivity extends AppCompatActivity {
    private ArcProgress arcProgressPresupuesto;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private EditText etPresupuesto;
    private Button btnGuardarPresupuesto;
    private FirebaseAuth mAuth;
    private FirebaseUser usuarioAutenticado;
    private ImageView ivVolver;
    private boolean mensajeExcedidoMostrado = false;
    private int presupuestoTotal = 0;
    private int gastosActuales = 0;
    private TextView tvProgresoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presupuesto);


        arcProgressPresupuesto = findViewById(R.id.arcProgressPresupuesto);
        etPresupuesto = findViewById(R.id.etPresupuesto);
        btnGuardarPresupuesto = findViewById(R.id.btnGuardarPresupuesto);
        tvProgresoActual = findViewById(R.id.tvProgresoActual);
        ivVolver = findViewById(R.id.ivVolver);

        ivVolver.setOnClickListener(v -> {
            Intent intent = new Intent(PresupuestoActivity.this, MainActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        usuarioAutenticado = mAuth.getCurrentUser();

        cargarPresupuesto();

        if (usuarioAutenticado != null) {
            String userID = usuarioAutenticado.getUid();
            obtenerDatosFB(userID);
        } else {
            Log.w("Firestore", "Usuario no autenticado");
        }


        // Listener para guardar el presupuesto en Firestore
        btnGuardarPresupuesto.setOnClickListener(v -> {
            String presupuestoStr = etPresupuesto.getText().toString();
            if (!presupuestoStr.isEmpty()) {
                int presupuesto = Integer.parseInt(presupuestoStr);
                tvProgresoActual.setText("Presupuesto: " + presupuesto + "€");
                guardarPresupuestoFirestore(presupuesto);
                guardarPresupuesto();
            } else {
                Toast.makeText(PresupuestoActivity.this, "Por favor, introduce una cantidad válida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Metodo para guardar el presupuesto en SharedPreferences
    private void guardarPresupuesto() {
        String presupuestoStr = etPresupuesto.getText().toString();
        if (!presupuestoStr.isEmpty()) {
            int presupuesto = Integer.parseInt(presupuestoStr);

            // Guardar el presupuesto en SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("PresupuestoPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("presupuestoMax", presupuesto);
            editor.apply();

            // Mostrar el presupuesto ingresado en el TextView
            tvProgresoActual.setText("Presupuesto: " + presupuesto + "€");
        } else {
            Toast.makeText(PresupuestoActivity.this, "Por favor, ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarPresupuesto() {
        SharedPreferences sharedPreferences = getSharedPreferences("PresupuestoPrefs", MODE_PRIVATE);
        int presupuestoGuardado = sharedPreferences.getInt("presupuestoMax", 0);  // 0 es el valor predeterminado si no se encuentra ningún valor

        // Mostrar el presupuesto guardado en el TextView
        if (presupuestoGuardado != 0) {
            tvProgresoActual.setText("Presupuesto: " + presupuestoGuardado + "€");
        }
    }

    private void obtenerDatosFB(String userID) {
        DocumentReference userRef = db.collection("presupuestos").document(userID);

        listenerRegistration = userRef.addSnapshotListener((doc, error) -> {
            if (error != null) {
                Log.w("Firestore", "Error al obtener datos", error);
                return;
            }

            if (doc != null && doc.exists()) {
                Long presupuestoMaxLong = doc.getLong("presupuestoMax");
                Long gastoActualLong = doc.getLong("gastoActual");

                int presupuestoTotal = presupuestoMaxLong != null ? presupuestoMaxLong.intValue() : 0;
                int gastosActuales = gastoActualLong != null ? gastoActualLong.intValue() : 0;

                arcProgressPresupuesto.setMax(presupuestoTotal);
                arcProgressPresupuesto.setProgress(gastosActuales);

                verificarPresupuesto(gastosActuales, presupuestoTotal);

            }
        });
    }

    private void verificarPresupuesto(int gastosActuales, int presupuestoTotal) {
        boolean excedido = gastosActuales > presupuestoTotal;
        if (excedido && !mensajeExcedidoMostrado) {
            Toast.makeText(this, "Has excedido tu presupuesto", Toast.LENGTH_SHORT).show();
            mensajeExcedidoMostrado = true;
        } else if (!excedido) {
            mensajeExcedidoMostrado = false;
        }
    }

    private void actualizarProgresoArc(int gastoActual) {
        arcProgressPresupuesto.setProgress(gastoActual);
    }

    private void animarProgreso(ArcProgress ArcProgressPresupuesto, int nuevoProgreso) {
        ObjectAnimator animator = ObjectAnimator.ofInt(ArcProgressPresupuesto, "progress", ArcProgressPresupuesto.getProgress(), nuevoProgreso);
        animator.setDuration(1000);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.start();
    }

    private void guardarPresupuestoFirestore(int presupuesto) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            Map<String, Object> datosPresupuesto = new HashMap<>();
            datosPresupuesto.put("presupuestoMax", presupuesto);


            FirebaseFirestore.getInstance().collection("presupuestos").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (!documentSnapshot.exists()) {
                            datosPresupuesto.put("gastoActual", 0);
                        }
                        db.collection("presupuestos").document(userId)
                                .set(datosPresupuesto, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    gastosActuales = 0; //reinicia los datos locales
                                    presupuestoTotal = presupuesto; //sincroniza los datos locales
                                    animarProgreso(arcProgressPresupuesto, 0); //reinicia el progreso
                                    Toast.makeText(PresupuestoActivity.this, "Presupuesto guardado correctamente", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PresupuestoActivity.this, "Error al guardar el presupuesto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
        }
    }


    public void agregarGastoAFirebase(double nuevoImporte) {
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual != null) {
            String userID = usuarioActual.getUid();
            DocumentReference userRef = db.collection("presupuestos").document(userID);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Long gastoActualLong = documentSnapshot.getLong("gastoActual");
                    int gastoActual = gastoActualLong != null ? gastoActualLong.intValue() : 0;

                    gastoActual += nuevoImporte;


                    Map<String, Object> actualizacion = new HashMap<>();
                    actualizacion.put("gastoActual", gastoActual);

                    userRef.set(actualizacion, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Gasto actualizado correctamente en Firebase"))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error al actualizar el gasto: " + e.getMessage()));
                }
            }).addOnFailureListener(e -> Log.w("Firestore", "Error al obtener gasto actual", e));
        }
    }
}