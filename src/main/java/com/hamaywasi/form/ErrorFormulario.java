package com.hamaywasi.form;

// excepcion de validacion para formularios
public class ErrorFormulario extends RuntimeException {

    private final String campo;

    public ErrorFormulario(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
