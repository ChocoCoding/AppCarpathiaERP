package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.DatosBarcoPedidoCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DatosBarcoRepository extends JpaRepository<DatosBarcoPedidoCompra, Long>, JpaSpecificationExecutor<DatosBarcoPedidoCompra> {


    Page<DatosBarcoPedidoCompra> findAll(Pageable pageable);
}
