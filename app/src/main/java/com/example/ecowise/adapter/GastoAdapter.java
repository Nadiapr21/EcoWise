package com.example.ecowise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.classes.Gasto;
import com.example.ecowise.ui.RegistrarGasto;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class GastoAdapter extends RecyclerView.Adapter<GastoAdapter.GastoViewHolder> {
    private ArrayList<Gasto> listaGastos;
    private Context context;
    private RegistrarGasto registrarGasto;

    // Constructor único que recibe tanto el contexto como la lista de gastos
    public GastoAdapter(RegistrarGasto registrarGasto, ArrayList<Gasto> listaGastos) {
        this.registrarGasto = registrarGasto;
        this.context = registrarGasto.getApplicationContext();
        this.listaGastos = listaGastos;
    }

    // Este método crea el ViewHolder para cada item
    @NonNull
    @Override
    public GastoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gasto, parent, false);
        return new GastoViewHolder(itemView);
    }

    // Este método vincula los datos con el ViewHolder
    @Override
    public void onBindViewHolder(@NonNull GastoViewHolder holder, int position) {
        Gasto gasto = listaGastos.get(position);
        holder.tvCategoria2.setText(gasto.getCategoria());
        holder.tvImporte2.setText(String.valueOf(gasto.getImporte()) + " €");
        holder.tvFecha2.setText(gasto.getFecha());

        holder.ivEliminar2.setOnClickListener(v -> {
            if (gasto.getId() != null && !gasto.getId().isEmpty()) {
                // Solo eliminar si el id no es nulo
                listaGastos.remove(position);
                eliminarGastoFirestore(gasto.getId()); //ID del gasto

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listaGastos.size());

                registrarGasto.eliminarGasto(gasto.getId());
            }else{
                Toast.makeText(context, "Error: el gasto no tiene un id válido", Toast.LENGTH_SHORT).show();
            };

        });
    }

    private void eliminarGastoFirestore(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("gastos")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Gasto eliminado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar el gasto", Toast.LENGTH_SHORT).show();
                });
    }

    ;

    // Este método devuelve el número total de elementos en la lista
    @Override
    public int getItemCount() {
        return listaGastos.size();
    }

    public static class GastoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria2;
        TextView tvImporte2;
        TextView tvFecha2;
        private ImageView ivEliminar2;

        public GastoViewHolder(View itemView) {
            super(itemView);
            // Aquí hacemos el findViewById para las vistas del item
            tvCategoria2 = itemView.findViewById(R.id.tvCategoria2);
            tvImporte2 = itemView.findViewById(R.id.tvImporte2);
            tvFecha2 = itemView.findViewById(R.id.tvFecha2);
            ivEliminar2 = itemView.findViewById(R.id.ivEliminar2);
        }
    }
}
