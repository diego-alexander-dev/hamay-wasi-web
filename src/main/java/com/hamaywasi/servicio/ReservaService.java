package com.hamaywasi.servicio;

import com.hamaywasi.evento.ReservaConfirmadaEvent;
import com.hamaywasi.form.ErrorFormulario;
import com.hamaywasi.form.ReservaForm;
import com.hamaywasi.modelo.EstadoReserva;
import com.hamaywasi.modelo.Habitacion;
import com.hamaywasi.modelo.HabitacionDisponible;
import com.hamaywasi.modelo.Huesped;
import com.hamaywasi.modelo.Promocion;
import com.hamaywasi.modelo.Reserva;
import com.hamaywasi.modelo.ReservaHabitacion;
import com.hamaywasi.modelo.ReservaServicio;
import com.hamaywasi.modelo.Servicio;
import com.hamaywasi.modelo.TipoDocumento;
import com.hamaywasi.modelo.TipoHabitacion;
import com.hamaywasi.repositorio.HabitacionRepository;
import com.hamaywasi.repositorio.HuespedRepository;
import com.hamaywasi.repositorio.PromocionRepository;
import com.hamaywasi.repositorio.ReservaRepository;
import com.hamaywasi.repositorio.ServicioRepository;
import com.hamaywasi.repositorio.TipoHabitacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

