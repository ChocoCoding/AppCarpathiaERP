package com.app.microservicio.ventas.repository;

import com.app.microservicio.ventas.entities.LineaPedidoVenta;
import com.app.microservicio.ventas.entities.PedidoVenta;
import com.app.microservicio.ventas.services.LineaPedidoVentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LineaPedidoVentaRepository extends JpaRepository<LineaPedidoVenta,Long>, JpaSpecificationExecutor<LineaPedidoVenta> {
    Page<LineaPedidoVenta> findAll(Pageable pageable);

    @Query("SELECT l FROM LineaPedidoVenta l WHERE l.pedidoVenta.idPedidoVenta =: idPedidoVenta")
    List<LineaPedidoVenta> findByIdPedidoVenta(@Param("idPedidoVenta") Long idPedidoVenta);

    @Query("SELECT SUM(l.pNeto) FROM LineaPedidoVenta l WHERE l.pedidoVenta.idPedidoVenta = :pedidoVentaId")
    BigDecimal sumPesoNetoByPedidoVentaId(@Param("pedidoVentaId") Long pedidoVentaId);

    @Query("SELECT SUM(lp.bultos) FROM LineaPedidoVenta lp WHERE lp.pedidoVenta.idPedidoVenta = :idPedidoVenta")
    Long sumBultosByPedidoVentaId(@Param("idPedidoVenta") Long idPedidoVenta);

    @Query("SELECT SUM(lp.valorVenta) FROM LineaPedidoVenta lp WHERE lp.pedidoVenta.idPedidoVenta = :idPedidoVenta")
    BigDecimal sumValorVentaByPedidoVentaId(@Param("idPedidoVenta") Long idPedidoVenta);

    @Query("SELECT SUM(lp.precio) FROM LineaPedidoVenta lp WHERE lp.pedidoVenta.idPedidoVenta = :idPedidoVenta")
    BigDecimal sumPrecioVentaByPedidoVentaId(@Param("idPedidoVenta") Long idPedidoVenta);
}
