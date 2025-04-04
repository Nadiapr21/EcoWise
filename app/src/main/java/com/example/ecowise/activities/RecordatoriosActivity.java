package com.example.ecowise.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.adapter.RecordatorioAdapter;
import com.example.ecowise.classes.Recordatorio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecordatoriosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecordatorioAdapter adapter;
    private FirebaseFirestore db;
    private List<Recordatorio> recordatorioList;
    private FloatingActionButton fabAgregar;
    private TextView notificationsTime;
    private int alarmID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios);



        recyclerView = findViewById(R.id.rvRecordatorios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recordatorioList = new ArrayList<>();
        cargarRecordatorios();

        adapter = new RecordatorioAdapter(this, recordatorioList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        fabAgregar = findViewById(R.id.fabAgregar);

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(RecordatoriosActivity.this, AgregarRecordatorioActivity.class);
            startActivity(intent);
        });
    }

    public static void setAlarm(int i, long timestamp, Context context, String titulo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("titulo", titulo);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(context, i, alarmIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);
    }

    private void cargarRecordatorios() {
        db = FirebaseFirestore.getInstance();
        db.collection("recordatorios")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recordatorioList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String titulo = document.getString("titulo");
                            double importe = document.getDouble("importe");
                            String descripcion = document.getString("descripcion");
                            String fecha = document.getString("fecha");
                            String frecuencia = document.getString("frecuencia");

                            recordatorioList.add(new Recordatorio(id, titulo, importe, descripcion, fecha, frecuencia));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(RecordatoriosActivity.this, "Error al cargar recordatorios", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


