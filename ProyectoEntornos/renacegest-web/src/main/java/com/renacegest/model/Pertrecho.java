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
    private boolean activo;
    private double valorEconomico;
    private String fechaCreacion;
    private String fechaBaja;
    private String motivoBaja;

    public Pertrecho() {
    }

    public Pertrecho(Long id, Long seccionId, String seccionNombre, String descripcion, int integridad, String estadoIa, String tokenQr, boolean disponible) {
        this(id, seccionId, seccionNombre, descripcion, integridad, estadoIa, tokenQr, disponible, true, 0.0, null, null, null);
    }

    public Pertrecho(Long id, Long seccionId, String seccionNombre, String descripcion, int integridad, String estadoIa, String tokenQr, boolean disponible, boolean activo, double valorEconomico, String fechaCreacion, String fechaBaja, String motivoBaja) {
        this.id = id;
        this.seccionId = seccionId;
        this.seccionNombre = seccionNombre;
        this.descripcion = descripcion;
        this.integridad = integridad;
        this.estadoIa = estadoIa;
        this.tokenQr = tokenQr;
        this.disponible = disponible;
        this.activo = activo;
        this.valorEconomico = valorEconomico;
        this.fechaCreacion = fechaCreacion;
        this.fechaBaja = fechaBaja;
        this.motivoBaja = motivoBaja;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public double getValorEconomico() {
        return valorEconomico;
    }

    public void setValorEconomico(double valorEconomico) {
        this.valorEconomico = valorEconomico;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(String fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getMotivoBaja() {
        return motivoBaja;
    }

    public void setMotivoBaja(String motivoBaja) {
        this.motivoBaja = motivoBaja;
    }
}
