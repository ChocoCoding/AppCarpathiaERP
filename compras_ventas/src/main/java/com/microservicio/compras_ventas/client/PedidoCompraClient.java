package com.microservicio.compras_ventas.client;

import com.microservicio.compras_ventas.dto.PedidoCompraDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Importa Resilience4j Feign Fallback

import java.util.Optional;

@FeignClient(
        name = "compras-service",
        url = "${microservicio.compras.url}",
        fallback = PedidoCompraClientFallback.class
)
public interface PedidoCompraClient {

    @GetMapping("/nOperacion/{nOperacion}")
    Optional<PedidoCompraDTO> obtenerPedidoCompraPorOperacion(@PathVariable("nOperacion") Long nOperacion);
}
