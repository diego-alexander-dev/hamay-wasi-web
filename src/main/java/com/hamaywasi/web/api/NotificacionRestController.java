package com.hamaywasi.web.api;

import com.hamaywasi.dto.notificacion.NotificacionRequest;
import com.hamaywasi.dto.notificacion.NotificacionResponse;
import com.hamaywasi.servicio.notificacion.WhatsAppService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// controlador rest para probar y enviar notificaciones
@RestController
@RequestMapping("/api/notifications")
public class NotificacionRestController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionRestController.class);

    private final WhatsAppService whatsAppService;

    public NotificacionRestController(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    // endpoint post para envio manual o automatico
    @PostMapping("/send")
    public ResponseEntity<NotificacionResponse> enviarNotificacion(
            @Valid @RequestBody NotificacionRequest solicitud) {

        String canal = solicitud.canal().trim().toUpperCase();
        log.info("solicitud de notificacion canal={} destinatario={}", canal, solicitud.destinatario());

        if ("WHATSAPP".equals(canal)) {
            String cuerpo = solicitud.mensaje();
            if (cuerpo == null || cuerpo.isBlank()) {
                cuerpo = "Hotel Hamay Wasi: Notificacion de " + solicitud.tipo();
            }
            NotificacionResponse respuesta = whatsAppService.enviarMensajeTexto(solicitud.destinatario(), cuerpo);
            return ResponseEntity.ok(respuesta);
        }

        if ("EMAIL".equals(canal)) {
            return ResponseEntity.ok(NotificacionResponse.exito("EMAIL", solicitud.destinatario(),
                    "EMAIL-PENDING", "pendiente de envio"));
        }

        if ("SMS".equals(canal)) {
            return ResponseEntity.ok(NotificacionResponse.exito("SMS", solicitud.destinatario(),
                    "SMS-PENDING", "pendiente de envio"));
        }

        return ResponseEntity.badRequest().body(
                NotificacionResponse.fallo(canal, solicitud.destinatario(), "canal no soportado")
        );
    }
}
