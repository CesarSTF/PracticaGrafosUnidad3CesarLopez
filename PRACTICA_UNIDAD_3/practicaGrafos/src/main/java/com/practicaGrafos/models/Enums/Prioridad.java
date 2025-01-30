package com.practicaGrafos.models.Enums;

public enum Prioridad {
    ALTA("ALTA"),
    MEDIA("MEDIA"),
    BAJA("BAJA");

    private String prioridad;

    private Prioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getPrioridad() {
        return this.prioridad;
    }
}
