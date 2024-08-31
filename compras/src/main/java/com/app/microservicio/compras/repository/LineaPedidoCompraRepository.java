package com.app.microservicio.compras.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.app.microservicio.compras.entities.LineaPedidoCompra;

import java.util.List;
import java.util.Optional;

@Repository
public interface LineaPedidoCompraRepository extends JpaRepository<LineaPedidoCompra, Long> {

    @Query("SELECT l FROM LineaPedidoCompra l WHERE l.pedidoCompra.idPedidoCompra =: idPedidoCOmpra")
    List<LineaPedidoCompra> findByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);
}