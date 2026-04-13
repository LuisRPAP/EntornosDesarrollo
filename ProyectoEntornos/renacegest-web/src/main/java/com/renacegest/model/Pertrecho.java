package com.renacegest.model;

public class Pertrecho {
    private Long id;
    private Long seccionId;
    private String seccionNombre;
    private String descripcion;
    private int integridad;
    private String estadoIa;
    private String tokenQr;
    private boolean disponible;

    public Pertrecho() {
    }

    public Pertrecho(Long id, Long seccionId, String seccionNombre, String descripcion, int integridad, String estadoIa, String tokenQr, boolean disponible) {
        this.id = id;
        this.seccionId = seccionId;
        this.seccionNombre = seccionNombre;
        this.descripcion = descripcion;
        this.integridad = integridad;
        this.estadoIa = estadoIa;
        this.tokenQr = tokenQr;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeccionId() {
        return seccionId;
    }

    public void setSeccionId(Long seccionId) {
        this.seccionId = seccionId;
    }

    public String getSeccionNombre() {
        return seccionNombre;
    }

    public void setSeccionNombre(String seccionNombre) {
        this.seccionNombre = seccionNombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIntegridad() {
        return integridad;
    }

    public void setIntegridad(int integridad) {
        this.integridad = integridad;
    }

    public String getEstadoIa() {
        return estadoIa;
    }

    public void setEstadoIa(String estadoIa) {
        this.estadoIa = estadoIa;
    }

    public String getTokenQr() {
        return tokenQr;
    }

    public void setTokenQr(String tokenQr) {
        this.tokenQr = tokenQr;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
