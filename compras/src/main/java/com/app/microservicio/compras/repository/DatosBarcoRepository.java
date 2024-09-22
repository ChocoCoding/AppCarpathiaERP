package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.DatosBarcoPedidoCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatosBarcoRepository extends JpaRepository<DatosBarcoPedidoCompra, Long> {
}
