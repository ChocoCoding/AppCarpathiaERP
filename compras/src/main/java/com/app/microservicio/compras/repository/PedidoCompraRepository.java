package com.app.microservicio.compras.repository;


import com.app.microservicio.compras.entities.PedidoCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long>, JpaSpecificationExecutor<PedidoCompra> {
    Page<PedidoCompra> findAll(Pageable pageable);
}
