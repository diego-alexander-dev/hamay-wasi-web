package com.hamaywasi.dto.notificacion;

import java.time.LocalDateTime;

// respuesta del estado del envio
public record NotificacionResponse(
        boolean exitoso,
        String canal,
        String destinatario,
        String idMensaje,
        String detalle,
        LocalDateTime fechaEnvio
) {
    public static NotificacionResponse exito(String canal, String destinatario, String idMensaje, String detalle) {
        return new NotificacionResponse(true, canal, destinatario, idMensaje, detalle, LocalDateTime.now());
    }

    public static NotificacionResponse fallo(String canal, String destinatario, String detalle) {
        return new NotificacionResponse(false, canal, destinatario, null, detalle, LocalDateTime.now());
    }
}
