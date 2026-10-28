package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByActivoTrueOrderByNombreAsc();

    List<Servicio> findByActivoTrueOrderByPrecioAsc();
}
