package com.example.ecowise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecowise.adapter.NotaAdapter;
import com.example.ecowise.classes.Nota;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.ecowise.R;

import java.util.ArrayList;

public class NotasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotaAdapter notaAdapter;
    private ArrayList<Nota> listaNotas;
    private FloatingActionButton fabAddNota;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notas);

        recyclerView = findViewById(R.id.rvNotas);
        fabAddNota = findViewById(R.id.fabAddNota);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaNotas = new ArrayList<>();
        notaAdapter = new NotaAdapter(this, listaNotas, this::abrirNota);
        recyclerView.setAdapter(notaAdapter);

        db = FirebaseFirestore.getInstance();

        cargarNotas(); // metodo para cargar notas desde Firestore

        fabAddNota.setOnClickListener(v -> {
            startActivity(new Intent(this, NuevaNotaActivity.class));
        });
    }

    private void abrirNota(Nota nota) {
        Intent intent = new Intent(this, DetalleNotaActivity.class);
        intent.putExtra("notaId", nota.getId());
        startActivity(intent);
    }

    private void cargarNotas() {
        db.collection("notas").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null){
                    Toast.makeText(NotasActivity.this, "Error al cargar las notas", Toast.LENGTH_SHORT).show();
                    return;
                }
                listaNotas.clear();
                if (value != null){
                    for (DocumentSnapshot doc : value.getDocuments()){
                        Nota nota = doc.toObject(Nota.class);
                        if (nota != null){
                            nota.setId(doc.getId()); //guardar el id del documento
                            listaNotas.add(nota);
                        }
                    }
                }
                notaAdapter.notifyDataSetChanged();
            }
        });
    }


}