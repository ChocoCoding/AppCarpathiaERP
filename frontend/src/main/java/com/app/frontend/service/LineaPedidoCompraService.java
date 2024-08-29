package com.app.frontend.service;

import com.app.frontend.DTO.LineaPedidoCompraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class LineaPedidoCompraService {


    private final RestTemplate restTemplate;

    @Autowired
    public LineaPedidoCompraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LineaPedidoCompraDTO> obtenerTodasLasLineasPedidoCompra() {
        String url = "http://localhost:8702/api/compras/lineas_pedidos_compra";  // URL del endpoint en el backend
        LineaPedidoCompraDTO[] lineas = restTemplate.getForObject(url, LineaPedidoCompraDTO[].class);
        return Arrays.asList(lineas);
    }
}