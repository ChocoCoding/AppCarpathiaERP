package com.microservicio.compras_ventas.client;

import com.microservicio.compras_ventas.dto.PedidoVentaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "ventas-service",
        url = "${microservicio.ventas.url}",
        fallback = PedidoVentaClientFallback.class
)
public interface PedidoVentaClient {

    @GetMapping("/nOperacion/{nOperacion}")
    List<PedidoVentaDTO> obtenerPedidosVentaPorOperacion(@PathVariable("nOperacion") Long nOperacion);
}
