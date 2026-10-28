package com.hamaywasi.dto.notificacion;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

// datos de entrada para enviar notificaciones
public record NotificacionRequest(
        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario,

        @NotBlank(message = "El canal es obligatorio (WHATSAPP, EMAIL, SMS)")
        String canal,

        @NotBlank(message = "El tipo es obligatorio")
        String tipo,

        String mensaje,

        Map<String, String> parametros
) {
}
