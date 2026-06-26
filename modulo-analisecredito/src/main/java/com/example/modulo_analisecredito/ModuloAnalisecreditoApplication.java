package com.example.modulo_analisecredito;

import com.example.modulo_analisecredito.service.stategy.AnaliseCreditoService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@AllArgsConstructor
@SpringBootApplication
public class ModuloAnalisecreditoApplication {

	private AnaliseCreditoService analiseCreditoService;

	public static void main(String[] args) {
		SpringApplication.run(ModuloAnalisecreditoApplication.class, args);
	}


}
