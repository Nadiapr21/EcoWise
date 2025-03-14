package com.example.ecowise.classes;

public class IngresosyGastos {
    private double importe;
    private String categoria;
    private String fecha;
    private String tipo;  //si es ingreso o gasto

    public IngresosyGastos(double importe, String categoria, String fecha, String tipo) {
        this.importe = importe;
        this.categoria = categoria;
        this.fecha = fecha;
        this.tipo = tipo;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
