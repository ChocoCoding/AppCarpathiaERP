package com.microservicio.compras_ventas.services;


import com.microservicio.compras_ventas.client.PedidoCompraClient;
import com.microservicio.compras_ventas.client.PedidoVentaClient;
import com.microservicio.compras_ventas.dto.ComprasVentasDTO;
import com.microservicio.compras_ventas.dto.PedidoCompraDTO;
import com.microservicio.compras_ventas.dto.PedidoVentaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComprasVentasService {

    @Autowired
    private PedidoCompraClient pedidoCompraClient;

    @Autowired
    private PedidoVentaClient pedidoVentaClient;

    /**
     * Obtiene la información de compras y ventas asociadas a un nOperacion.
     *
     * @param nOperacion Número de operación para filtrar.
     * @return Objeto ComprasVentasDTO con los detalles combinados.
     */
    public ComprasVentasDTO getComprasVentasByNOperacion(Long nOperacion) {
        // Obtener PedidoCompraDTO (puede ser null si el fallback retorna Optional.empty())
        PedidoCompraDTO pedidoCompraDTO = pedidoCompraClient.obtenerPedidoCompraPorOperacion(nOperacion).orElse(null);

        // Obtener lista de PedidoVentaDTO (puede estar vacía si el fallback lo retorna así)
        List<PedidoVentaDTO> pedidosVentaDTO = pedidoVentaClient.obtenerPedidosVentaPorOperacion(nOperacion);

        return new ComprasVentasDTO(pedidoCompraDTO, pedidosVentaDTO);
    }
}
