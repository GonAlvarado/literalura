package com.gonnadev.literalura;

import com.gonnadev.literalura.principal.Principal;
import com.gonnadev.literalura.repository.AutorRepository;
import com.gonnadev.literalura.repository.LibroRepository;
import com.gonnadev.literalura.service.ConsumoAPI;
import com.gonnadev.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraluraApplication implements CommandLineRunner {

	@Autowired
	LibroRepository libroRepository;
	@Autowired
	AutorRepository autorRepository;
	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroRepository, autorRepository);
		principal.muestraElMenu();
	}
}