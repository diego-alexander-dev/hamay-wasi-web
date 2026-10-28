package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

// tabla de auditoria solo lectura
@Entity
@Immutable
@Table(name = "auditoria_reservas")
public class AuditoriaReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;

    @Column(name = "accion", nullable = false, length = 20)
    private String accion;

    @Column(name = "detalle", length = 300)
    private String detalle;

    @Column(name = "usuario_bd", nullable = false, length = 80, insertable = false, updatable = false)
    private String usuarioBd;

    @Column(name = "fecha", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fecha;

    public String getFechaFmt() {
        return fecha == null ? "" : fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public Long getId() { return id; }

    public Long getReservaId() { return reservaId; }

    public String getAccion() { return accion; }

    public String getDetalle() { return detalle; }

    public String getUsuarioBd() { return usuarioBd; }

    public LocalDateTime getFecha() { return fecha; }
}
