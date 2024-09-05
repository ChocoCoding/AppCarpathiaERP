package com.app.frontend.service;

import com.app.frontend.DTO.PedidoCompraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class PedidoCompraService {

    @Autowired
    private MessageSource messageSource;

    private final RestTemplate restTemplate;

    @Value("${api.url.getPedidosCompra}")
    private String getPedidosCompra;

    @Autowired
    public PedidoCompraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PedidoCompraDTO> obtenerTodosLosPedidosCompra() {
        String url = getPedidosCompra;  // URL del endpoint en el backend
        PedidoCompraDTO[] pedidos = restTemplate.getForObject(url, PedidoCompraDTO[].class);
        return Arrays.asList(pedidos);
    }
}
