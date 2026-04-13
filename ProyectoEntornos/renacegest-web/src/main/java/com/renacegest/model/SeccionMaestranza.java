package com.renacegest.model;

public class SeccionMaestranza {
    private Long id;
    private String nombreSeccion;
    private Long responsableId;
    private String responsableApodo;

    public SeccionMaestranza() {
    }

    public SeccionMaestranza(Long id, String nombreSeccion, Long responsableId, String responsableApodo) {
        this.id = id;
        this.nombreSeccion = nombreSeccion;
        this.responsableId = responsableId;
        this.responsableApodo = responsableApodo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion = nombreSeccion;
    }

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }

    public String getResponsableApodo() {
        return responsableApodo;
    }

    public void setResponsableApodo(String responsableApodo) {
        this.responsableApodo = responsableApodo;
    }
}
