package com.app.frontend.service;

import com.app.frontend.DTO.CostesDTO;
import com.app.frontend.DTO.PedidoCompraDetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class CostesPedidoCompraService {


    private final RestTemplate restTemplate;
    @Autowired
    public CostesPedidoCompraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.getCostesPedidoCompra}")
    private String getCostesPedidoCompra;

    public List<CostesDTO> obtenerTodosLosCostesPedidoCompra() {
        String url = getCostesPedidoCompra;  // URL del endpoint en el backend
        CostesDTO[] costes = restTemplate.getForObject(url, CostesDTO[].class);
        return Arrays.asList(costes);
    }
}