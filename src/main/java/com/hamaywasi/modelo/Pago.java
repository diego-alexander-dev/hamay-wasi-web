package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// pagos realizados para una reserva
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false, length = 15)
    private MetodoPago metodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "referencia", length = 60)
    private String referencia;

    @Column(name = "pagado_en")
    private LocalDateTime pagadoEn;

    @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    public static Pago de(Reserva reserva, BigDecimal monto, MetodoPago metodo, String referencia) {
        Pago p = new Pago();
        p.setReserva(reserva);
        p.setMonto(monto);
        p.setMetodo(metodo);
        p.setReferencia(referencia);
        p.setEstado(EstadoPago.PAGADO);
        p.setPagadoEn(LocalDateTime.now());
        return p;
    }

    public String getMontoFmt() {
        return monto == null ? "0.00" : String.format(java.util.Locale.forLanguageTag("es-PE"), "%.2f", monto);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public MetodoPago getMetodo() { return metodo; }
    public void setMetodo(MetodoPago metodo) { this.metodo = metodo; }

    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public LocalDateTime getPagadoEn() { return pagadoEn; }
    public void setPagadoEn(LocalDateTime pagadoEn) { this.pagadoEn = pagadoEn; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
}
