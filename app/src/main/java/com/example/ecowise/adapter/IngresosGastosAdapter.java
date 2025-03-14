package com.example.ecowise.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecowise.R;
import com.example.ecowise.classes.IngresosyGastos;

import java.util.ArrayList;

public class IngresosGastosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<IngresosyGastos> listaIngresosGastos;
    private static final int VIEW_TYPE_INGRESO = 0;
    private static final int VIEW_TYPE_GASTO = 1;

    public IngresosGastosAdapter(Context context, ArrayList<IngresosyGastos> listaIngresosGastos) {
        this.context = context;
        this.listaIngresosGastos = listaIngresosGastos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_INGRESO:
                view = LayoutInflater.from(context).inflate(R.layout.item_ingreso, parent, false);
                return new IngresoViewHolder(view);

            case VIEW_TYPE_GASTO:
                view = LayoutInflater.from(context).inflate(R.layout.item_gasto, parent, false);
                return new GastoViewHolder(view);

            default:
                return null;
        }
    }

    //Aqui asocio los datos de mi modelo con las vistas (elementos gráficos) que maneja el Viewholder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IngresosyGastos item = listaIngresosGastos.get(position);

        if (getItemViewType(position) == VIEW_TYPE_INGRESO) {
            IngresoViewHolder ingresoViewHolder = (IngresoViewHolder) holder;
            ingresoViewHolder.tvCategoria.setText(item.getCategoria());
            ingresoViewHolder.tvFecha.setText(item.getFecha());
            ingresoViewHolder.tvImporte.setText(String.format("€%.2f", item.getImporte()));
        } else {
            GastoViewHolder gastoViewHolder = (GastoViewHolder) holder;
            gastoViewHolder.tvCategoria2.setText(item.getCategoria());
            gastoViewHolder.tvFecha2.setText(item.getFecha());
            gastoViewHolder.tvImporte2.setText(String.format("€%.2f", item.getImporte()));
        }
    }
        //Aqui recupera los items de la listaIngresosGastos
    @Override
    public int getItemCount() {
        return listaIngresosGastos.size();
    }

    //Recupera el tipo de item (si es ingreso o gasto)
    @Override
    public int getItemViewType(int position) {
        String tipo = listaIngresosGastos.get(position).getTipo();
        switch (tipo){
            case "ingreso":
                return VIEW_TYPE_INGRESO;
            case "gasto":
                return VIEW_TYPE_GASTO;
            default:
                return -1;
        }
    }


    public static class IngresoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria, tvFecha, tvImporte;

        public IngresoViewHolder(View itemView) {
            super(itemView);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvImporte = itemView.findViewById(R.id.tvImporte);
        }
    }

    public static class GastoViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria2, tvFecha2, tvImporte2;

        public GastoViewHolder(View itemView) {
            super(itemView);
            tvCategoria2 = itemView.findViewById(R.id.tvCategoria2);
            tvFecha2 = itemView.findViewById(R.id.tvFecha2);
            tvImporte2 = itemView.findViewById(R.id.tvImporte2);
        }
    }
}
