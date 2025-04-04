package com.example.ecowise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.classes.Nota;

import java.util.ArrayList;


public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {
    private Context context;
    private ArrayList<Nota> listaNotas;
    private OnNotaClickListener listener;

    public interface OnNotaClickListener {
        void onNotaClick(Nota nota);
    }

    public NotaAdapter(Context context, ArrayList<Nota> listaNotas, OnNotaClickListener listener) {
        this.context = context;
        this.listaNotas = listaNotas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nota, parent, false);
        return new NotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotaAdapter.NotaViewHolder holder, int position) {
        Nota nota = listaNotas.get(position);
        holder.tvTituloNota.setText(nota.getTitulo());
        holder.tvFechaNota.setText(nota.getFechaCreacion());
        holder.tvContenidoNota.setText(nota.getContenido());

        holder.itemView.setOnClickListener(v -> listener.onNotaClick(nota));

    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    public static class NotaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTituloNota, tvFechaNota, tvContenidoNota;

        public NotaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTituloNota = itemView.findViewById(R.id.tvTituloNota);
            tvFechaNota = itemView.findViewById(R.id.tvFechaNota);
            tvContenidoNota = itemView.findViewById(R.id.tvContenidoNota);
        }
    }
}
