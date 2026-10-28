package com.hamaywasi.modelo;

// tipo de documento del huesped
public enum TipoDocumento {

    DNI("DNI"),
    CE("Carné de extranjería"),
    PASAPORTE("Pasaporte");

    private final String etiqueta;

    TipoDocumento(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    // formato de ayuda para el formulario
    public String getFormato() {
        return switch (this) {
            case DNI -> "8 dígitos";
            case CE -> "entre 9 y 12 letras o números";
            case PASAPORTE -> "entre 6 y 12 letras o números";
        };
    }
}
