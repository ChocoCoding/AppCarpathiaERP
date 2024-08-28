package com.app.microservicio.compras.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.app.microservicio.compras.entities.PedidoCompraDet;

@Repository
public interface PedidoCompraDetRepository extends JpaRepository<PedidoCompraDet, Long> {
}