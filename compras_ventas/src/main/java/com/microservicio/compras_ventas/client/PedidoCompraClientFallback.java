package com.microservicio.compras_ventas.client;

import com.microservicio.compras_ventas.dto.PedidoCompraDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PedidoCompraClientFallback implements PedidoCompraClient {

    @Override
    public Optional<PedidoCompraDTO> obtenerPedidoCompraPorOperacion(Long nOperacion) {
        // Lógica de fallback: Puedes retornar un valor por defecto o null
        // Aquí retornamos Optional.empty()
        System.err.println("Compras Service no disponible. Fallback activado para nOperacion: " + nOperacion);
        return Optional.empty();
    }
}

