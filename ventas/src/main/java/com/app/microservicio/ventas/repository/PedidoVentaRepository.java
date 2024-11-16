package com.app.microservicio.ventas.repository;

import com.app.microservicio.ventas.entities.PedidoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PedidoVentaRepository extends JpaRepository<PedidoVenta,Long>, JpaSpecificationExecutor<PedidoVenta> {
    Page<PedidoVenta> findAll(Pageable pageable);

    @Query("SELECT pv FROM PedidoVenta pv WHERE nOperacion = :nOperacion")
    List<PedidoVenta> findPedidoVentaByNoperacion(@Param("nOperacion") Long nOperacion);

}
