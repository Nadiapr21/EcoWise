package com.example.ecowise.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.activities.InformesActivity;
import com.example.ecowise.activities.NotasActivity;
import com.example.ecowise.adapter.GastoAdapter;
import com.example.ecowise.classes.Gasto;
import com.example.ecowise.activities.AnalisisActivity;
import com.example.ecowise.activities.MetasActivity;
import com.example.ecowise.activities.PresupuestoActivity;
import com.example.ecowise.activities.RecordatoriosActivity;
import com.example.ecowise.sqlite.DBHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarChart graficaBarras;
    private Button fabAddGasto;
    private FloatingActionButton fabPrincipal;
    private GastoAdapter adapter;
    private boolean isOpen = false;
    private RecyclerView rvGastos;
    private ArrayList<Gasto> listaGastos;
    private FirebaseFirestore db;
    private Toolbar optionsMenu;
    private BottomNavigationView bottomNavigationView;
    private boolean doubleBackToExitPressedOnce = false;
    private ImageView ivVolver;
    private FragmentContainerView fragmentContainerView;
    private ScrollView scrollView;
    private LinearLayout contenido;
    private FirebaseMessaging firebaseMessaging;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarTemaGuardado();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);

        FirebaseApp.initializeApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DBHelper dbHelper = new DBHelper(this);
        try{
            dbHelper.checkycopiarDB();
        }catch (Exception e){
            Log.e("MainActivity", "Error al copiar la base de datos: " + e.getMessage());
        }

        SQLiteDatabase SQLitedb = dbHelper.getWritableDatabase();



        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (doubleBackToExitPressedOnce) {
                            finish();
                            return;
                        }
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(MainActivity.this, "Pulse de nuevo para salir", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
                    }

                });



        //Inicializo bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Le seteo un OnItemSelectedListener para navegar entre distintas actividades
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_inicio) {
                    return true;
                } else if (id == R.id.nav_graficos) {
                    startActivity(new Intent(MainActivity.this, AnalisisActivity.class));
                    return true;
                } else if (id == R.id.nav_recordatorios) {
                    startActivity(new Intent(MainActivity.this, RecordatoriosActivity.class));
                    return true;
                } else if (id == R.id.nav_metas) {
                    startActivity(new Intent(MainActivity.this, MetasActivity.class));
                    return true;
                }
                return false;
            }
        });


        graficaBarras = findViewById(R.id.graficaBarras);

        db = FirebaseFirestore.getInstance();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){

            scrollView = findViewById(R.id.scrollView);
            contenido = findViewById(R.id.contenido);
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > oldScrollY) {
                        bottomNavigationView.animate()
                                .translationY(bottomNavigationView.getHeight())
                                .alpha(0f)
                                .setDuration(300)
                                .start();
                    }else if (scrollY < oldScrollY){
                        bottomNavigationView.animate()
                                .translationY(0)
                                .alpha(1f)
                                .setDuration(300)
                                .start();
                    }


                }
            });

        }



        //Inicializo fabPrincipal
        fabPrincipal = findViewById(R.id.fabPrincipal);

        //onClickListener para la animacion
        fabPrincipal.setOnClickListener(v -> {
            if (isOpen) {
                animacionButton(fabAddGasto, false, -80f);
            } else {
                animacionButton(fabAddGasto, true, -20f);
            }
            isOpen = !isOpen;
        });


        //Inicializo rvGastos
        rvGastos = findViewById(R.id.rvGastos);
        rvGastos.setLayoutManager(new LinearLayoutManager(this));
        listaGastos = new ArrayList<>(); //ArrayList para la lista de gastos
        adapter = new GastoAdapter(MainActivity.this, listaGastos); //Creo un objeto GastoAdapter para los gastos
        rvGastos.setAdapter(adapter); //Al recyclerview le seteo el adapter

        cargarDatosFirebase();
        cargarDatosGrafica();
        configurarBarChart();

        fabAddGasto = findViewById(R.id.fabAddGasto);
        fabAddGasto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrarGasto.class);
            startActivity(intent);

        });

        optionsMenu = findViewById(R.id.optionsMenu);
        setSupportActionBar(optionsMenu);

    }





    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    private void askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {

                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }



    private void aplicarTemaGuardado() {
        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        int themePosition = preferences.getInt("theme", 0);

        switch (themePosition) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void cargarDatosGrafica() {
        db.collection("gastos")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            List<BarEntry> entries = new ArrayList<>();

                            int index = 0;
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                double importe = document.getDouble("importe");
                                entries.add(new BarEntry(index, (float) importe));
                                index++;
                            }
                            actualizarGrafica(entries);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Error al obtener datos", e);
                    }
                });
    };

    private void actualizarGrafica(List<BarEntry> entries) {

        BarDataSet barDataSet = new BarDataSet(entries, "Gastos");
        barDataSet.setColor(Color.rgb(91, 134, 184));
        BarData barData = new BarData(barDataSet);
        graficaBarras.setData(barData);
        graficaBarras.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_perfil) {
            abrirPerfil();
            return true;
        } else if (id == R.id.nav_ajustes) {
            abrirAjustes();
            return true;
        } else if (id == R.id.nav_cerrarSesion) {
            logOut();
            return true;
        } else if (id == R.id.nav_ayuda) {
            abrirAyuda();
            return true;
        }else if (id == R.id.nav_notas){
            abrirNotas();
            return true;
        } else if (id == R.id.nav_presupuesto) {
            abrirPresupuesto();
            return true;
        }else if (id == R.id.nav_informes){
            abrirInforme();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void abrirInforme() {
        startActivity(new Intent(this, InformesActivity.class));
    }


    private void abrirPresupuesto() {
        startActivity(new Intent(this, PresupuestoActivity.class));
    }

    private void abrirNotas() {
        startActivity( new Intent(MainActivity.this, NotasActivity.class));
    }

    private void abrirAyuda() {
        startActivity(new Intent(this, AyudaActivity.class));
    }

    private void abrirAjustes() {
        startActivity(new Intent(this, AjustesActivity.class));
    }

    private void abrirPerfil() {
        startActivity(new Intent(this, PerfilActivity.class));
    }

    private void logOut() {
        new AlertDialog.Builder(this)
                .setTitle("Cierre de sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(this, LoginActivity.class);
                    //Esto es para limpiar la pila de tasks / actividades anteriores
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    private void cargarDatosFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

                    //cargar gastos de firebase
                    db.collection("gastos")
                            .addSnapshotListener((value, error) -> {
                                if (error != null) {
                                    Toast.makeText(MainActivity.this, "Error al cargar los gastos", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (value != null) {
                                    listaGastos.clear();
                                    for (DocumentSnapshot document : value.getDocuments()) {
                                        String id = document.getId();
                                        double importe = document.getDouble("importe");
                                        String categoria = document.getString("categoria");
                                        String fecha = document.getString("fecha");
                                        listaGastos.add(new Gasto(id, importe, categoria, fecha));
                                    }

                                }
                                adapter.notifyDataSetChanged();
                            });
    }

    private void animacionButton(Button button, boolean mostrar, float transicionY) {
        if (mostrar) {
            button.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(button, "translationY", 0f, transicionY);
            animator.setDuration(300);
            animator.start();
        } else {
            ObjectAnimator animator = ObjectAnimator.ofFloat(button, "translationY", transicionY, 0f);
            animator.setDuration(500);
            animator.start();
            button.postDelayed(() -> button.setVisibility(View.GONE), 50);
        }
    }


    private void configurarBarChart() {
        graficaBarras.setTouchEnabled(true);
        graficaBarras.setPinchZoom(true);
        graficaBarras.setScaleEnabled(true);
        graficaBarras.getDescription().setEnabled(false);
        graficaBarras.setDrawGridBackground(false);
        graficaBarras.setExtraOffsets(10, 10, 10, 10);


        XAxis xAxis = graficaBarras.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);


        YAxis leftAxis = graficaBarras.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);


        YAxis rightAxis = graficaBarras.getAxisRight();
        rightAxis.setEnabled(false);


        Legend legend = graficaBarras.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setTextSize(12f);
        legend.setTextColor(Color.WHITE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);


        graficaBarras.setData(null);


    }

}