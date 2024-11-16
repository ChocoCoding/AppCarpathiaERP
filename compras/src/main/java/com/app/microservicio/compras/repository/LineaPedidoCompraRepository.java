package com.app.microservicio.compras.repository;


import com.app.microservicio.compras.entities.PedidoCompra;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.app.microservicio.compras.entities.LineaPedidoCompra;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LineaPedidoCompraRepository extends JpaRepository<LineaPedidoCompra, Long>, JpaSpecificationExecutor<LineaPedidoCompra> {

    Page<LineaPedidoCompra> findAll(Pageable pageable);

    @Query("SELECT l FROM LineaPedidoCompra l WHERE l.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    List<LineaPedidoCompra> findByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);

    @Query("SELECT SUM(l.pNeto) FROM LineaPedidoCompra l WHERE l.pedidoCompra.idPedidoCompra = :pedidoCompraId")
    BigDecimal sumPesoNetoByPedidoCompraId(@Param("pedidoCompraId") Long pedidoCompraId);

    @Query("SELECT SUM(lp.bultos) FROM LineaPedidoCompra lp WHERE lp.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    Long sumBultosByPedidoCompraId(@Param("idPedidoCompra") Long idPedidoCompra);

    @Query("SELECT SUM(lp.valorCompra) FROM LineaPedidoCompra lp WHERE lp.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    BigDecimal sumValorCompraByPedidoCompraId(@Param("idPedidoCompra") Long idPedidoCompra);

}