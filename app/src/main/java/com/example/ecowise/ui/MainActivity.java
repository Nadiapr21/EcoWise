package com.example.ecowise.ui;

import static android.content.ContentValues.TAG;
import androidx.appcompat.widget.Toolbar;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.adapter.IngresosGastosAdapter;
import com.example.ecowise.classes.IngresosyGastos;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.IDN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BarChart graficaBarras;
    private Button fabAddGasto,fabAddIngreso;
    private FloatingActionButton fabPrincipal;
    private IngresosGastosAdapter adapter;
    private boolean isOpen = false;
    private RecyclerView rvIngresosGastos;
    private ArrayList<IngresosyGastos> listaIngresosGastos;
    private Toolbar optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        graficaBarras = findViewById(R.id.graficaBarras);

        //Inicializo fabAddIngreso
        fabAddIngreso = findViewById(R.id.fabAddIngreso);
        fabAddIngreso.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrarIngreso.class);
            startActivity(intent);
        });

        //Inicializo fabPrincipal
        fabPrincipal = findViewById(R.id.fabPrincipal);
        fabPrincipal.setOnClickListener(v -> {
            if (isOpen) {
                animacionButton(fabAddGasto, false, -80f);
                animacionButton(fabAddIngreso, false, -100f);
            } else {
                animacionButton(fabAddGasto, true, -20f);
                animacionButton(fabAddIngreso, true, -10f);
            }
            isOpen = !isOpen;
        });


        //Inicializo rvIngresosGastos
        rvIngresosGastos = findViewById(R.id.rvIngresosGastos);
        rvIngresosGastos.setLayoutManager(new LinearLayoutManager(this));
        listaIngresosGastos = new ArrayList<>();
        adapter = new IngresosGastosAdapter(this, listaIngresosGastos);
        rvIngresosGastos.setAdapter(adapter);

        cargarDatosFirebase();


        configurarBarChart();
        getEntries();

        fabAddGasto = findViewById(R.id.fabAddGasto);
        fabAddGasto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistrarGasto.class);
            startActivity(intent);
            finish();
        });

        optionsMenu = findViewById(R.id.optionsMenu);
        setSupportActionBar(optionsMenu);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    private void cargarDatosFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //cargar ingresos de firebase
        db.collection("ingresos")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar los ingresos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        for (DocumentSnapshot document : value.getDocuments()) {
                            double importe = document.getDouble("importe");
                            String categoria = document.getString("categoria");
                            String fecha = document.getString("fecha");
                            listaIngresosGastos.add(new IngresosyGastos(importe, categoria, fecha, "ingreso"));
                        }
                    }

                    //cargar gastos de firebase
                    db.collection("ingresos")
                            .addSnapshotListener((value2, error2) -> {
                                if (error2 != null) {
                                    Toast.makeText(MainActivity.this, "Error al cargar los gastos", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (value2 != null) {
                                    for (DocumentSnapshot document : value2.getDocuments()) {
                                        double importe = document.getDouble("importe");
                                        String categoria = document.getString("categoria");
                                        String fecha = document.getString("fecha");
                                        listaIngresosGastos.add(new IngresosyGastos(importe, categoria, fecha, "gasto"));
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            });
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


//    private void cerrarMenu() {
//        fabRegistrarGasto.animate().translationY(0f).alpha(0f).setDuration(300).withEndAction(() ->
//                fabRegistrarGasto.setVisibility(View.GONE)).start();
//
//        fabAddMeta.animate().translationY(0f).alpha(0f).setDuration(300).withEndAction(() ->
//                fabAddMeta.setVisibility(View.GONE)).start();
//
//        fabNotasPersonales.animate().translationY(0f).alpha(0f).setDuration(300).withEndAction(() ->
//                fabNotasPersonales.setVisibility(View.GONE)).start();
//
//        isMenuOpen = false;
//    }

//    private void abrirMenu() {
//        fabRegistrarGasto.setVisibility(View.VISIBLE);
//        fabRegistrarGasto.animate().translationY(-80f).alpha(1f).setDuration(300).start();
//
//        fabAddMeta.setVisibility(View.VISIBLE);
//        fabAddMeta.animate().translationY(-140f).alpha(1f).setDuration(300).start();
//
//        fabNotasPersonales.setVisibility(View.VISIBLE);
//        fabNotasPersonales.animate().translationY(-200f).alpha(1f).setDuration(300).start();
//
//        isMenuOpen = true;
//    }

    private void configurarBarChart() {
        graficaBarras.setTouchEnabled(true);  // Habilitar interacción táctil
        graficaBarras.setPinchZoom(true);  // Habilitar zoom con gestos de pinza
        graficaBarras.setScaleEnabled(true); // Permitir escalar el gráfico
        graficaBarras.getDescription().setEnabled(false);  // Deshabilitar descripción predeterminada
        graficaBarras.setDrawGridBackground(false); // Quitar fondo de cuadrícula
        graficaBarras.setExtraOffsets(10, 10, 10, 10); // Añadir márgenes

        // Configurar el eje X
        XAxis xAxis = graficaBarras.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Posición en la parte inferior
        xAxis.setDrawGridLines(false); // Quitar líneas de cuadrícula del eje X
        xAxis.setGranularity(1f); // Intervalo mínimo entre valores

        // Configurar el eje Y izquierdo
        YAxis leftAxis = graficaBarras.getAxisLeft();
        leftAxis.setDrawGridLines(false); // Mostrar líneas de cuadrícula en el eje Y
        leftAxis.setGranularityEnabled(true); // Habilitar granularidad para valores consecutivos

        // Deshabilitar el eje Y derecho (opcional)
        YAxis rightAxis = graficaBarras.getAxisRight();
        rightAxis.setEnabled(false);

        // Configurar la leyenda
        Legend legend = graficaBarras.getLegend();
        legend.setForm(Legend.LegendForm.NONE); // Representación en forma de cuadrados (para gráfico de barras)
        legend.setTextSize(12f); // Tamaño de texto
        legend.setTextColor(Color.BLACK); // Color del texto
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false); // Posición por fuera del gráfico

        // Cambiar el tipo de gráfico a BarChart
        graficaBarras.setData(null); // Limpiar cualquier dato existente

        // Configurar el gráfico de barras (BarChart)
        BarDataSet barDataSet = new BarDataSet(getEntries(), "");
        barDataSet.setColor(Color.rgb(91, 134, 184));  // Color de las barras
        BarData barData = new BarData(barDataSet);
        graficaBarras.setData(barData);  // Establecer los datos al gráfico de barras
        graficaBarras.invalidate();  // Refrescar el gráfico
    }

    private List<BarEntry> getEntries() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 4f));  // X: 0, Y: 4
        entries.add(new BarEntry(1f, 8f));  // X: 1, Y: 8
        entries.add(new BarEntry(2f, 6f));  // X: 2, Y: 6
        entries.add(new BarEntry(3f, 12f)); // X: 3, Y: 12
        entries.add(new BarEntry(4f, 3f));  // X: 4, Y: 3
        return entries;
    }
}