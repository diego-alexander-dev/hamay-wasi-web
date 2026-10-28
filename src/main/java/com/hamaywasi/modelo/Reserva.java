package com.hamaywasi.modelo;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// entidad de la reserva
@Entity
@Table(name = "reservas")
public class Reserva {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale LOCALE_PE = Locale.forLanguageTag("es-PE");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 20, unique = true)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "huesped_id", nullable = false)
    private Huesped huesped;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promocion_id")
    private Promocion promocion;

    @Column(name = "fecha_entrada", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "num_huespedes", nullable = false)
    private Short numHuespedes = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 15)
    private EstadoReserva estado = EstadoReserva.CONFIRMADA;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // total calculado en base de datos
    @Column(name = "total", nullable = false, precision = 12, scale = 2,
            insertable = false, updatable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false, insertable = false, updatable = false)
    private LocalDateTime actualizadoEn;

    // carga las listas en una sola consulta
    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<ReservaHabitacion> habitaciones = new ArrayList<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<ReservaServicio> servicios = new ArrayList<>();

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Pago> pagos = new ArrayList<>();

    // datos calculados para la vista

    public int getNoches() {
        if (fechaEntrada == null || fechaSalida == null) {
            return 0;
        }
        long n = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        return (int) Math.max(n, 0);
    }

    public String getEntradaFmt() {
        return fechaEntrada == null ? "" : fechaEntrada.format(FMT);
    }

    public String getSalidaFmt() {
        return fechaSalida == null ? "" : fechaSalida.format(FMT);
    }

    public String getTotalFmt() {
        return total == null ? "0.00" : String.format(LOCALE_PE, "%.2f", total);
    }

    public String getTotalPagadoFmt() {
        return String.format(LOCALE_PE, "%.2f", getTotalPagado());
    }

    public String getSaldoFmt() {
        return String.format(LOCALE_PE, "%.2f", getSaldo());
    }

    // lista de numeros de habitacion
    public String getNumerosHabitaciones() {
        return habitaciones.stream()
                .map(rh -> rh.getHabitacion() == null ? "" : rh.getHabitacion().getNumero())
                .filter(n -> !n.isBlank())
                .sorted()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
    }

    public String getNombreHuesped() {
        return huesped == null ? "" : huesped.getNombreCompleto();
    }

    public BigDecimal getTotalPagado() {
        return pagos.stream()
                .filter(p -> p.getEstado() == EstadoPago.PAGADO)
                .map(p -> p.getMonto() == null ? BigDecimal.ZERO : p.getMonto())
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public BigDecimal getSaldo() {
        BigDecimal t = total == null ? BigDecimal.ZERO : total;
        return t.subtract(getTotalPagado());
    }

    public void agregarHabitacion(ReservaHabitacion linea) {
        linea.setReserva(this);
        this.habitaciones.add(linea);
    }

    public void agregarServicio(ReservaServicio linea) {
        linea.setReserva(this);
        this.servicios.add(linea);
    }

    public void agregarPago(Pago pago) {
        pago.setReserva(this);
        this.pagos.add(pago);
    }

    // getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Huesped getHuesped() { return huesped; }
    public void setHuesped(Huesped huesped) { this.huesped = huesped; }

    public Promocion getPromocion() { return promocion; }
    public void setPromocion(Promocion promocion) { this.promocion = promocion; }

    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public Short getNumHuespedes() { return numHuespedes; }
    public void setNumHuespedes(Short numHuespedes) { this.numHuespedes = numHuespedes; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public BigDecimal getTotal() { return total; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }

    public List<ReservaHabitacion> getHabitaciones() { return habitaciones; }
    public void setHabitaciones(List<ReservaHabitacion> habitaciones) { this.habitaciones = habitaciones; }

    public List<ReservaServicio> getServicios() { return servicios; }
    public void setServicios(List<ReservaServicio> servicios) { this.servicios = servicios; }

    public List<Pago> getPagos() { return pagos; }
    public void setPagos(List<Pago> pagos) { this.pagos = pagos; }
}
