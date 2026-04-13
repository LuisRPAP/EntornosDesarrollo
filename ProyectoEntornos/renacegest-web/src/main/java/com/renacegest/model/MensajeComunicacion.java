package com.renacegest.model;

public class MensajeComunicacion {
    private Long id;
    private Long emisorId;
    private String emisorApodo;
    private Long grupoId;
    private String grupoNombre;
    private String contenido;
    private boolean broadcast;
    private boolean visibleParaTodos;
    private String fechaEnvio;

    public MensajeComunicacion() {
    }

    public MensajeComunicacion(Long id, Long emisorId, String emisorApodo, Long grupoId, String grupoNombre, String contenido, boolean broadcast, boolean visibleParaTodos, String fechaEnvio) {
        this.id = id;
        this.emisorId = emisorId;
        this.emisorApodo = emisorApodo;
        this.grupoId = grupoId;
        this.grupoNombre = grupoNombre;
        this.contenido = contenido;
        this.broadcast = broadcast;
        this.visibleParaTodos = visibleParaTodos;
        this.fechaEnvio = fechaEnvio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmisorId() {
        return emisorId;
    }

    public void setEmisorId(Long emisorId) {
        this.emisorId = emisorId;
    }

    public String getEmisorApodo() {
        return emisorApodo;
    }

    public void setEmisorApodo(String emisorApodo) {
        this.emisorApodo = emisorApodo;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getGrupoNombre() {
        return grupoNombre;
    }

    public void setGrupoNombre(String grupoNombre) {
        this.grupoNombre = grupoNombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public boolean isVisibleParaTodos() {
        return visibleParaTodos;
    }

    public void setVisibleParaTodos(boolean visibleParaTodos) {
        this.visibleParaTodos = visibleParaTodos;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
}
