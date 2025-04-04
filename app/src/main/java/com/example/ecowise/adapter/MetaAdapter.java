package com.example.ecowise.adapter;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.classes.Meta;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MetaAdapter extends RecyclerView.Adapter<MetaAdapter.MetaViewHolder> {
    private List<Meta> metasList;
    private Context context;

    public MetaAdapter(Context context, List<Meta> metasList) {
        this.metasList = metasList;
        this.context = context;
    }

    @NonNull
    @Override
    public MetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meta, parent, false);
        return new MetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MetaViewHolder holder, int position) {
        Meta meta = metasList.get(position);
        holder.tvTitulo.setText(meta.getTitulo());
        holder.tvImporteObjetivo.setText("Objetivo: " + meta.getImporteObjetivo() + "€");
        holder.tvImporteAhorrado.setText("Ahorrado: " + meta.getImporteAhorrado() + "€");
        holder.tvFechaLimite.setText("Límite: " + meta.getFechaLimite());
        holder.progresoMeta.setProgress((int) meta.getProgreso());

        holder.ivEliminar.setOnClickListener(v -> {
            eliminarMeta(position, meta.getId());
        });


        double progreso = 0;
        if (meta.getImporteObjetivo() > 0) {
            progreso = (meta.getImporteAhorrado() / meta.getImporteObjetivo()) * 100;
        }
        holder.progresoMeta.setProgress((int) progreso);

        holder.btnAgregarAhorrado.setOnClickListener(v -> {

            mostrarDialogoAgregarAhorro(meta, position);
        });
    }

    private void eliminarMeta(int position, String id) {
        metasList.remove(position);
        notifyItemRemoved(position);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("metas").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Meta eliminada correctamente", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar la meta", Toast.LENGTH_SHORT).show());
    }

    private void mostrarDialogoAgregarAhorro(Meta meta, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Agregar ahorro");
        EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            double nuevoAhorro = Double.parseDouble(input.getText().toString());
            meta.setImporteAhorrado((meta.getImporteAhorrado() + nuevoAhorro));
            notifyItemChanged(position);


        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    @Override
    public int getItemCount() {
        return metasList.size();
    }

    public static class MetaViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progresoMeta;
        TextView tvTitulo, tvImporteObjetivo, tvImporteAhorrado, tvFechaLimite, tvEstado;
        Button btnAgregarAhorrado;
        ImageView ivEliminar;

        public MetaViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloMeta);
            tvImporteObjetivo = itemView.findViewById(R.id.tvImporteObjetivo);
            tvImporteAhorrado = itemView.findViewById(R.id.tvImporteAhorradoMeta);
            tvFechaLimite = itemView.findViewById(R.id.tvFechaLimiteMeta);
            tvEstado = itemView.findViewById(R.id.tvEstadoMeta);
            progresoMeta = itemView.findViewById(R.id.progresoMeta);
            btnAgregarAhorrado = itemView.findViewById(R.id.btnAgregarAhorrado);
            ivEliminar = itemView.findViewById(R.id.ivEliminar);
        }
    }
}