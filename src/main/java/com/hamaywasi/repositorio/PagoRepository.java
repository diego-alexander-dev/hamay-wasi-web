package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByReservaIdOrderByIdDesc(Long reservaId);
}
