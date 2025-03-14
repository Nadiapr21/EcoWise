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
import com.example.ecowise.classes.Ingreso;
import com.example.ecowise.ui.RegistrarIngreso;

import java.util.ArrayList;

public class IngresoAdapter extends RecyclerView.Adapter<IngresoAdapter.IngresoViewHolder> {
    private ArrayList<Ingreso> listaIngresos;
    private Context context;
    private RegistrarIngreso registrarIngreso;

    public IngresoAdapter(RegistrarIngreso registrarIngreso, ArrayList<Ingreso> listaIngresos) {
        this.registrarIngreso = registrarIngreso;
        this.context = registrarIngreso.getApplicationContext();
        this.listaIngresos = listaIngresos;
    }

    @NonNull
    @Override
    public IngresoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingreso, parent, false);
        return new IngresoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull IngresoViewHolder holder, int position) {
        Ingreso ingreso = listaIngresos.get(position);
        holder.tvCategoria.setText(ingreso.getCategoria());
        holder.tvImporte.setText(String.valueOf(ingreso.getImporte()) + " €");
        holder.tvFecha.setText(ingreso.getFecha());
        
        holder.ivEliminar.setOnClickListener(v -> {
            if (ingreso.getId() != null && !ingreso.getId().isEmpty()){
                listaIngresos.remove(position);
                eliminarIngresoFirestore(ingreso.getId());
                
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listaIngresos.size());
                
                registrarIngreso.eliminarIngreso(ingreso.getId());
            }else{
                Toast.makeText(context, "Error: el ingreso no tiene un id válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarIngresoFirestore(String id) {
    }


    @Override
    public int getItemCount() {
        return listaIngresos.size();
    }

    public static class IngresoViewHolder extends RecyclerView.ViewHolder {
        TextView tvImporte;
        TextView tvCategoria;
        TextView tvFecha;
        private ImageView ivEliminar;

    public IngresoViewHolder(View itemView){
        super(itemView);

        tvImporte = itemView.findViewById(R.id.tvImporte);
        tvCategoria = itemView.findViewById(R.id.tvCategoria);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        ivEliminar = itemView.findViewById(R.id.ivEliminar);
    }

    }
}


