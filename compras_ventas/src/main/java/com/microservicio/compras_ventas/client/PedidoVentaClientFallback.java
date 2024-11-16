package com.microservicio.compras_ventas.client;
import com.microservicio.compras_ventas.dto.PedidoVentaDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PedidoVentaClientFallback implements PedidoVentaClient {

    @Override
    public List<PedidoVentaDTO> obtenerPedidosVentaPorOperacion(Long nOperacion) {
        // Lógica de fallback: Puedes retornar una lista vacía o valores por defecto
        System.err.println("Ventas Service no disponible. Fallback activado para nOperacion: " + nOperacion);
        return Collections.emptyList();
    }
}
