package com.renacegest.model;

public class GrupoMision {
    private Long id;
    private String nombreGrupo;
    private String descripcion;
    private String tipo;
    private String jefeEquipo;
    private String creadoPor;
    private boolean activo;

    public GrupoMision() {
    }

    public GrupoMision(Long id, String nombreGrupo, String descripcion, String tipo, String jefeEquipo, String creadoPor, boolean activo) {
        this.id = id;
        this.nombreGrupo = nombreGrupo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.jefeEquipo = jefeEquipo;
        this.creadoPor = creadoPor;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getJefeEquipo() {
        return jefeEquipo;
    }

    public void setJefeEquipo(String jefeEquipo) {
        this.jefeEquipo = jefeEquipo;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
