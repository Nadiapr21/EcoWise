package com.example.ecowise.classes;

public class Recordatorio {
    private String id;
    private String titulo;
    private double importe;
    private String frecuencia;
    private String descripcion;
    private String fecha;

    public Recordatorio(String titulo, double importe, String frecuencia, String descripcion, String fecha) {
        this.titulo = titulo;
        this.importe = importe;
        this.frecuencia = frecuencia;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public Recordatorio(String id, String titulo, double importe, String frecuencia, String descripcion, String fecha) {
        this.id = id;
        this.titulo = titulo;
        this.importe = importe;
        this.frecuencia = frecuencia;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
