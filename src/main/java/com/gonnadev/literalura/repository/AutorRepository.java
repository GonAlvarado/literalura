package com.gonnadev.literalura.repository;

import com.gonnadev.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombre(String nombreAutor);
    List<Autor> findByNombreContainingIgnoreCase(String nombreAutor);
    List<Autor> findByFechaDeNacimientoLessThanEqualAndFechaDeFallecimientoGreaterThanEqualOrFechaDeFallecimientoIsNull(
            Integer anioNacimiento, Integer anioActual
    );
}
