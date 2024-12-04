package com.app.microservicio.compras.repository;

import com.app.microservicio.compras.entities.DatosBarcoPedidoCompra;
import com.app.microservicio.compras.entities.LineaPedidoCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DatosBarcoRepository extends JpaRepository<DatosBarcoPedidoCompra, Long>, JpaSpecificationExecutor<DatosBarcoPedidoCompra> {

    Page<DatosBarcoPedidoCompra> findAll(Pageable pageable);

    @Query("SELECT db FROM DatosBarcoPedidoCompra db WHERE db.pedidoCompra.idPedidoCompra = :idPedidoCompra")
    Optional<DatosBarcoPedidoCompra> findByIdPedidoCompra(@Param("idPedidoCompra") Long idPedidoCompra);
}
