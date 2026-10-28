package com.hamaywasi.repositorio;

import com.hamaywasi.modelo.Huesped;
import com.hamaywasi.modelo.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HuespedRepository extends JpaRepository<Huesped, Long> {

    // busca por tipo y numero de documento
    Optional<Huesped> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

    Optional<Huesped> findByEmailIgnoreCase(String email);

    List<Huesped> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrderByApellidosAsc(
            String nombres, String apellidos);
}
