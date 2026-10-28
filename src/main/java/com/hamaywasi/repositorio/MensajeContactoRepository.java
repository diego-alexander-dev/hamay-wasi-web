package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {

    List<MensajeContacto> findByLeidoFalseOrderByCreadoEnDesc();

    List<MensajeContacto> findAllByOrderByLeidoAscCreadoEnDesc();

    long countByLeidoFalse();
}
