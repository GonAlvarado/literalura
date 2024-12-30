package com.gonnadev.literalura.principal;

import com.gonnadev.literalura.model.*;
import com.gonnadev.literalura.repository.AutorRepository;
import com.gonnadev.literalura.repository.LibroRepository;
import com.gonnadev.literalura.service.ConsumoAPI;
import com.gonnadev.literalura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    ConsumoAPI consumoAPI = new ConsumoAPI();
    ConvierteDatos conversor = new ConvierteDatos();
    LibroRepository libroRepository;
    AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu(){
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    ************************************************************
                                   BIENVENIDOS A LITERALURA
                    ************************************************************
                   \s
                       Ingrese la opción deseada:
                   \s
                       1 - Agregar libro
                       2 - Listar libros
                       3 - Buscar libros por idioma
                       4 - Listar autores
                       5 - Listar autores vivos en año determinado
                       6 - Cantidad de libros en idioma determinado
                       7 - Estadísticas
                       8 - Top 10 libros mas descargados
                       9 - Buscar autor por nombre
                       10 - Buscar autor por otra característica
                                 \s
                       0 - Salir
                   \s
                    ************************************************************
                   \s""";

            System.out.println(menu);

            try {
                opcion = teclado.nextInt();
                teclado.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                teclado.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    agregarLibro();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    buscarLibrosPorIdioma();
                    break;
                case 4:
                    ListarAutores();
                    break;
                case 5:
                    ListarAutoresVivos();
                    break;
                case 6:
                    cantidadLibrosPorIdioma();
                    break;
                case 7:
                    mostrarEstadisticas();
                    break;
                case 8:
                    topDiezLibros();
                    break;
                case 9:
                    buscarAutorPorNombre();
                    break;
                case 10:
                    buscarAutorPorCaracteristica();
                    break;
                case 0:
                    System.out.println("¡Gracias por utilizar nuestra aplicación!");
                    break;
                default:
                    System.out.println("Por favor, ingrese una opción válida.");
            }
        }
    }

    private void agregarLibro() {
        System.out.println("Ingrese el título del libro que deseas agregar a la biblioteca:");
        var tituloBuscado = teclado.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloBuscado.replace(" ", "+"));
        DatosAPI datos = conversor.obtenerDatos(json, DatosAPI.class);
        Optional<DatosLibro> libroBuscado = datos.libros().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloBuscado.toUpperCase()))
                .findFirst();
        if(libroBuscado.isPresent()){
            DatosLibro datosLibro = libroBuscado.get();

            System.out.println("Libro encontrado: " + datosLibro.titulo());
            System.out.println("Autor: " + datosLibro.autores().get(0).nombre());

            String nombreAutor = datosLibro.autores().get(0).nombre();
            Autor autorExistente = autorRepository.findByNombre(nombreAutor)
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor(datosLibro.autores().get(0));
                        return autorRepository.save(nuevoAutor);
                    });

            String tituloLibro = datosLibro.titulo();
            Libro libroExistente = libroRepository.findByTitulo(tituloLibro)
                    .orElseGet(() -> {
                        Libro nuevoLibro = new Libro(datosLibro);
                        nuevoLibro.setAutor(autorExistente);
                        return libroRepository.save(nuevoLibro);
                    });
        }else{
            System.out.println("Título no encontrado");
        }
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();
        libros.forEach(System.out::println);
    }

    private void buscarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma que desea buscar:");
        var idioma = teclado.nextLine();
        var idiomaEsp = Idioma.fromEspanol(idioma);
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idiomaEsp);
        System.out.println("Libros en idioma " + idiomaEsp);
        librosPorIdioma.forEach(System.out::println);
    }

    private void ListarAutores() {
        List<Autor> autores = autorRepository.findAll();
        autores.forEach(System.out::println);
    }

    private void ListarAutoresVivos() {
        System.out.println("Ingrese el año en que desea buscar autores vivos:");
        var fecha = teclado.nextLine();
        Integer anio = Integer.valueOf(fecha);
        List<Autor> autores = autorRepository.findByFechaDeNacimientoLessThanEqualAndFechaDeFallecimientoGreaterThanEqualOrFechaDeFallecimientoIsNull(anio, anio);
        autores.forEach(System.out::println);
    }

    private void cantidadLibrosPorIdioma() {
        System.out.println("Ingrese el idioma que desea buscar:");
        var idioma = teclado.nextLine();
        var idiomaEsp = Idioma.fromEspanol(idioma);
        List<Libro> librosPorIdioma = libroRepository.findByIdioma(idiomaEsp);
        var cantidad = librosPorIdioma.stream()
                .count();
        System.out.println("Cantidad de libros en idioma " + idioma + ": " + cantidad);
    }

    private void mostrarEstadisticas() {
        var json = consumoAPI.obtenerDatos(URL_BASE);
        DatosAPI datos = conversor.obtenerDatos(json, DatosAPI.class);
        DoubleSummaryStatistics est = datos.libros().stream()
                .filter(l -> l.numeroDeDescargas() > 0)
                .collect(Collectors.summarizingDouble(DatosLibro::numeroDeDescargas));
        System.out.println("Promedio de descargas: " + est.getAverage());
        System.out.println("Cantidad máxima de descargas: " + est.getMax());
        System.out.println("Cantidad mínima de descargas: " + est.getMin());
        System.out.println("Cantidad de registros analizados: " + est.getCount());
    }

    private void topDiezLibros() {
        var json = consumoAPI.obtenerDatos(URL_BASE);
        DatosAPI datos = conversor.obtenerDatos(json, DatosAPI.class);
        datos.libros().stream()
                .sorted(Comparator.comparing(DatosLibro::numeroDeDescargas).reversed())
                .limit(10)
                .map(l -> "Título: " + l.titulo().toUpperCase() + " - Número de descargas: " + l.numeroDeDescargas().intValue())
                .forEach(System.out::println);
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre del autor que desea buscar:");
        var nombre = teclado.nextLine();
        Optional<Autor> autor = autorRepository.findByNombre(nombre);
        if(autor.isPresent()){
            System.out.println(autor.get());
        } else {
            System.out.println("Autor no encontrado");
        }
    }

    private void buscarAutorPorCaracteristica() {
    }
}
