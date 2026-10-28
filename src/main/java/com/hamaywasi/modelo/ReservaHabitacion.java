package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.Objects;

// habitacion asignada a una reserva
@Entity
@Table(name = "reserva_habitaciones")
public class ReservaHabitacion {

    // clave compuesta para la relacion
    @EmbeddedId
    private ReservaHabitacionId id = new ReservaHabitacionId();

    @MapsId("reservaId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @MapsId("habitacionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    @Column(name = "precio_noche", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    public ReservaHabitacion() {
    }

    public static ReservaHabitacion de(Reserva reserva, Habitacion habitacion, BigDecimal precioNoche) {
        ReservaHabitacion linea = new ReservaHabitacion();
        linea.setReserva(reserva);
        linea.setHabitacion(habitacion);
        linea.setPrecioNoche(precioNoche);
        return linea;
    }

    public String getNumero() {
        return habitacion == null ? "" : habitacion.getNumero();
    }

    public String getNombreTipo() {
        return habitacion == null || habitacion.getTipo() == null ? "" : habitacion.getTipo().getNombre();
    }

    public BigDecimal getPrecioNocheFmt() {
        return precioNoche == null ? BigDecimal.ZERO : precioNoche;
    }

    public ReservaHabitacionId getId() { return id; }
    public void setId(ReservaHabitacionId id) { this.id = id; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }

    public BigDecimal getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(BigDecimal precioNoche) { this.precioNoche = precioNoche; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservaHabitacion otra)) {
            return false;
        }
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
