package com.renacegest.model;

public class Guardia {
    private Long id;
    private String nombreReal;
    private String apodo;
    private String rango;
    private String claveAcceso;
    private int puntosGracia;
    private String estadoHonor;
    private boolean maestreActivo;

    public Guardia() {
    }

    public Guardia(Long id, String nombreReal, String apodo, String rango, int puntosGracia, String estadoHonor, boolean maestreActivo) {
        this(id, nombreReal, apodo, rango, "", puntosGracia, estadoHonor, maestreActivo);
    }

    public Guardia(Long id, String nombreReal, String apodo, String rango, String claveAcceso, int puntosGracia, String estadoHonor, boolean maestreActivo) {
        this.id = id;
        this.nombreReal = nombreReal;
        this.apodo = apodo;
        this.rango = rango;
        this.claveAcceso = claveAcceso;
        this.puntosGracia = puntosGracia;
        this.estadoHonor = estadoHonor;
        this.maestreActivo = maestreActivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public void setNombreReal(String nombreReal) {
        this.nombreReal = nombreReal;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public int getPuntosGracia() {
        return puntosGracia;
    }

    public void setPuntosGracia(int puntosGracia) {
        this.puntosGracia = puntosGracia;
    }

    public String getEstadoHonor() {
        return estadoHonor;
    }

    public void setEstadoHonor(String estadoHonor) {
        this.estadoHonor = estadoHonor;
    }

    public boolean isMaestreActivo() {
        return maestreActivo;
    }

    public void setMaestreActivo(boolean maestreActivo) {
        this.maestreActivo = maestreActivo;
    }
}
