package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// datos de los huespedes
@Entity
@Table(name = "huespedes")
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 80)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 80)
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;

    @Column(name = "numero_documento", nullable = false, length = 20)
    private String numeroDocumento;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "pais", length = 60)
    private String pais = "Perú";

    @Column(name = "creado_en", nullable = false, insertable = false, updatable = false)
    private LocalDateTime creadoEn;

    public String getNombreCompleto() {
        return (nombres == null ? "" : nombres) + " " + (apellidos == null ? "" : apellidos);
    }

    // iniciales para el avatar
    public String getIniciales() {
        String a = nombres == null || nombres.isBlank() ? "" : nombres.strip().substring(0, 1);
        String b = apellidos == null || apellidos.isBlank() ? "" : apellidos.strip().substring(0, 1);
        return (a + b).toUpperCase(java.util.Locale.ROOT);
    }

    // oculta parte del correo en vistas publicas
    public String getEmailEnmascarado() {
        return Privacidad.enmascaraCorreo(email);
    }

    // oculta parte del documento
    public String getDocumentoEnmascarado() {
        return Privacidad.enmascaraDocumento(numeroDocumento);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }
}
