package com.microservicio.compras_ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ComprasVentasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComprasVentasApplication.class, args);
	}

}
