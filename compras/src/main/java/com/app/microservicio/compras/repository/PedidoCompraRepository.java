package com.app.microservicio.compras.repository;


import com.app.microservicio.compras.entities.PedidoCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long>, JpaSpecificationExecutor<PedidoCompra> {
    Page<PedidoCompra> findAll(Pageable pageable);

    @Query("SELECT p FROM PedidoCompra p WHERE p.nOperacion = :nOperacion")
    Optional<PedidoCompra> findPedidoCompraByNoperacion(@Param("nOperacion") Long nOperacion);
}
