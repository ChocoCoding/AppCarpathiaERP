package com.app.frontend.service;

import com.app.frontend.DTO.PedidoCompraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class PedidoCompraService {


    private final RestTemplate restTemplate;
    @Autowired
    public PedidoCompraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PedidoCompraDTO> obtenerTodosLosPedidosCompra() {
        String url = "http://localhost:8702/api/compras/pedidos_compra";  // URL del endpoint en el backend
        PedidoCompraDTO[] pedidos = restTemplate.getForObject(url, PedidoCompraDTO[].class);
        return Arrays.asList(pedidos);
    }
}
