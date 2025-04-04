package com.example.ecowise.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecowise.R;
import com.example.ecowise.activities.EditarPerfilActivity;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;

public class PerfilActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private GestureDetector gestureDetector;
    private ImageView bsdBarra;
    private LinearLayout bottomSheet;
    private TextView tvEditarPerfil;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);
        cargarPerfilUsuario();
        mostrarBottomDialog();

        tvEditarPerfil = findViewById(R.id.tvEditarPerfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinatorLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LinearLayout bottomSheet = findViewById(R.id.linear_layout);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED); //Inicialmente colapsado


        bottomSheet.setOnTouchListener((v, event) -> {
            bottomSheetBehavior.setDraggable(true); //Es true para que sea deslizable
            bottomSheetBehavior.setPeekHeight(210);
            return false;
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //Expandido
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    //Colapsado
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                bottomSheet.setElevation(slideOffset * 10);
            }
        });





        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(PerfilActivity.this, "Pulse de nuevo para salir", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }

        });


        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling( MotionEvent e1,  MotionEvent e2, float velocityX, float velocityY) {
                //Detecta un deslizamiento hacia arriba
                if (e1.getY() - e2.getY() > 100){
                    mostrarBottomDialog();
                    return true;
                }
                return false;
            }
        });

    }

    private void cargarPerfilUsuario() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuarios").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {

                            String nombre = documentSnapshot.getString("nombre");
                            String email = documentSnapshot.getString("email");
                            String fotoPerfilURL = documentSnapshot.getString("fotoPerfil");
                            ImageView imagenPerfil = findViewById(R.id.imagen_perfil);


                            TextView mensajeBienvenida = findViewById(R.id.tvMensajeBienvenida); // Muestra el mensaje de bienvenida
                            TextView nombrePerfil = findViewById(R.id.nombre_perfil);
                            TextView correoPerfil = findViewById(R.id.correo_perfil);

                            mensajeBienvenida.setText("Hola, " + (nombre != null ? nombre : "Usuario") + "!");
                            nombrePerfil.setText(nombre != null ? nombre : "Sin nombre");
                            correoPerfil.setText(email != null ? email : "Sin correo");

                            if (fotoPerfilURL != null) {
                                new Thread(() -> {
                                    try {
                                        URL url = new URL(fotoPerfilURL);
                                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        runOnUiThread(() -> imagenPerfil.setImageBitmap(bitmap));
                                    } catch (Exception e) {
                                        Log.e("BitmapError", "Error al descargar o mostrar la imagen", e);
                                    }
                                }).start();

                            } else {
                                Log.w("Firestore", "URL de fotoPerfil es nula.");
                            }
                        } else {

                            Log.w("Firestore", "El documento del usuario no existe.");
                        }


                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error al recuperar el documento del usuario", e);
                    });
        }

    }

    @SuppressLint("PrivateResource")
    private void mostrarBottomDialog(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_perfil);

        TextView tvEditarPerfil = dialog.findViewById(R.id.tvEditarPerfil);
        ImageView btnCerrar = dialog.findViewById(R.id.btnCerrar);

        tvEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PerfilActivity", "Botón Editar perfil clickeado");
                dialog.dismiss();
                Intent intent = new Intent(PerfilActivity.this, EditarPerfilActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}