package com.renacegest.model;

public class ResultadoClasificacionIa {
    private Pertrecho pertrecho;
    private String categoriaPropuesta;
    private int confianza;
    private boolean autoValidado;

    public ResultadoClasificacionIa() {
    }

    public ResultadoClasificacionIa(Pertrecho pertrecho, String categoriaPropuesta, int confianza, boolean autoValidado) {
        this.pertrecho = pertrecho;
        this.categoriaPropuesta = categoriaPropuesta;
        this.confianza = confianza;
        this.autoValidado = autoValidado;
    }

    public Pertrecho getPertrecho() {
        return pertrecho;
    }

    public void setPertrecho(Pertrecho pertrecho) {
        this.pertrecho = pertrecho;
    }

    public String getCategoriaPropuesta() {
        return categoriaPropuesta;
    }

    public void setCategoriaPropuesta(String categoriaPropuesta) {
        this.categoriaPropuesta = categoriaPropuesta;
    }

    public int getConfianza() {
        return confianza;
    }

    public void setConfianza(int confianza) {
        this.confianza = confianza;
    }

    public boolean isAutoValidado() {
        return autoValidado;
    }

    public void setAutoValidado(boolean autoValidado) {
        this.autoValidado = autoValidado;
    }
}
