package com.hamaywasi.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// formulario de contacto del sitio
public class ContactoForm {

    @NotBlank(message = "Escribe tu nombre")
    @Size(max = 80, message = "El nombre admite hasta 80 caracteres")
    private String nombre;

    @NotBlank(message = "Escribe tus apellidos")
    @Size(max = 80, message = "Los apellidos admiten hasta 80 caracteres")
    private String apellidos;

    @NotBlank(message = "Escribe tu correo")
    @Email(message = "Revisa el correo: le falta el @ o el dominio")
    @Size(max = 120, message = "El correo admite hasta 120 caracteres")
    private String email;

    @Pattern(regexp = "^$|^[0-9+()\\s.-]{6,30}$",
            message = "El teléfono solo admite números, espacios, + y guiones")
    private String telefono;

    @NotBlank(message = "Escribe tu consulta")
    @Size(min = 10, message = "Cuéntanos un poco más: al menos 10 caracteres")
    @Size(max = 2000, message = "La consulta admite hasta 2000 caracteres")
    private String mensaje;

    // campo trampa antispam
    private String sitioTrampa;

    public boolean esTrampa() {
        return sitioTrampa != null && !sitioTrampa.isBlank();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getSitioTrampa() { return sitioTrampa; }
    public void setSitioTrampa(String sitioTrampa) { this.sitioTrampa = sitioTrampa; }
}
