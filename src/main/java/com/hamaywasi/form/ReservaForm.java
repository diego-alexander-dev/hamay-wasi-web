package com.hamaywasi.form;

import com.hamaywasi.modelo.TipoDocumento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// datos del formulario de reserva
@DocumentoValido
@FechasCoherentes
public class ReservaForm {

    @NotBlank(message = "Escribe tus nombres")
    @Size(max = 80, message = "Los nombres admiten hasta 80 caracteres")
    private String nombres;

    @NotBlank(message = "Escribe tus apellidos")
    @Size(max = 80, message = "Los apellidos admiten hasta 80 caracteres")
    private String apellidos;

    @NotNull(message = "Elige el tipo de documento")
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;

    @NotBlank(message = "Escribe el número de documento")
    @Pattern(regexp = "^[A-Za-z0-9]+$",
            message = "El número de documento solo admite letras y números, sin espacios ni guiones")
    @Size(max = 20, message = "El número de documento admite hasta 20 caracteres")
    private String numeroDocumento;

    @NotBlank(message = "Escribe tu correo")
    @Email(message = "Revisa el correo: le falta el @ o el dominio")
    @Size(max = 120, message = "El correo admite hasta 120 caracteres")
    private String email;

    @Pattern(regexp = "^$|^[0-9+()\\s.-]{6,30}$",
            message = "El teléfono solo admite números, espacios, + y guiones")
    private String telefono;

    @NotBlank(message = "Escribe tu país")
    @Size(max = 60, message = "El país admite hasta 60 caracteres")
    private String pais = "Perú";

    @NotNull(message = "Elige la fecha de entrada")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaEntrada;

    @NotNull(message = "Elige la fecha de salida")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaSalida;

    @NotNull(message = "Indica cuántos huespedes son")
    @Min(value = 1, message = "Debe haber al menos 1 huésped")
    @Max(value = 20, message = "Aceptamos hasta 20 huéspedes por reserva")
    private Integer numHuespedes = 1;

    @NotNull(message = "Elige cuántas habitaciones quieres")
    @Min(value = 1, message = "Reserva al menos 1 habitación")
    @Max(value = 5, message = "Reserva hasta 5 habitaciones por formulario")
    private Integer numHabitaciones = 1;

    @NotNull(message = "Elige el tipo de habitación")
    private Long tipoHabitacionId;

    @Size(max = 30, message = "El código de promoción admite hasta 30 caracteres")
    private String codigoPromo;

    @Size(max = 500, message = "Las observaciones admiten hasta 500 caracteres")
    private String observaciones;

    // lista de servicios opcionales
    private List<LineaServicio> servicios = new ArrayList<>();

    // calcula noches entre fechas
    public int getNoches() {
        if (fechaEntrada == null || fechaSalida == null) {
            return 0;
        }
        long n = java.time.temporal.ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        return (int) Math.max(n, 0);
    }

    public String getEntradaFmt() {
        return fechaEntrada == null ? "" : fechaEntrada.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getSalidaFmt() {
        return fechaSalida == null ? "" : fechaSalida.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getTipoDocumentoEtiqueta() {
        return tipoDocumento == null ? "" : tipoDocumento.getEtiqueta();
    }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public Integer getNumHuespedes() { return numHuespedes; }
    public void setNumHuespedes(Integer numHuespedes) { this.numHuespedes = numHuespedes; }

    public Integer getNumHabitaciones() { return numHabitaciones; }
    public void setNumHabitaciones(Integer numHabitaciones) { this.numHabitaciones = numHabitaciones; }

    public Long getTipoHabitacionId() { return tipoHabitacionId; }
    public void setTipoHabitacionId(Long tipoHabitacionId) { this.tipoHabitacionId = tipoHabitacionId; }

    public String getCodigoPromo() { return codigoPromo; }
    public void setCodigoPromo(String codigoPromo) { this.codigoPromo = codigoPromo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<LineaServicio> getServicios() { return servicios; }
    public void setServicios(List<LineaServicio> servicios) {
        this.servicios = servicios == null ? new ArrayList<>() : servicios;
    }

    // inicializa lineas para los servicios extras
    public void alineaExtras(int cuantosExtras) {
        if (servicios == null) {
            servicios = new ArrayList<>();
        }
        if (!servicios.isEmpty()) {
            return;
        }
        for (int i = 0; i < cuantosExtras; i++) {
            LineaServicio linea = new LineaServicio();
            linea.setCantidad(1);
            servicios.add(linea);
        }
    }

    // linea de servicio extra seleccionado
    public static class LineaServicio {

        private Long servicioId;

        // indica si fue marcado por el huesped
        private boolean seleccionado;

        @Min(value = 0, message = "La cantidad no puede ser negativa")
        @Max(value = 99, message = "Máximo 99 unidades por servicio")
        private Integer cantidad = 0;

        public Long getServicioId() { return servicioId; }
        public void setServicioId(Long servicioId) { this.servicioId = servicioId; }

        public boolean isSeleccionado() { return seleccionado; }
        public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
