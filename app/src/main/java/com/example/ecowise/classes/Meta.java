package com.example.ecowise.classes;

public class Meta {
    private String id;
    private String titulo;
    private Double importeObjetivo = 0.0;
    private Double importeAhorrado = 0.0;
    private float progreso;
    private String fechaLimite;
    private boolean enProgreso, completada;


    public Meta(){

    }

    public Meta(String titulo, Double importeObjetivo, Double importeAhorrado, String fechaLimite) {
        this.titulo = titulo;
        this.importeObjetivo = importeObjetivo;
        this.importeAhorrado = importeAhorrado;
        this.fechaLimite = fechaLimite;
    }

    public Meta(String titulo, Double importeObjetivo, Double importeAhorrado, String fechaLimite, boolean enProgreso) {
        this.titulo = titulo;
        this.importeObjetivo = importeObjetivo;
        this.importeAhorrado = importeAhorrado;
        this.fechaLimite = fechaLimite;
        this.enProgreso = enProgreso;
    }

    public Meta(String id, String titulo, Double importeObjetivo, Double importeAhorrado, String fechaLimite, boolean enProgreso, boolean completada) {
        this.id = id;
        this.titulo = titulo;
        this.importeObjetivo = importeObjetivo;
        this.importeAhorrado = importeAhorrado;
        this.fechaLimite = fechaLimite;
        this.enProgreso = enProgreso;
        this.completada = completada;
    }

    public float getProgreso() {
        return progreso;
    }

    public void setProgreso(float progreso) {
        this.progreso = progreso;
    }



    public Double getImporteObjetivo() {
        return importeObjetivo;
    }

    public void setImporteObjetivo(Double importeObjetivo) {
        this.importeObjetivo = importeObjetivo;
    }

    public Double getImporteAhorrado() {
        return importeAhorrado;
    }

    public void setImporteAhorrado(Double importeAhorrado) {
        this.importeAhorrado = importeAhorrado;
    }

    public String getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(String fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public boolean isEnProgreso() {
        return enProgreso;
    }

    public void setEnProgreso(boolean enProgreso) {
        this.enProgreso = enProgreso;

        if (progreso == 100) {
            this.completada = true;
            this.enProgreso = false;
        } else if (progreso > 0) {
            this.enProgreso = true;
            this.completada = false;
        } else {
            this.enProgreso = false;
            this.completada = false;
        }
    }


    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
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

}
