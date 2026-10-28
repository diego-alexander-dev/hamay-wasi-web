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

// servicios adicionales de la reserva
@Entity
@Table(name = "reserva_servicios")
public class ReservaServicio {

    // clave compuesta de la asociacion
    @EmbeddedId
    private ReservaServicioId id = new ReservaServicioId();

    @MapsId("reservaId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @MapsId("servicioId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicio servicio;

    @Column(name = "cantidad", nullable = false)
    private Short cantidad = 1;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    public ReservaServicio() {
    }

    public static ReservaServicio de(Reserva reserva, Servicio servicio, Short cantidad, BigDecimal precioUnitario) {
        ReservaServicio linea = new ReservaServicio();
        linea.setReserva(reserva);
        linea.setServicio(servicio);
        linea.setCantidad(cantidad);
        linea.setPrecioUnitario(precioUnitario);
        return linea;
    }

    public String getNombre() {
        return servicio == null ? "" : servicio.getNombre();
    }

    public BigDecimal getSubtotal() {
        if (precioUnitario == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad == null ? 0 : cantidad));
    }

    // formato con dos decimales
    public String getSubtotalFmt() {
        return getSubtotal().setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    public ReservaServicioId getId() { return id; }
    public void setId(ReservaServicioId id) { this.id = id; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public Short getCantidad() { return cantidad; }
    public void setCantidad(Short cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReservaServicio otra)) {
            return false;
        }
        return Objects.equals(id, otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
