package com.example.ecowise.classes;

public class Ingreso {
    private String id;
    private double importe;
    private String categoria;
    private String fecha;
    // Constructor con id, ya que cuando se trae de Firebase, se necesita un id
    public Ingreso(String id, double importe, String categoria, String fecha) {
        this.id = id;
        this.importe = importe;
        this.categoria = categoria;
        this.fecha = fecha;
    }
    // Constructor sin id, para crear un nuevo ingreso
    public Ingreso(double importe, String categoria, String fecha) {
        this.importe = importe;
        this.categoria = categoria;
        this.fecha = fecha;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
