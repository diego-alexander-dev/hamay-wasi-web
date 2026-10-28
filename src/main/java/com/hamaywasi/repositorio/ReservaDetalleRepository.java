package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.ReservaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// lectura de la vista de reservas con saldos
public interface ReservaDetalleRepository extends JpaRepository<ReservaDetalle, Long> {

    @Query("SELECT d FROM ReservaDetalle d ORDER BY d.fechaEntrada DESC, d.id DESC")
    List<ReservaDetalle> findAllDetalle();

    @Query("SELECT d FROM ReservaDetalle d WHERE d.id = :id")
    ReservaDetalle findByIdDetalle(@Param("id") Long id);

    // consulta con filtros para el panel de recepcion
    @Query(value = """
            SELECT d.* FROM v_reservas_detalle d
             WHERE (CAST(:huesped AS text) IS NULL
                    OR LOWER(d.huesped) LIKE LOWER('%' || CAST(:huesped AS text) || '%'))
               AND (CAST(:habitacion AS text) IS NULL
                    OR LOWER(d.tipos) LIKE LOWER('%' || CAST(:habitacion AS text) || '%')
                    OR LOWER(d.habitaciones) LIKE LOWER('%' || CAST(:habitacion AS text) || '%'))
               AND (CAST(:desde AS date) IS NULL OR d.fecha_entrada >= CAST(:desde AS date))
               AND (CAST(:hasta AS date) IS NULL OR d.fecha_entrada <= CAST(:hasta AS date))
               AND (CAST(:estado AS text) IS NULL OR d.estado = CAST(:estado AS text))
             ORDER BY d.fecha_entrada DESC, d.id DESC
            """, nativeQuery = true)
    List<ReservaDetalle> filtrar(@Param("huesped") String huesped,
                                 @Param("habitacion") String habitacion,
                                 @Param("desde") LocalDate desde,
                                 @Param("hasta") LocalDate hasta,
                                 @Param("estado") String estado);
}
