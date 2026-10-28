package com.hamaywasi.modelo;

// estado del pago
public enum EstadoPago {

    PENDIENTE("Pendiente"),
    PAGADO("Pagado"),
    REEMBOLSADO("Reembolsado");

    private final String etiqueta;

    EstadoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
