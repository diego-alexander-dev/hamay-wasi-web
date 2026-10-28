package com.hamaywasi.servicio.notificacion;

import com.hamaywasi.evento.ReservaConfirmadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

// escucha el evento para enviar el whatsapp en segundo plano
@Component
public class NotificacionEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificacionEventListener.class);

    private final WhatsAppService whatsAppService;

    public NotificacionEventListener(WhatsAppService whatsAppService) {
        this.whatsAppService = whatsAppService;
    }

    // se ejecuta sin bloquear al usuario
    @Async
    @EventListener
    public void manejarReservaConfirmada(ReservaConfirmadaEvent evento) {
        log.info("Evento ReservaConfirmadaEvent recibido en segundo plano en hilo [{}] para reserva {}",
                Thread.currentThread().getName(), evento.reserva().getCodigo());
        try {
            whatsAppService.enviarConfirmacionReserva(evento.reserva());
        } catch (Exception e) {
            log.error("error al enviar mensaje: {}", e.getMessage());
        }
    }
}