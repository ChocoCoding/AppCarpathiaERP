package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.PedidoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.app.microservicio.compras.entities.PedidoCompraDet;

import java.util.Optional;

@Repository
public interface PedidoCompraDetRepository extends JpaRepository<PedidoCompraDet, Long> {
void deleteByidPedidoCompraDet(Long idPedidoCompraDet);

    @Query("SELECT pcd FROM PedidoCompraDet pcd WHERE pcd.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    Optional<PedidoCompraDet> findByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);

}