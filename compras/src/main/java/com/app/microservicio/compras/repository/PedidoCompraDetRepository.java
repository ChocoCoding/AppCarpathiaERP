package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.PedidoCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.app.microservicio.compras.entities.PedidoCompraDet;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PedidoCompraDetRepository extends JpaRepository<PedidoCompraDet, Long>, JpaSpecificationExecutor<PedidoCompraDet> {
void deleteByidPedidoCompraDet(Long idPedidoCompraDet);
    Page<PedidoCompraDet> findAll(Pageable pageable);

    @Query("SELECT pcd FROM PedidoCompraDet pcd WHERE pcd.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    Optional<PedidoCompraDet> findByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);

    @Query("SELECT pcd.valorCompraTotal FROM PedidoCompraDet pcd WHERE pcd.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    BigDecimal findValorCompraTotalByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);

    @Query("SELECT pcd.pesoNetoTotal FROM PedidoCompraDet pcd WHERE pcd.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    BigDecimal findPesoNetoTotalByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);

}