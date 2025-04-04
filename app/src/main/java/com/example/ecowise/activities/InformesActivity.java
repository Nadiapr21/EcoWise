package com.example.ecowise.activities;

import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecowise.R;
import com.example.ecowise.classes.Gasto;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformesActivity extends AppCompatActivity {
    private final Map<String, Integer> coloresPorCategoria = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_informes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cargarDatosFB();


    }

    private void cargarDatosFB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Double> resumenPorCategoria = new HashMap<>();

        db.collection("gastos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String categoria = doc.getString("categoria");
                            double importe = doc.getDouble("importe");


                            if (!coloresPorCategoria.containsKey(categoria)) {
                                int color = Color.rgb(
                                        (int) (Math.random() * 256),
                                        (int) (Math.random() * 256),
                                        (int) (Math.random() * 256)
                                );
                                coloresPorCategoria.put(categoria, color);
                            }


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                resumenPorCategoria.put(categoria,
                                        resumenPorCategoria.getOrDefault(categoria, 0.0) + importe);
                            }
                        }


                        generarPDF(resumenPorCategoria);
                    } else {
                        Log.e("FirebaseError", "Error al leer los datos", task.getException());
                        Toast.makeText(this, "Error al cargar los datos de Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generarPDF(Map<String, Double> resumenPorCategoria) {
        PdfDocument pdfDocument = new PdfDocument();

        final int pageWidth = 595;
        final int pageHeight = 842;
        final int margin = 40;
        final int contentHeight = pageHeight - (2 * margin);
        final int maxBarWidth = pageWidth - (2 * margin);
        final int chartDiameter = 200;


        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        int x = margin;
        int y = margin;


        String titulo = "Informe de gestión de los gastos personales";
        paint.setTextSize(15);
        float textWidth = paint.measureText(titulo);
        canvas.drawText(titulo, (pageWidth - textWidth) / 2, margin + 20, paint);

        y += 60;

        int chartLeft = (pageWidth - chartDiameter) / 2;
        int chartTop = y;
        int chartRight = chartLeft + chartDiameter;
        int chartBottom = chartTop + chartDiameter;

        double total = 0.0;
        for (double importe : resumenPorCategoria.values()) {
            total += importe;
        }

        float startAngle = 0f;
        for (Map.Entry<String, Double> entry : resumenPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double importe = entry.getValue();
            float sweepAngle = (float) (importe / total * 360);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                paint.setColor(coloresPorCategoria.getOrDefault(categoria, Color.GRAY));
            }
            canvas.drawArc(
                    chartLeft, chartTop, chartRight, chartBottom,
                    startAngle, sweepAngle, true, paint
            );
            startAngle += sweepAngle;
        }

        y += chartDiameter + 20;
        paint.setTextSize(12);
        for (Map.Entry<String, Double> entry : resumenPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                paint.setColor(coloresPorCategoria.getOrDefault(categoria, Color.GRAY));
            }


            canvas.drawRect(x, y, x + 20, y + 20, paint);
            paint.setColor(Color.BLACK);
            canvas.drawText(categoria + " (" + entry.getValue() + "€)", x + 30, y + 15, paint);

            y += 30;
        }

        y += 120;



        double maxImporte = 0.0;
        for (double importe : resumenPorCategoria.values()) {
            if (importe > maxImporte) {
                maxImporte = importe;
            }

        }

        for (Map.Entry<String, Double> entry : resumenPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            double importe = entry.getValue();

            if (page == null || (y + 80 > contentHeight)) {
                if (page != null) {
                    pdfDocument.finishPage(page);
                }

                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = margin;

                canvas.drawText(titulo, (pageWidth - textWidth) / 2, margin + 20, paint);
                y += 60;
            }

            paint.setColor(Color.BLACK);
            canvas.drawText(categoria + " (" + importe + "€)", x, y, paint);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                paint.setColor(coloresPorCategoria.getOrDefault(categoria, Color.GRAY));
                int barWidth = (int) Math.min(maxBarWidth, maxBarWidth * (importe / maxImporte));
                canvas.drawRect(x, y + 20, x + barWidth, y + 50, paint);
                y += 80;
            }




        }


        pdfDocument.finishPage(page);


        try {
            OutputStream fos = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "InformeGastos.pdf");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                fos = getContentResolver().openOutputStream(
                        getContentResolver().insert(MediaStore.Files.getContentUri("external"), values)
                );
            }

            if (fos != null) {
                pdfDocument.writeTo(fos);
                fos.close();
                Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("PDFError", "Error al guardar el PDF", e);
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }
}