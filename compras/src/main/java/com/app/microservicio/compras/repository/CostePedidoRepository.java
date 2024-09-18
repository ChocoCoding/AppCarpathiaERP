package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.CostePedidoCompra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostePedidoRepository extends JpaRepository<CostePedidoCompra,Long> {


}
