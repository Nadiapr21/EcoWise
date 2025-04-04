package com.example.ecowise.classes;

public class Presupuesto {
    private int id;
    private double importe;
    private String periodo; //Si es mensual, anual...
    private int usuarioId;

    public Presupuesto(double importe, String periodo, int usuarioId) {
        this.importe = importe;
        this.periodo = periodo;
        this.usuarioId = usuarioId;
    }

    public Presupuesto(int id, double importe, String periodo, int usuarioId) {
        this.id = id;
        this.importe = importe;
        this.periodo = periodo;
        this.usuarioId = usuarioId;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }
}
