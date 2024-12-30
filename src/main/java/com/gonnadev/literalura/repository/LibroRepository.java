package com.gonnadev.literalura.repository;

import com.gonnadev.literalura.model.Idioma;
import com.gonnadev.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Optional<Libro> findByTitulo(String tituloLibro);
    List<Libro> findByIdioma(Idioma idioma);
}