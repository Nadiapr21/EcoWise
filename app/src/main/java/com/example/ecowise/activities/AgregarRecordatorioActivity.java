package com.example.ecowise.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecowise.R;
import com.example.ecowise.classes.Recordatorio;
import com.example.ecowise.ui.MainActivity;
import com.example.ecowise.ui.RegistrarGasto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AgregarRecordatorioActivity extends AppCompatActivity {
    private EditText etTitulo, etDescripcion, etFecha, etImporte, etFrecuencia;
    private Button btnGuardar, fabVolver;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_recordatorio);

        etTitulo = findViewById(R.id.etTitulo);
        etDescripcion = findViewById(R.id.etDescripcion);
        etFecha = findViewById(R.id.etFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        fabVolver = findViewById(R.id.fabVolver);
        etImporte = findViewById(R.id.etImporte);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        db = FirebaseFirestore.getInstance();

        etFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AgregarRecordatorioActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Mostrar la fecha seleccionada en el formato dd/MM/yyyy
                            String fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                            etFecha.setText(fechaSeleccionada);  // Establecer la fecha en el EditText
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });

        fabVolver.setOnClickListener(v -> volverAInicio());

        btnGuardar.setOnClickListener(v -> {
            String titulo = etTitulo.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            double importe = Double.parseDouble(etImporte.getText().toString());
            String frecuencia = etFrecuencia.getText().toString();
            String fecha = etFecha.getText().toString();

            Recordatorio recordatorio = new Recordatorio(titulo, importe, descripcion, frecuencia, fecha);

            db.collection("recordatorios").document()
                    .set(recordatorio)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AgregarRecordatorioActivity.this, "Recordatorio agregado", Toast.LENGTH_SHORT).show();
                        finish();

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AgregarRecordatorioActivity.this, "Error al agregar recordatorio", Toast.LENGTH_SHORT).show();

                    });
        });
    }

    private void volverAInicio() {
        Intent intent = new Intent(AgregarRecordatorioActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
