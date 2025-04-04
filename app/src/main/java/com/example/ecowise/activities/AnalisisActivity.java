package com.example.ecowise.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.adapter.GastoAdapter;
import com.example.ecowise.classes.Gasto;
import com.example.ecowise.ui.MainActivity;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Map;

public class AnalisisActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    //Declaro los graficos
    private PieChart pieChart;
    private LineChart lineChart;
    private BubbleChart bubbleChart;
    private ArrayList<Gasto> listaGastos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analisis);  // Aquí usamos el layout correspondiente a una actividad

        //Inicializo los graficos
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        bubbleChart = findViewById(R.id.bubbleChart);


        //Declaro una instancia de Firestore
        db = FirebaseFirestore.getInstance();
        obtenerDatosDeFirebase(); //metodo para obtener los datos de firebase
    }

    private void obtenerDatosDeFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gastos")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error al cargar los gastos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        // Inicializar listas para las gráficas
                        ArrayList<BubbleEntry> bubbleEntries = new ArrayList<>();
                        ArrayList<Entry> lineEntries = new ArrayList<>();
                        ArrayList<PieEntry> pieEntries = new ArrayList<>();

                        int i = 0;  // Usado para agregar un índice para los gráficos


                        for (DocumentSnapshot document : value.getDocuments()) {
                            double importe = document.getDouble("importe");
                            String categoria = document.getString("categoria");

                            //Agregar los datos a las colecciones de gráficos
                            bubbleEntries.add(new BubbleEntry(i, (float) importe, 1));  // Usamos 1 como tamaño predeterminado
                            lineEntries.add(new Entry(i, (float) importe));
                            pieEntries.add(new PieEntry((float) importe, categoria));

                            i++;
                        }

                        //Para el gráfico de burbujas
                        BubbleDataSet bubbleDataSet = new BubbleDataSet(bubbleEntries, "Gastos por Categoría");
                        BubbleData bubbleData = new BubbleData(bubbleDataSet);
                        bubbleChart.setData(bubbleData);
                        bubbleDataSet.setColor(Color.MAGENTA);
                        bubbleChart.getDescription().setText("Gastos en diferentes categorías");
                        bubbleChart.invalidate(); // Refresca el gráfico


                        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Gastos a lo largo del tiempo");
                        LineData lineData = new LineData(lineDataSet);
                        lineChart.setData(lineData);
                        lineChart.getDescription().setText("Evolución de los gastos");
                        lineDataSet.setColor(Color.MAGENTA);
                        lineDataSet.setCircleColor(Color.BLACK);
                        lineChart.invalidate(); // Refresca el gráfico


                        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Categorías de Gastos");
                        PieData pieData = new PieData(pieDataSet);
                        pieDataSet.setColors(new int[]{Color.MAGENTA, Color.CYAN, Color.DKGRAY});
                        pieChart.setData(pieData);
                        pieChart.getDescription().setText("Distribución de los gastos por categoría");
                        pieChart.invalidate(); // Refresca el gráfico // Refresca el gráfico
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}


