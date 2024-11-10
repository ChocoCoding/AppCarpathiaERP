package com.app.microservicio.ventas.repository;

import com.app.microservicio.ventas.entities.PedidoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PedidoVentaRepository extends JpaRepository<PedidoVenta,Long>, JpaSpecificationExecutor<PedidoVenta> {
    Page<PedidoVenta> findAll(Pageable pageable);

}
