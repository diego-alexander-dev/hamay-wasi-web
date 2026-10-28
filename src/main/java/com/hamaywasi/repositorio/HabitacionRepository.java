package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.EstadoHabitacion;
import com.hamaywasi.modelo.Habitacion;
import com.hamaywasi.modelo.HabitacionDisponible;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    // habitaciones libres usando la funcion de base de datos
    @Query(value = """
            SELECT d.hab_id    AS "habId",
                   d.hab_numero AS "habNumero",
                   d.hab_piso   AS "habPiso",
                   d.tipo_nombre AS "tipoNombre",
                   d.precio     AS "precio"
              FROM fn_habitaciones_disponibles(CAST(:entrada AS date),
                                               CAST(:salida  AS date),
                                               CAST(:tipoId  AS bigint)) d
             ORDER BY d.precio, d.hab_numero
            """, nativeQuery = true)
    List<HabitacionDisponible> disponibles(@Param("entrada") LocalDate entrada,
                                           @Param("salida") LocalDate salida,
                                           @Param("tipoId") Long tipoId);

    @EntityGraph(attributePaths = "tipo")
    List<Habitacion> findAllByOrderByPisoAscNumeroAsc();

    @EntityGraph(attributePaths = "tipo")
    List<Habitacion> findByTipoIdOrderByNumeroAsc(Long tipoId);

    @EntityGraph(attributePaths = "tipo")
    Optional<Habitacion> findByNumero(String numero);

    @EntityGraph(attributePaths = "tipo")
    List<Habitacion> findByEstadoOrderByPisoAscNumeroAsc(EstadoHabitacion estado);
}
