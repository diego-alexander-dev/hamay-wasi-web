package com.hamaywasi.modelo;

import java.util.Locale;

// oculta datos personales
public final class Privacidad {

    private Privacidad() {
    }

    // enmascara el correo
    public static String enmascaraCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return "";
        }
        int arroba = correo.indexOf('@');
        if (arroba < 1) {
            return "***";
        }
        String local = correo.substring(0, arroba);
        String dominio = correo.substring(arroba);
        String visible = local.substring(0, Math.min(2, local.length()));
        return visible + "***" + dominio;
    }

    // enmascara el documento
    public static String enmascaraDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            return "";
        }
        String d = documento.trim();
        if (d.length() <= 4) {
            return "****";
        }
        return d.substring(0, 2) + "*".repeat(Math.min(6, d.length() - 4)) + d.substring(d.length() - 2);
    }

    // deja solo numeros para telefonos
    public static String soloDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    public static String nombreTipoDocumento(TipoDocumento tipo) {
        return tipo == null ? "" : tipo.name();
    }

    public static String primerNombre(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            return "";
        }
        return nombres.strip().split("\\s+")[0];
    }

    public static String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        String t = texto.strip();
        return t.substring(0, 1).toUpperCase(Locale.ROOT) + t.substring(1);
    }
}
