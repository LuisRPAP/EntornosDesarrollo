package com.renacegest.model;

import java.util.ArrayList;
import java.util.List;

public class FotoPublica {
    private Long id;
    private String titulo;
    private String descripcion;
    private String lugarEvento;
    private String fechaEvento;
    private String urlImagen;
    private final List<EtiquetaPersonaPublica> etiquetas = new ArrayList<>();
    private final List<ValoracionFotoPublica> valoraciones = new ArrayList<>();

    public FotoPublica(Long id, String titulo, String descripcion, String lugarEvento, String fechaEvento, String urlImagen) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugarEvento = lugarEvento;
        this.fechaEvento = fechaEvento;
        this.urlImagen = urlImagen;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getLugarEvento() {
        return lugarEvento;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public List<EtiquetaPersonaPublica> getEtiquetas() {
        return etiquetas;
    }

    public List<ValoracionFotoPublica> getValoraciones() {
        return valoraciones;
    }

    public void addEtiqueta(EtiquetaPersonaPublica etiqueta) {
        etiquetas.add(0, etiqueta);
    }

    public void addValoracion(ValoracionFotoPublica valoracion) {
        valoraciones.add(0, valoracion);
    }

    public double getMediaValoracion() {
        if (valoraciones.isEmpty()) {
            return 0;
        }

        int suma = 0;
        for (ValoracionFotoPublica valoracion : valoraciones) {
            suma += valoracion.getPuntuacion();
        }
        return (double) suma / valoraciones.size();
    }
}
