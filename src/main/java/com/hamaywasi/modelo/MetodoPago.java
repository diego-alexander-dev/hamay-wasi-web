package com.hamaywasi.modelo;

// metodos de pago del hotel
public enum MetodoPago {

    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    YAPE("Yape"),
    PLIN("Plin"),
    TRANSFERENCIA("Transferencia bancaria");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
