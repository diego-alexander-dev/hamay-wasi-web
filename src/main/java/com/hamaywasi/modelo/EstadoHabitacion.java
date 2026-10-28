package com.hamaywasi.modelo;

// estado de la habitacion
public enum EstadoHabitacion {

    DISPONIBLE("Disponible"),
    OCUPADA("Ocupada"),
    LIMPIEZA("Limpieza"),
    MANTENIMIENTO("Mantenimiento");

    private final String etiqueta;

    EstadoHabitacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
