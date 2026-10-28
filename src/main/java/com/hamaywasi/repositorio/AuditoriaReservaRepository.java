package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.AuditoriaReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaReservaRepository extends JpaRepository<AuditoriaReserva, Long> {

    // historial de la reserva
    List<AuditoriaReserva> findByReservaIdOrderByFechaDescIdDesc(Long reservaId);

    List<AuditoriaReserva> findTop8ByOrderByFechaDescIdDesc();
}
