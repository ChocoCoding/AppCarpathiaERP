package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.CostePedidoCompra;
import com.app.microservicio.compras.entities.DatosBarcoPedidoCompra;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface CostePedidoRepository extends JpaRepository<CostePedidoCompra,Long>, JpaSpecificationExecutor<CostePedidoCompra> {

    Page<CostePedidoCompra> findAll(Pageable pageable);

    @Query("SELECT cpd FROM CostePedidoCompra cpd WHERE cpd.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    Optional<CostePedidoCompra> findByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);

    @Query("SELECT SUM(COALESCE(c.arancel, 0) + COALESCE(c.sanidad, 0) + COALESCE(c.plastico, 0) + " +
            "COALESCE(c.carga, 0) + COALESCE(c.inland, 0) + COALESCE(c.muellaje, 0) + " +
            "COALESCE(c.pif, 0) + COALESCE(c.despacho, 0) + COALESCE(c.conexiones, 0)) " +
            "FROM CostePedidoCompra c WHERE c.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    BigDecimal sumaCostes(@Param("idPedidoCompra") Long idPedidoCompra);

}
