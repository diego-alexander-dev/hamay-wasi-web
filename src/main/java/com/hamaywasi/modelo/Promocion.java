package com.hamaywasi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

// promociones y descuentos del hotel
@Entity
@Table(name = "promociones")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 30, unique = true)
    private String codigo;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "porcentaje_descuento", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta", nullable = false)
    private LocalDate vigenteHasta;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;

    public boolean vigenteEn(LocalDate fecha) {
        return Boolean.TRUE.equals(activo) && vigenteDesde != null && vigenteHasta != null
                && !fecha.isBefore(vigenteDesde) && !fecha.isAfter(vigenteHasta);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }

    public LocalDate getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(LocalDate vigenteDesde) { this.vigenteDesde = vigenteDesde; }

    public LocalDate getVigenteHasta() { return vigenteHasta; }
    public void setVigenteHasta(LocalDate vigenteHasta) { this.vigenteHasta = vigenteHasta; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    // formato de descuento sin ceros de mas
    public String getPorcentajeDescuentoFmt() {
        return porcentajeDescuento == null ? "0" : porcentajeDescuento.stripTrailingZeros().toPlainString();
    }
}
