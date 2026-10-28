package com.hamaywasi.modelo;

import java.math.BigDecimal;

// proyeccion para habitaciones disponibles
public interface HabitacionDisponible {

    Long getHabId();

    String getHabNumero();

    Short getHabPiso();

    String getTipoNombre();

    BigDecimal getPrecio();

    default String getPrecioFmt() {
        return getPrecio() == null ? "0.00" : String.format(java.util.Locale.forLanguageTag("es-PE"), "%.2f", getPrecio());
    }
}
