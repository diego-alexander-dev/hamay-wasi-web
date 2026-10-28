package com.hamaywasi.web;

import com.hamaywasi.catalogo.Contenido;
import com.hamaywasi.form.ContactoForm;
import com.hamaywasi.form.ErrorFormulario;
import com.hamaywasi.form.ReservaForm;
import com.hamaywasi.modelo.Reserva;
import com.hamaywasi.modelo.Servicio;
import com.hamaywasi.modelo.TipoHabitacion;
import com.hamaywasi.servicio.CatalogoService;
import com.hamaywasi.servicio.ContactoService;
import com.hamaywasi.servicio.ReservaService;
import com.hamaywasi.servicio.notificacion.WhatsAppService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// controlador de la web publica  
@Controller
public class PublicoController {

    private final ReservaService reservaServicio;
    private final CatalogoService catalogo;
    private final ContactoService contacto;
    private final WhatsAppService whatsAppService;

    public PublicoController(ReservaService reservaServicio, CatalogoService catalogo,
                             ContactoService contacto, WhatsAppService whatsAppService) {
        this.reservaServicio = reservaServicio;
        this.catalogo = catalogo;
        this.contacto = contacto;
        this.whatsAppService = whatsAppService;
    }

    // portada
    @GetMapping("/")
    public String inicio(Model model) {
        modeloCatalogoPublico(model);
        return "index";
    }

    // catalogo de habitaciones
    @GetMapping("/habitaciones")
    public String habitaciones(Model model) {
        modeloCatalogoPublico(model);
        return "habitaciones";
    }

    // pagina y formulario de reserva
    @GetMapping("/reservas")
    public String nuevaReserva(
            @RequestParam(name = "tipo", required = false) Long tipo,
            @RequestParam(name = "entrada", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate entrada,
            @RequestParam(name = "salida", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate salida,
            @RequestParam(name = "huespedes", required = false) Integer huespedes,
            @RequestParam(name = "tab", required = false) String tab,
            Model model) {

        LocalDate hoy = LocalDate.now();
        ReservaForm form = new ReservaForm();
        form.setFechaEntrada(entrada != null && !entrada.isBefore(hoy) ? entrada : hoy.plusDays(1));
        form.setFechaSalida(salida != null && salida.isAfter(form.getFechaEntrada())
                ? salida : form.getFechaEntrada().plusDays(2));
        if (tipo != null && catalogo.tipoPorId(tipo).isPresent()) {
            form.setTipoHabitacionId(tipo);
        }
        if (huespedes != null && huespedes >= 1 && huespedes <= 20) {
            form.setNumHuespedes(huespedes);
        }
        formularioReserva(model, form, null);
        if (tab != null && !tab.isBlank()) {
            model.addAttribute("tab", tab);
        }
        return "reservas";
    }

    @SuppressWarnings("null")
    @PostMapping("/reservas")
    public String crear(@Valid @ModelAttribute("form") ReservaForm form,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            formularioReserva(model, form, null);
            return "reservas";
        }
        try {
            Reserva reserva = reservaServicio.crear(form);
            return "redirect:/reservas/confirmacion/" + reserva.getCodigo();
        } catch (ErrorFormulario e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Error al procesar la reserva";
            String campo = e.getCampo();
            if (campo == null) {
                result.reject("reserva.rechazada", mensaje);
            } else {
                result.rejectValue(campo, "reserva.rechazada", mensaje);
            }
            formularioReserva(model, form, mensaje);
            return "reservas";
        }
    }

    // confirmacion de la reserva
    @GetMapping("/reservas/confirmacion/{codigo}")
    public String confirmacion(@PathVariable String codigo, Model model) {
        Reserva reserva = reservaServicio.buscarPorCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No encontramos una reserva con ese código"));
        model.addAttribute("reserva", reserva);

        String waLink = "";
        if (reserva.getHuesped() != null && reserva.getHuesped().getTelefono() != null
                && !reserva.getHuesped().getTelefono().isBlank()) {
            waLink = whatsAppService.generarEnlaceDirecto(
                    reserva.getHuesped().getTelefono(),
                    whatsAppService.construirMensajeConfirmacion(reserva)
            );
        }
        model.addAttribute("waLink", waLink);

        return "confirmacion";
    }

    // mensaje de contacto
    @PostMapping("/contacto")
    public String contacto(@Valid @ModelAttribute("contactoForm") ContactoForm form,
                           BindingResult result, Model model, RedirectAttributes flash) {
        if (result.hasErrors()) {
            formularioReserva(model, modeloReservaVacia(), null);
            model.addAttribute("contactoForm", form);
            model.addAttribute("tab", "contacto");
            return "reservas";
        }
        contacto.guardar(form);
        flash.addFlashAttribute("aviso",
                "Recibimos tu consulta. Te respondemos por correo en menos de 24 horas.");
        return "redirect:/reservas?tab=contacto";
    }

    // metodos de ayuda para las vistas
    private void formularioReserva(Model model, ReservaForm form, String aviso) {
        List<TipoHabitacion> tipos = catalogo.tiposActivos();
        if (form.getTipoHabitacionId() == null && !tipos.isEmpty()) {
            form.setTipoHabitacionId(tipos.get(0).getId());
        }
        List<Servicio> extras = catalogo.serviciosActivos();
        form.alineaExtras(extras.size());
        model.addAttribute("form", form);
        model.addAttribute("tipos", tipos);
        model.addAttribute("fotos", fotosDe(tipos));
        model.addAttribute("habitacionesTexto", Contenido.porNombre());
        model.addAttribute("serviciosExtra", extras);
        model.addAttribute("tiposDocumento", com.hamaywasi.modelo.TipoDocumento.values());
        model.addAttribute("hoy", LocalDate.now());
        // formulario de contacto para la vista
        if (!model.containsAttribute("contactoForm")) {
            model.addAttribute("contactoForm", new ContactoForm());
        }
        if (aviso != null) {
            model.addAttribute("aviso", aviso);
        }
    }

    private void modeloCatalogoPublico(Model model) {
        List<TipoHabitacion> tipos = catalogo.tiposActivos();
        model.addAttribute("heroImagenes", Contenido.HERO_IMAGENES);
        model.addAttribute("categorias", Contenido.CATEGORIAS);
        model.addAttribute("atractivos", Contenido.ATRACTIVOS);
        model.addAttribute("servicios", Contenido.SERVICIOS);
        model.addAttribute("tipos", tipos);
        model.addAttribute("fotos", fotosDe(tipos));
        model.addAttribute("habitacionesTexto", Contenido.porNombre());
    }

    // mapa con fotos de cada habitacion
    private Map<Long, String> fotosDe(List<TipoHabitacion> tipos) {
        Map<Long, String> fotos = new LinkedHashMap<>();
        for (TipoHabitacion t : tipos) {
            String img = t.getImagenUrl();
            if (img == null || img.isBlank()) {
                img = Contenido.buscarHabitacion(t.getNombre())
                        .map(h -> h.imagen()).orElse(null);
            }
            fotos.put(t.getId(), img);
        }
        return fotos;
    }

    // fechas por defecto para la reserva
    private ReservaForm modeloReservaVacia() {
        ReservaForm form = new ReservaForm();
        LocalDate hoy = LocalDate.now();
        form.setFechaEntrada(hoy.plusDays(1));
        form.setFechaSalida(hoy.plusDays(3));
        return form;
    }
}
