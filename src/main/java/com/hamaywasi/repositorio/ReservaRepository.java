package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.EstadoReserva;
import com.hamaywasi.modelo.Reserva;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @EntityGraph(attributePaths = {"huesped", "promocion"})
    Optional<Reserva> findByCodigo(String codigo);

    @EntityGraph(attributePaths = {"huesped", "promocion"})
    Optional<Reserva> findById(Long id);

    boolean existsByCodigo(String codigo);

    // recalcula el total con el procedimiento de base de datos
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "CALL sp_recalcular_total(:id)", nativeQuery = true)
    void recalcularTotal(@Param("id") Long id);

    // busquedas del panel

    // busca por huesped
    @EntityGraph(attributePaths = "huesped")
    @Query("""
            SELECT r FROM Reserva r
             WHERE LOWER(r.huesped.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
                OR LOWER(r.huesped.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))
             ORDER BY r.fechaEntrada DESC, r.id DESC
            """)
    List<Reserva> buscarPorHuesped(@Param("texto") String texto);

    // busca por habitacion
    @Query(value = """
            SELECT r.* FROM reservas r
              WHERE EXISTS (SELECT 1 FROM reserva_habitaciones rh
                              JOIN habitaciones h ON h.id = rh.habitacion_id
                              JOIN tipos_habitacion t ON t.id = h.tipo_id
                             WHERE rh.reserva_id = r.id
                               AND (h.numero ILIKE CONCAT('%', :texto, '%')
                                    OR t.nombre ILIKE CONCAT('%', :texto, '%')))
             ORDER BY r.fecha_entrada DESC, r.id DESC
            """, nativeQuery = true)
    List<Reserva> buscarPorHabitacion(@Param("texto") String texto);

    // busca por rango de fechas
    @EntityGraph(attributePaths = "huesped")
    List<Reserva> findByFechaEntradaBetweenOrderByFechaEntradaAsc(LocalDate desde, LocalDate hasta);

    // consultas de apoyo

    @EntityGraph(attributePaths = "huesped")
    List<Reserva> findByEstadoOrderByFechaEntradaAsc(EstadoReserva estado);

    @EntityGraph(attributePaths = "huesped")
    List<Reserva> findByFechaEntradaBetweenOrderByFechaEntradaDesc(LocalDate desde, LocalDate salida);

    @EntityGraph(attributePaths = {"huesped", "promocion"})
    List<Reserva> findAllByOrderByFechaEntradaDescIdDesc();
}