// logica para crear y consultar reservas
@Service
@Transactional
@SuppressWarnings("null")
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    // letras y numeros para generar el codigo
    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final String MENSAJE_CRUCE = "La habitación ya está reservada en esas fechas";

    private final ReservaRepository reservaRepo;
    private final HuespedRepository huespedRepo;
    private final TipoHabitacionRepository tipoRepo;
    private final ServicioRepository servicioRepo;
    private final PromocionRepository promoRepo;
    private final HabitacionRepository habitacionRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final SecureRandom azar = new SecureRandom();

    public ReservaService(ReservaRepository reservaRepo, HuespedRepository huespedRepo,
                          TipoHabitacionRepository tipoRepo, ServicioRepository servicioRepo,
                          PromocionRepository promoRepo, HabitacionRepository habitacionRepo,
                          ApplicationEventPublisher eventPublisher) {
        this.reservaRepo = reservaRepo;
        this.huespedRepo = huespedRepo;
        this.tipoRepo = tipoRepo;
        this.servicioRepo = servicioRepo;
        this.promoRepo = promoRepo;
        this.habitacionRepo = habitacionRepo;
        this.eventPublisher = eventPublisher;
    }

    // crea la reserva
    public Reserva crear(ReservaForm form) {
        return crear(form, EstadoReserva.CONFIRMADA);
    }

    // crea reserva con estado inicial
    public Reserva crear(ReservaForm form, EstadoReserva estadoInicial) {
        Huesped huesped = obtenerOCrearHuesped(form);
        Promocion promocion = resolverPromocion(form.getCodigoPromo());
        TipoHabitacion tipo = resolverTipo(form.getTipoHabitacionId());

        int numHabitaciones = form.getNumHabitaciones() == null ? 1 : form.getNumHabitaciones();
        int numHuespedes = form.getNumHuespedes() == null ? 1 : form.getNumHuespedes();
        validarCapacidad(tipo, numHabitaciones, numHuespedes);

        List<HabitacionDisponible> libres = habitacionRepo.disponibles(
                form.getFechaEntrada(), form.getFechaSalida(), tipo.getId());
        if (libres.size() < numHabitaciones) {
            throw new ErrorFormulario("tipoHabitacionId", soloQuedan(libres.size(), tipo.getNombre()));
        }

        Reserva reserva = new Reserva();
        reserva.setCodigo(generarCodigo());
        reserva.setHuesped(huesped);
        reserva.setPromocion(promocion);
        reserva.setFechaEntrada(form.getFechaEntrada());
        reserva.setFechaSalida(form.getFechaSalida());
        reserva.setNumHuespedes((short) numHuespedes);
        reserva.setEstado(estadoInicial == null ? EstadoReserva.CONFIRMADA : estadoInicial);
        reserva.setObservaciones(limpiar(form.getObservaciones()));

        // agrega habitaciones con el precio actual
        for (HabitacionDisponible libre : libres.subList(0, numHabitaciones)) {
            Habitacion habitacion = habitacionRepo.findById(libre.getHabId())
                    .orElseThrow(() -> new ErrorFormulario("tipoHabitacionId",
                            "Ya no queda esa habitación. Elige otra fecha o tipo."));
            reserva.agregarHabitacion(ReservaHabitacion.de(reserva, habitacion, tipo.getPrecioNoche()));
        }

        agregarServicios(reserva, form);

        try {
            // guarda y calcula total
            reservaRepo.saveAndFlush(reserva);
            reservaRepo.recalcularTotal(reserva.getId());
        } catch (DataAccessException e) {
            throw traducir(e);
        }

        Reserva guardada = reservaRepo.findById(reserva.getId())
                .orElseThrow(() -> new NoSuchElementException("La reserva no se pudo guardar"));
        cargarCompleto(guardada);
        log.info("Solicitud procesada: Reserva {} creada ({} habitacion(es) del tipo {})",
                guardada.getCodigo(), numHabitaciones, tipo.getNombre());
        log.info("Publicando evento ReservaConfirmadaEvent para la reserva {}", guardada.getCodigo());
        eventPublisher.publishEvent(new ReservaConfirmadaEvent(guardada));
        return guardada;
    }

    // metodos de busqueda
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorCodigo(String codigo) {
        return reservaRepo.findByCodigo(codigo).map(this::cargarCompleto);
    }

    @Transactional(readOnly = true)
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepo.findById(id).map(this::cargarCompleto);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listar() {
        return reservaRepo.findAllByOrderByFechaEntradaDescIdDesc().stream()
                .map(this::cargarCompleto)
                .toList();
    }

    // buscar por huesped
    @Transactional(readOnly = true)
    public List<Reserva> buscarPorHuesped(String texto) {
        return reservaRepo.buscarPorHuesped(texto == null ? "" : texto.trim()).stream()
                .map(this::cargarCompleto)
                .toList();
    }

    // buscar por habitacion
    @Transactional(readOnly = true)
    public List<Reserva> buscarPorHabitacion(String texto) {
        return reservaRepo.buscarPorHabitacion(texto == null ? "" : texto.trim()).stream()
                .map(this::cargarCompleto)
                .toList();
    }

    // buscar por fechas
    @Transactional(readOnly = true)
    public List<Reserva> buscarPorRangoDeEntrada(LocalDate desde, LocalDate hasta) {
        return reservaRepo.findByFechaEntradaBetweenOrderByFechaEntradaAsc(desde, hasta).stream()
                .map(this::cargarCompleto)
                .toList();
    }

    // cambiar estado o eliminar
    public Reserva cambiarEstado(Long id, EstadoReserva destino) {
        Reserva reserva = reservaRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("La reserva ya no existe"));
        if (reserva.getEstado() == destino) {
            return cargarCompleto(reserva);
        }
        if (!reserva.getEstado().puedeIrA(destino)) {
            throw new IllegalStateException("No se puede pasar de "
                    + reserva.getEstado().getEtiqueta() + " a " + destino.getEtiqueta());
        }
        reserva.setEstado(destino);
        return cargarCompleto(reservaRepo.saveAndFlush(reserva));
    }

    public void eliminar(Long id) {
        if (!reservaRepo.existsById(id)) {
            throw new NoSuchElementException("La reserva ya no existe");
        }
        reservaRepo.deleteById(id);
    }

    // consulta de habitaciones disponibles
    @Transactional(readOnly = true)
    public List<HabitacionDisponible> disponibles(LocalDate entrada, LocalDate salida, Long tipoId) {
        return habitacionRepo.disponibles(entrada, salida, tipoId);
    }

    // metodos internos
    private Huesped obtenerOCrearHuesped(ReservaForm form) {
        String documento = form.getNumeroDocumento().trim().toUpperCase(java.util.Locale.ROOT);
        TipoDocumento tipo = form.getTipoDocumento() == null ? TipoDocumento.DNI : form.getTipoDocumento();
        Optional<Huesped> existente = huespedRepo.findByTipoDocumentoAndNumeroDocumento(tipo, documento);
        if (existente.isPresent()) {
            Huesped h = existente.get();
            // actualiza datos si ya existe
            h.setNombres(form.getNombres().trim());
            h.setApellidos(form.getApellidos().trim());
            h.setEmail(form.getEmail().trim());
            if (form.getTelefono() != null && !form.getTelefono().isBlank()) {
                h.setTelefono(form.getTelefono().trim());
            }
            h.setPais(limpiar(form.getPais()) == null ? "Perú" : form.getPais().trim());
            return huespedRepo.save(h);
        }
        Huesped nuevo = new Huesped();
        nuevo.setNombres(form.getNombres().trim());
        nuevo.setApellidos(form.getApellidos().trim());
        nuevo.setTipoDocumento(tipo);
        nuevo.setNumeroDocumento(documento);
        nuevo.setEmail(form.getEmail().trim());
        nuevo.setTelefono(limpiar(form.getTelefono()));
        nuevo.setPais(form.getPais() == null || form.getPais().isBlank() ? "Perú" : form.getPais().trim());
        return huespedRepo.save(nuevo);
    }

    private Promocion resolverPromocion(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        return promoRepo.findVigentePorCodigo(codigo.trim(), LocalDate.now())
                .orElseThrow(() -> new ErrorFormulario("codigoPromo",
                        "Ese código de promoción no existe o ya no está vigente"));
    }

    private TipoHabitacion resolverTipo(Long tipoId) {
        if (tipoId == null) {
            throw new ErrorFormulario("tipoHabitacionId", "Elige un tipo de habitación");
        }
        TipoHabitacion tipo = tipoRepo.findById(tipoId)
                .orElseThrow(() -> new ErrorFormulario("tipoHabitacionId",
                        "Ese tipo de habitación ya no está disponible"));
        if (!tipo.isActivo()) {
            throw new ErrorFormulario("tipoHabitacionId",
                    "Ese tipo de habitación ya no está disponible");
        }
        return tipo;
    }

    private void validarCapacidad(TipoHabitacion tipo, int numHabitaciones, int numHuespedes) {
        int plazas = (tipo.getCapacidad() == null ? 0 : tipo.getCapacidad()) * numHabitaciones;
        if (numHuespedes > plazas) {
            throw new ErrorFormulario("numHuespedes", plazas == 1
                    ? "Esa habitación solo tiene 1 plaza. Elige más habitaciones."
                    : "Con " + numHabitaciones + " de ese tipo tienes " + plazas
                      + " plazas. Elige más habitaciones o menos huéspedes.");
        }
    }

    private void agregarServicios(Reserva reserva, ReservaForm form) {
        if (form.getServicios() == null) {
            return;
        }
        Set<Long> yaAgregados = new HashSet<>();
        for (ReservaForm.LineaServicio linea : form.getServicios()) {
            if (linea == null || !linea.isSeleccionado()) {
                continue;
            }
            int cantidad = linea.getCantidad() == null ? 0 : linea.getCantidad();
            if (linea.getServicioId() == null || linea.getServicioId() <= 0
                    || cantidad <= 0 || !yaAgregados.add(linea.getServicioId())) {
                continue;
            }
            Servicio servicio = servicioRepo.findById(linea.getServicioId())
                    .filter(s -> s.isActivo())
                    .orElse(null);
            if (servicio == null) {
                continue;
            }
            reserva.agregarServicio(ReservaServicio.de(reserva, servicio, (short) cantidad, servicio.getPrecio()));
        }
    }



    // carga datos de la reserva
    private Reserva cargarCompleto(Reserva reserva) {
        reserva.getHabitaciones().forEach(linea -> {
            if (linea.getHabitacion() != null && linea.getHabitacion().getTipo() != null) {
                linea.getHabitacion().getTipo().getNombre();
            }
        });
        reserva.getServicios().forEach(linea -> {
            if (linea.getServicio() != null) {
                linea.getServicio().getNombre();
            }
        });
        reserva.getPagos().forEach(pago -> pago.getEstado());
        if (reserva.getHuesped() != null) {
            reserva.getHuesped().getNombreCompleto();
        }
        if (reserva.getPromocion() != null) {
            reserva.getPromocion().getCodigo();
        }
        return reserva;
    }

    // mensaje si se cruzan las fechas
    private RuntimeException traducir(DataAccessException e) {
        Throwable causa = e;
        while (causa != null) {
            String mensaje = causa.getMessage();
            if (mensaje != null && mensaje.contains("reservada en esas fechas")) {
                log.info("Cruce de fechas rechazado por el trigger: {}", causa.getMessage());
                return new ErrorFormulario(null, MENSAJE_CRUCE);
            }
            if (causa.getCause() == causa) {
                break;
            }
            causa = causa.getCause();
        }
        return e;
    }

    private String soloQuedan(int disponibles, String tipo) {
        if (disponibles <= 0) {
            return "No queda ninguna habitación de " + tipo + " libre en esas fechas";
        }
        return "Solo quedan " + disponibles + " de " + tipo
                + (disponibles == 1 ? " libre" : " libres") + " en esas fechas";
    }

    private String generarCodigo() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder("HW-");
            for (int i = 0; i < 6; i++) {
                sb.append(ALFABETO.charAt(azar.nextInt(ALFABETO.length())));
            }
            codigo = sb.toString();
        } while (reservaRepo.existsByCodigo(codigo));
        return codigo;
    }

    private String limpiar(String texto) {
        if (texto == null) {
            return null;
        }
        String t = texto.trim();
        return t.isEmpty() ? null : t;
    }
}
