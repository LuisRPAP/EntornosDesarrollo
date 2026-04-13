package com.renacegest.model;

public class MiembroGrupo {
    private Long id;
    private Long grupoId;
    private Long miembroId;
    private String apodo;
    private String nombreReal;
    private String rolEnGrupo;
    private boolean puedeModificarMiembros;
    private String fechaAlta;

    public MiembroGrupo() {
    }

    public MiembroGrupo(Long id, Long grupoId, Long miembroId, String apodo, String nombreReal, String rolEnGrupo, boolean puedeModificarMiembros, String fechaAlta) {
        this.id = id;
        this.grupoId = grupoId;
        this.miembroId = miembroId;
        this.apodo = apodo;
        this.nombreReal = nombreReal;
        this.rolEnGrupo = rolEnGrupo;
        this.puedeModificarMiembros = puedeModificarMiembros;
        this.fechaAlta = fechaAlta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getMiembroId() {
        return miembroId;
    }

    public void setMiembroId(Long miembroId) {
        this.miembroId = miembroId;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getNombreReal() {
        return nombreReal;
    }

    public void setNombreReal(String nombreReal) {
        this.nombreReal = nombreReal;
    }

    public String getRolEnGrupo() {
        return rolEnGrupo;
    }

    public void setRolEnGrupo(String rolEnGrupo) {
        this.rolEnGrupo = rolEnGrupo;
    }

    public boolean isPuedeModificarMiembros() {
        return puedeModificarMiembros;
    }

    public void setPuedeModificarMiembros(boolean puedeModificarMiembros) {
        this.puedeModificarMiembros = puedeModificarMiembros;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}
