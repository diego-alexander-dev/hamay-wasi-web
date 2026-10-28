package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// clave compuesta habitacion reserva
@Embeddable
public class ReservaHabitacionId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "reserva_id")
    private Long reservaId;

    @Column(name = "habitacion_id")
    private Long habitacionId;

    public ReservaHabitacionId() {
    }

    public ReservaHabitacionId(Long reservaId, Long habitacionId) {
        this.reservaId = reservaId;
        this.habitacionId = habitacionId;
    }

    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }

    public Long getHabitacionId() { return habitacionId; }
    public void setHabitacionId(Long habitacionId) { this.habitacionId = habitacionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservaHabitacionId otra)) {
            return false;
        }
        return Objects.equals(reservaId, otra.reservaId)
                && Objects.equals(habitacionId, otra.habitacionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservaId, habitacionId);
    }

    @Override
    public String toString() {
        return "ReservaHabitacionId[" + reservaId + "/" + habitacionId + "]";
    }
}
