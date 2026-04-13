package com.renacegest.model;

public class EtiquetaPersonaPublica {
    private String nombrePersona;
    private String etiquetadoPor;
    private String usuarioRed;
    private String fechaEtiqueta;

    public EtiquetaPersonaPublica(String nombrePersona, String etiquetadoPor, String usuarioRed, String fechaEtiqueta) {
        this.nombrePersona = nombrePersona;
        this.etiquetadoPor = etiquetadoPor;
        this.usuarioRed = usuarioRed;
        this.fechaEtiqueta = fechaEtiqueta;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public String getEtiquetadoPor() {
        return etiquetadoPor;
    }

    public String getUsuarioRed() {
        return usuarioRed;
    }

    public String getFechaEtiqueta() {
        return fechaEtiqueta;
    }
}
