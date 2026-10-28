package com.hamaywasi.servicio.notificacion;

import com.hamaywasi.dto.notificacion.NotificacionResponse;
import com.hamaywasi.modelo.Reserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

// servicio para enviar mensajes por whatsapp
@Service
public class WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);

    private final RestClient restClient;
    private final boolean habilitado;
    private final String phoneNumberId;
    private final String accessToken;
    private final String apiUrl;

    public WhatsAppService(
            @Value("${whatsapp.api.enabled:true}") boolean habilitado,
            @Value("${whatsapp.api.phone-number-id:100609346426371}") String phoneNumberId,
            @Value("${whatsapp.api.access-token:SANDBOX_TOKEN}") String accessToken,
            @Value("${whatsapp.api.url:https://graph.facebook.com/v20.0}") String apiUrl) {
        this.habilitado = habilitado;
        this.phoneNumberId = phoneNumberId;
        this.accessToken = accessToken;
        this.apiUrl = apiUrl;
        this.restClient = RestClient.builder().build();
    }

    // envia la confirmacion al numero del huesped
    public NotificacionResponse enviarConfirmacionReserva(Reserva reserva) {
        if (reserva == null || reserva.getHuesped() == null) {
            log.warn("reserva o huesped vacio");
            return NotificacionResponse.fallo("WHATSAPP", "N/A", "datos incompletos");
        }

        String telefono = reserva.getHuesped().getTelefono();
        if (telefono == null || telefono.isBlank()) {
            log.info("la reserva {} no tiene telefono", reserva.getCodigo());
            return NotificacionResponse.fallo("WHATSAPP", "SIN_NUMERO", "sin telefono registrado");
        }

        String mensaje = construirMensajeConfirmacion(reserva);
        return enviarMensajeTexto(telefono, mensaje);
    }

    // envio del texto por api
    public NotificacionResponse enviarMensajeTexto(String telefonoDestino, String mensaje) {
        if (!habilitado) {
            log.info("whatsapp apagado");
            return NotificacionResponse.fallo("WHATSAPP", telefonoDestino, "servicio desactivado");
        }

        String numeroNormalizado = normalizarTelefonoPeru(telefonoDestino);
        if (numeroNormalizado.isBlank()) {
            log.warn("numero invalido: {}", telefonoDestino);
            return NotificacionResponse.fallo("WHATSAPP", telefonoDestino, "numero invalido");
        }

        // modo prueba local sin token
        if (accessToken == null || accessToken.isBlank() || "SANDBOX_TOKEN".equalsIgnoreCase(accessToken)) {
            String simId = "WA-PRUEBA-" + UUID.randomUUID().toString().substring(0, 8);
            log.info("[whatsapp prueba] mensaje simulado a +{}: id={}", numeroNormalizado, simId);
            log.info("contenido:\n{}", mensaje);
            return NotificacionResponse.exito("WHATSAPP", numeroNormalizado, simId, "mensaje simulado en prueba");
        }

        // modo real con token de meta
        try {
            Map<String, Object> payload = Map.of(
                    "messaging_product", "whatsapp",
                    "recipient_type", "individual",
                    "to", numeroNormalizado,
                    "type", "text",
                    "text", Map.of("preview_url", false, "body", mensaje)
            );

            String endpoint = apiUrl + "/" + phoneNumberId + "/messages";

            String respuesta = restClient.post()
                    .uri(endpoint)
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            log.info("whatsapp enviado a +{}: {}", numeroNormalizado, respuesta);
            return NotificacionResponse.exito("WHATSAPP", numeroNormalizado, "META-OK", "enviado");

        } catch (Exception e) {
            log.error("error al enviar mensaje a +{}: {}", numeroNormalizado, e.getMessage());
            return NotificacionResponse.fallo("WHATSAPP", numeroNormalizado, e.getMessage());
        }
    }

    // mensaje sin emojis y sin asteriscos
    public String construirMensajeConfirmacion(Reserva reserva) {
        String nombreHuesped = reserva.getHuesped().getNombres();
        String habs = reserva.getNumerosHabitaciones().isBlank()
                ? (!reserva.getHabitaciones().isEmpty() ? reserva.getHabitaciones().get(0).getNombreTipo() : "Habitacion")
                : "Hab. " + reserva.getNumerosHabitaciones();

        return """
                HOTEL HAMAY WASI
                Hola %s, tu reserva fue confirmada con exito
                
                Detalles de la reserva:
                - Codigo: %s
                - Entrada: %s (desde las 14:00)
                - Salida: %s (hasta las 12:00)
                - Estadia: %d noche(s) para %d huesped(es)
                - Habitacion: %s
                - Total: S/ %s
                
                Direccion: Jr. Huancas 245, Huancayo
                Politica: Cancelacion gratuita hasta las 18:00 hrs del dia de llegada
                
                Puedes responder a este mensaje si tienes dudas o necesitas coordinar tu llegada
                """.formatted(
                nombreHuesped,
                reserva.getCodigo(),
                reserva.getEntradaFmt(),
                reserva.getSalidaFmt(),
                reserva.getNoches(),
                reserva.getNumHuespedes(),
                habs,
                reserva.getTotalFmt()
        );
    }

    // enlace directo para abrir el chat
    public String generarEnlaceDirecto(String telefono, String mensaje) {
        String normalizado = normalizarTelefonoPeru(telefono);
        String textoCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8);
        return "https://wa.me/" + normalizado + "?text=" + textoCodificado;
    }

    // deja el numero en formato de peru 51
    public String normalizarTelefonoPeru(String raw) {
        if (raw == null) {
            return "";
        }
        String digitos = raw.replaceAll("[^0-9]", "");
        if (digitos.isBlank()) {
            return "";
        }
        if (digitos.startsWith("51") && digitos.length() == 11) {
            return digitos;
        }
        if (digitos.length() == 9 && digitos.startsWith("9")) {
            return "51" + digitos;
        }
        return digitos;
    }
}
