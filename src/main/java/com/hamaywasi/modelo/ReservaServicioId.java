package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// clave compuesta servicio reserva
@Embeddable
public class ReservaServicioId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "reserva_id")
    private Long reservaId;

    @Column(name = "servicio_id")
    private Long servicioId;

    public ReservaServicioId() {
    }

    public ReservaServicioId(Long reservaId, Long servicioId) {
        this.reservaId = reservaId;
        this.servicioId = servicioId;
    }

    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId = servicioId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservaServicioId otra)) {
            return false;
        }
        return Objects.equals(reservaId, otra.reservaId)
                && Objects.equals(servicioId, otra.servicioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservaId, servicioId);
    }

    @Override
    public String toString() {
        return "ReservaServicioId[" + reservaId + "/" + servicioId + "]";
    }
}
