package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// vista de reservas con totales y saldo
@Entity
@Immutable
@Table(name = "v_reservas_detalle")
public class ReservaDetalle {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo", length = 20)
    private String codigo;

    @Column(name = "huesped", columnDefinition = "TEXT")
    private String huesped;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "fecha_entrada")
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida")
    private LocalDate fechaSalida;

    @Column(name = "noches")
    private Integer noches;

    @Column(name = "num_huespedes")
    private Short numHuespedes;

    @Column(name = "estado", length = 15)
    private String estado;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "pagado", precision = 12, scale = 2)
    private BigDecimal pagado;

    @Column(name = "saldo", precision = 12, scale = 2)
    private BigDecimal saldo;

    @Column(name = "habitaciones", columnDefinition = "TEXT")
    private String habitaciones;

    @Column(name = "tipos", columnDefinition = "TEXT")
    private String tipos;

    public boolean isPagada() {
        return saldo != null && saldo.signum() <= 0;
    }

    // estado como tipo enum
    public EstadoReserva getEstadoEnum() {
        if (estado == null) {
            return null;
        }
        try {
            return EstadoReserva.valueOf(estado);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getEstadoEtiqueta() {
        EstadoReserva e = getEstadoEnum();
        return e == null ? estado : e.getEtiqueta();
    }

    public String getEntradaFmt() {
        return fechaEntrada == null ? "" : fechaEntrada.format(FMT);
    }

    public String getSalidaFmt() {
        return fechaSalida == null ? "" : fechaSalida.format(FMT);
    }

    public String getTotalFmt() { return fmt(total); }
    public String getPagadoFmt() { return fmt(pagado); }
    public String getSaldoFmt() { return fmt(saldo); }

    private String fmt(BigDecimal valor) {
        return valor == null ? "0.00" : String.format(Locale.forLanguageTag("es-PE"), "%.2f", valor);
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getHuesped() { return huesped; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public Integer getNoches() { return noches; }
    public Short getNumHuespedes() { return numHuespedes; }
    public String getEstado() { return estado; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getPagado() { return pagado; }
    public BigDecimal getSaldo() { return saldo; }
    public String getHabitaciones() { return habitaciones; }
    public String getTipos() { return tipos; }
}
