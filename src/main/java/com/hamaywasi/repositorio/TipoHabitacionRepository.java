package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {

    // catalogo de tipos de habitacion activos
    List<TipoHabitacion> findByActivoTrueOrderByPrecioNocheAsc();

    Optional<TipoHabitacion> findByNombreIgnoreCase(String nombre);
}
