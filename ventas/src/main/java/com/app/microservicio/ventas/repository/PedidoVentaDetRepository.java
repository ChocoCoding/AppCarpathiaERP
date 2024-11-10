package com.app.microservicio.ventas.repository;

import com.app.microservicio.ventas.entities.PedidoVenta;
import com.app.microservicio.ventas.entities.PedidoVentaDet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface PedidoVentaDetRepository extends JpaRepository<PedidoVentaDet,Long>, JpaSpecificationExecutor<PedidoVentaDet> {
    Page<PedidoVentaDet> findAll(Pageable pageable);

    @Query("SELECT pcd FROM PedidoVentaDet pcd WHERE pcd.pedidoVenta.idPedidoVenta = :idPedidoVenta")
    Optional<PedidoVentaDet> findByIdPedidoVenta(@Param("idPedidoVenta") Long idPedidoVenta);

    @Query("SELECT pcd.valorVentaTotal FROM PedidoVentaDet pcd WHERE pcd.pedidoVenta.idPedidoVenta = :idPedidoVenta")
    BigDecimal findValorVentaTotalByIdPedidoVenta(@Param("idPedidoVenta") Long idPedidoVenta);

    @Query("SELECT pcd.pesoNetoTotal FROM PedidoVentaDet pcd WHERE pcd.pedidoVenta.idPedidoVenta = :idPedidoVenta")
    BigDecimal findPesoNetoTotalByIdPedidoVenta(@Param("idPedidoVenta") Long idPedidoVenta);
}
