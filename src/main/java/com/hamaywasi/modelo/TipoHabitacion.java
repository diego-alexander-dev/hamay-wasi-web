package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;

// catalogo de tipos de habitacion
@Entity
@Table(name = "tipos_habitacion")
public class TipoHabitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120, unique = true)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tamano_m2", nullable = false)
    private Short tamanoM2;

    @Column(name = "cama", nullable = false, length = 60)
    private String cama;

    @Column(name = "capacidad", nullable = false)
    private Short capacidad;

    @Column(name = "precio_noche", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNoche;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;

    // texto limpio para comparar nombres
    public String getSlug() {
        if (nombre == null) {
            return "";
        }
        String base = Normalizer.normalize(nombre, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return base.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    public String getPrecioFmt() {
        return precioNoche == null ? "" : String.format(Locale.forLanguageTag("es-PE"), "%.2f", precioNoche);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Short getTamanoM2() { return tamanoM2; }
    public void setTamanoM2(Short tamanoM2) { this.tamanoM2 = tamanoM2; }

    public String getCama() { return cama; }
    public void setCama(String cama) { this.cama = cama; }

    public Short getCapacidad() { return capacidad; }
    public void setCapacidad(Short capacidad) { this.capacidad = capacidad; }

    public BigDecimal getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(BigDecimal precioNoche) { this.precioNoche = precioNoche; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public boolean isActivo() { return Boolean.TRUE.equals(activo); }
}
