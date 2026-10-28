package com.hamaywasi.servicio;

import com.hamaywasi.modelo.Promocion;
import com.hamaywasi.modelo.Servicio;
import com.hamaywasi.modelo.TipoHabitacion;
import com.hamaywasi.repositorio.PromocionRepository;
import com.hamaywasi.repositorio.ServicioRepository;
import com.hamaywasi.repositorio.TipoHabitacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// servicio de catalogos de habitaciones y servicios
@Service
@Transactional(readOnly = true)
public class CatalogoService {

    // servicios que no se muestran como extra
    public static final List<String> SERVICIOS_SIN_OFRECER =
            List.of("desayuno buffet", "desayuno");

    private final TipoHabitacionRepository tipoRepo;
    private final ServicioRepository servicioRepo;
    private final PromocionRepository promoRepo;

    public CatalogoService(TipoHabitacionRepository tipoRepo, ServicioRepository servicioRepo,
                           PromocionRepository promoRepo) {
        this.tipoRepo = tipoRepo;
        this.servicioRepo = servicioRepo;
        this.promoRepo = promoRepo;
    }

    // tipos de habitacion activos
    public List<TipoHabitacion> tiposActivos() {
        return tipoRepo.findByActivoTrueOrderByPrecioNocheAsc();
    }

    public Optional<TipoHabitacion> tipoPorId(Long id) {
        return id == null ? Optional.empty() : tipoRepo.findById(id);
    }

    // servicios activos disponibles
    public List<Servicio> serviciosActivos() {
        return servicioRepo.findByActivoTrueOrderByPrecioAsc().stream()
                .filter(s -> SERVICIOS_SIN_OFRECER.stream().noneMatch(
                        exclusiones -> s.getNombre().toLowerCase().contains(exclusiones)))
                .toList();
    }

    public Optional<Promocion> promocionVigente(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        }
        return promoRepo.findVigentePorCodigo(codigo.trim(), LocalDate.now());
    }
}
