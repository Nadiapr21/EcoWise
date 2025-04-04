package com.example.ecowise.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.compose.runtime.internal.LiveLiteralFileInfo;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.activities.RecordatoriosActivity;
import com.example.ecowise.classes.Recordatorio;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;


public class RecordatorioAdapter extends RecyclerView.Adapter<RecordatorioAdapter.ViewHolder> {
    private List<Recordatorio> recordatorioList;
    private Context context;
    private int alarmID = 1;

    public RecordatorioAdapter(Context context, List<Recordatorio> recordatorioList) {
        this.recordatorioList = recordatorioList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecordatorioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recordatorio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordatorioAdapter.ViewHolder holder, int position) {
        Recordatorio recordatorio = recordatorioList.get(position);
        holder.tvTitulo.setText(recordatorio.getTitulo());
        holder.tvDescripcion.setText(recordatorio.getDescripcion());
        holder.tvFecha.setText(recordatorio.getFecha());
        holder.tvFrecuencia.setText(recordatorio.getFrecuencia());
        holder.tvImporte.setText(recordatorio.getImporte() + "€");
        holder.btnRecordatorio.setOnClickListener(v -> {
            mostrarDialogoConfiguracion(recordatorio);
        });

        holder.ivEliminar.setOnClickListener(v -> {
            eliminarRecordatorio(position, recordatorio);
        });
    }

    private void mostrarDialogoConfiguracion(Recordatorio recordatorio) {
        Calendar currentCalendar = Calendar.getInstance();


        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (timeView, hourOfDay, minute) -> {

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                long alarmTime = calendar.getTimeInMillis();

                RecordatoriosActivity.setAlarm(alarmID++, alarmTime, context, recordatorio.getTitulo());
                String titulo = recordatorio.getTitulo() != null ? recordatorio.getTitulo() : "Recordatorio sin título";
                Toast.makeText(context, "Recordatorio configurado para: " + titulo, Toast.LENGTH_SHORT).show();
            }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void eliminarRecordatorio(int position, Recordatorio recordatorio) {
        Recordatorio recordatorioEliminado = recordatorioList.get(position);
        recordatorioList.remove(position);
        notifyItemRemoved(position);

        Snackbar.make(((Activity) context).findViewById(android.R.id.content), "Recordatorio eliminado", Snackbar.LENGTH_LONG)
                .setAction("Deshacer", v -> {
                    recordatorioList.add(position, recordatorioEliminado);
                    notifyItemInserted(position);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("recordatorios").document(recordatorioEliminado.getId())
                            .set(recordatorioEliminado)
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Recordatorio restaurado correctamente", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(context, "Error al restaurar el recordatorio", Toast.LENGTH_SHORT).show());
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("recordatorios").document(recordatorio.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(context, "Recordatorio eliminado permanentemente", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar el recordatorio", Toast.LENGTH_SHORT).show());
                        }
                    }
                })
                .show();

    }

    @Override
    public int getItemCount() {
        return recordatorioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDescripcion, tvFecha, tvFrecuencia, tvImporte;
        ImageView ivEliminar, btnRecordatorio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloRecordatorio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionRecordatorio);
            tvFecha = itemView.findViewById(R.id.tvFechaRecordatorio);
            tvFrecuencia = itemView.findViewById(R.id.tvFrecuencia);
            tvImporte = itemView.findViewById(R.id.tvImporte);
            ivEliminar = itemView.findViewById(R.id.ivEliminar);
            btnRecordatorio = itemView.findViewById(R.id.btnRecordatorio);

        }
    }
}
