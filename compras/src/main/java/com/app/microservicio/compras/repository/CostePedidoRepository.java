package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.CostePedidoCompra;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface CostePedidoRepository extends JpaRepository<CostePedidoCompra,Long> {

    @Query("SELECT cpd FROM CostePedidoCompra cpd WHERE cpd.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    Optional<CostePedidoCompra> findByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);

    @Query("SELECT SUM(c.arancel + c.sanidad + c.plastico + c.carga + c.inland + c.muellaje + c.pif + c.despacho + c.conexiones) " +
            "FROM CostePedidoCompra c WHERE c.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    BigDecimal sumaCostes(@Param("idPedidoCompra") Long idPedidoCompra);

}
