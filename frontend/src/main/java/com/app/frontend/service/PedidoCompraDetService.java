package com.app.frontend.service;

import com.app.frontend.DTO.PedidoCompraDetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class PedidoCompraDetService {


    private final RestTemplate restTemplate;
    @Autowired
    public PedidoCompraDetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PedidoCompraDetDTO> obtenerTodosLosDetallesPedidoCompra() {
        String url = "http://localhost:8702/api/compras/pedidos_compra_det";  // URL del endpoint en el backend
        PedidoCompraDetDTO[] detalles = restTemplate.getForObject(url, PedidoCompraDetDTO[].class);
        return Arrays.asList(detalles);
    }
}