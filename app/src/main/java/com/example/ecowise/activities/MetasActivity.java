package com.example.ecowise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.adapter.MetaAdapter;
import com.example.ecowise.classes.Meta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MetasActivity extends AppCompatActivity {

    private RecyclerView rvMetas;
    private MetaAdapter metaAdapter;
    private List<Meta> metasList;
    private FirebaseFirestore db;
    private FloatingActionButton fabAgregarMeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metas);


        rvMetas = findViewById(R.id.rvMetas);
        rvMetas.setLayoutManager(new LinearLayoutManager(this));
        fabAgregarMeta = findViewById(R.id.fabAgregarMeta);


        metasList = new ArrayList<>();
        metaAdapter = new MetaAdapter(this, metasList);
        rvMetas.setAdapter(metaAdapter);

        db = FirebaseFirestore.getInstance();

        cargarMetasFirebase();

        fabAgregarMeta.setOnClickListener(v -> {
            Intent intent = new Intent(MetasActivity.this, NuevaMetaActivity.class);
            startActivity(intent);
        });
    }

    private void cargarMetasFirebase() {
        db.collection("metas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    metasList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Meta meta = document.toObject(Meta.class);
                        metasList.add(meta);
                    }
                    metaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar las metas", Toast.LENGTH_SHORT).show();
                });
    }
}


