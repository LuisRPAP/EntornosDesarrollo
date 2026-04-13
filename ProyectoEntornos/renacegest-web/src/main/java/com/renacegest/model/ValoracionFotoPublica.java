package com.renacegest.model;

public class ValoracionFotoPublica {
    private Long id;
    private Long fotoId;
    private int puntuacion;
    private String comentario;
    private String visitante;
    private String usuarioRed;
    private String fechaValoracion;

    public ValoracionFotoPublica(Long id, Long fotoId, int puntuacion, String comentario, String visitante, String usuarioRed, String fechaValoracion) {
        this.id = id;
        this.fotoId = fotoId;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.visitante = visitante;
        this.usuarioRed = usuarioRed;
        this.fechaValoracion = fechaValoracion;
    }

    public Long getId() {
        return id;
    }

    public Long getFotoId() {
        return fotoId;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public String getVisitante() {
        return visitante;
    }

    public String getUsuarioRed() {
        return usuarioRed;
    }

    public String getFechaValoracion() {
        return fechaValoracion;
    }
}
