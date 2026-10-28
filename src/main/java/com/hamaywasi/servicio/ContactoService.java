package com.hamaywasi.servicio;

import com.hamaywasi.form.ContactoForm;
import com.hamaywasi.modelo.MensajeContacto;
import com.hamaywasi.repositorio.MensajeContactoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// servicio para mensajes de contacto
@Service
@Transactional
public class ContactoService {

    private static final Logger log = LoggerFactory.getLogger(ContactoService.class);

    private final MensajeContactoRepository repo;

    public ContactoService(MensajeContactoRepository repo) {
        this.repo = repo;
    }

    // guarda el mensaje y valida spam
    public boolean guardar(ContactoForm form) {
        if (form.esTrampa()) {
            log.info("Mensaje descartado por campo trampa: posible spam");
            return false;
        }
        MensajeContacto m = new MensajeContacto();
        m.setNombre(form.getNombre().trim());
        m.setApellidos(form.getApellidos().trim());
        m.setEmail(form.getEmail().trim());
        m.setTelefono(vacioANull(form.getTelefono()));
        m.setMensaje(form.getMensaje().trim());
        m.setLeido(Boolean.FALSE);
        repo.save(m);
        log.info("Mensaje de contacto guardado (id {})", m.getId());
        return true;
    }

    private String vacioANull(String texto) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim();
        return t.isEmpty() ? null : t;
    }
}
