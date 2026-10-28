package com.hamaywasi.modelo;

// estados de la reserva
public enum EstadoReserva {

    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    CHECK_IN("Check-in"),
    CHECK_OUT("Check-out"),
    CANCELADA("Cancelada");

    private final String etiqueta;

    EstadoReserva(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // valida si puede cambiar a otro estado
    public boolean puedeIrA(EstadoReserva destino) {
        if (destino == null || destino == this) {
            return false;
        }
        return switch (this) {
            case PENDIENTE -> destino == CONFIRMADA || destino == CANCELADA;
            case CONFIRMADA -> destino == CHECK_IN || destino == CANCELADA;
            case CHECK_IN -> destino == CHECK_OUT;
            case CHECK_OUT, CANCELADA -> false;
        };
    }

    // indica si ocupa habitacion
    public boolean ocupa() {
        return this == PENDIENTE || this == CONFIRMADA || this == CHECK_IN;
    }
}
