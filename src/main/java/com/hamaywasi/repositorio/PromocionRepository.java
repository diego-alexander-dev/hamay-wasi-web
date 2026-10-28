package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    // busca promocion activa por fecha
    @Query("""
            SELECT p FROM Promocion p
             WHERE UPPER(p.codigo) = UPPER(:codigo)
               AND p.activo = TRUE
               AND p.vigenteDesde <= :fecha
               AND p.vigenteHasta >= :fecha
            """)
    Optional<Promocion> findVigentePorCodigo(@Param("codigo") String codigo, @Param("fecha") LocalDate fecha);
}
