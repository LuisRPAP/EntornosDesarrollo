package com.renacegest.model;

public class HistoricoAlarde {
    private Long id;
    private Long guardiaId;
    private String guardiaApodo;
    private Long pertrechoId;
    private String pertrechoDescripcion;
    private Long autorizadorId;
    private String autorizadorApodo;
    private String fechaSalida;
    private String fechaEntrada;
    private String observaciones;
    private boolean ticketMaestranza;
    private int deltaGracia;
    private int integridadSalida;
    private int integridadEntrada;

    public HistoricoAlarde() {
    }

    public HistoricoAlarde(Long id, Long guardiaId, String guardiaApodo, Long pertrechoId, String pertrechoDescripcion,
                           Long autorizadorId, String autorizadorApodo, String fechaSalida, String fechaEntrada,
                           String observaciones, boolean ticketMaestranza, int deltaGracia,
                           int integridadSalida, int integridadEntrada) {
        this.id = id;
        this.guardiaId = guardiaId;
        this.guardiaApodo = guardiaApodo;
        this.pertrechoId = pertrechoId;
        this.pertrechoDescripcion = pertrechoDescripcion;
        this.autorizadorId = autorizadorId;
        this.autorizadorApodo = autorizadorApodo;
        this.fechaSalida = fechaSalida;
        this.fechaEntrada = fechaEntrada;
        this.observaciones = observaciones;
        this.ticketMaestranza = ticketMaestranza;
        this.deltaGracia = deltaGracia;
        this.integridadSalida = integridadSalida;
        this.integridadEntrada = integridadEntrada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuardiaId() {
        return guardiaId;
    }

    public void setGuardiaId(Long guardiaId) {
        this.guardiaId = guardiaId;
    }

    public String getGuardiaApodo() {
        return guardiaApodo;
    }

    public void setGuardiaApodo(String guardiaApodo) {
        this.guardiaApodo = guardiaApodo;
    }

    public Long getPertrechoId() {
        return pertrechoId;
    }

    public void setPertrechoId(Long pertrechoId) {
        this.pertrechoId = pertrechoId;
    }

    public String getPertrechoDescripcion() {
        return pertrechoDescripcion;
    }

    public void setPertrechoDescripcion(String pertrechoDescripcion) {
        this.pertrechoDescripcion = pertrechoDescripcion;
    }

    public Long getAutorizadorId() {
        return autorizadorId;
    }

    public void setAutorizadorId(Long autorizadorId) {
        this.autorizadorId = autorizadorId;
    }

    public String getAutorizadorApodo() {
        return autorizadorApodo;
    }

    public void setAutorizadorApodo(String autorizadorApodo) {
        this.autorizadorApodo = autorizadorApodo;
    }

    public String getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(String fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public String getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public boolean isTicketMaestranza() {
        return ticketMaestranza;
    }

    public void setTicketMaestranza(boolean ticketMaestranza) {
        this.ticketMaestranza = ticketMaestranza;
    }

    public int getDeltaGracia() {
        return deltaGracia;
    }

    public void setDeltaGracia(int deltaGracia) {
        this.deltaGracia = deltaGracia;
    }

    public int getIntegridadSalida() {
        return integridadSalida;
    }

    public void setIntegridadSalida(int integridadSalida) {
        this.integridadSalida = integridadSalida;
    }

    public int getIntegridadEntrada() {
        return integridadEntrada;
    }

    public void setIntegridadEntrada(int integridadEntrada) {
        this.integridadEntrada = integridadEntrada;
    }
}
