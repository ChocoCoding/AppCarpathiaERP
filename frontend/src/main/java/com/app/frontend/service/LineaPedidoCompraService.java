package com.app.frontend.service;

import com.app.frontend.DTO.LineaPedidoCompraDTO;
import com.app.frontend.DTO.PagedResponseDTO;
import com.app.frontend.DTO.PedidoCompraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

    @Value("${api.url.getLineasPedidoCompra}")
    private String getLineasPedidoCompra;


    public PagedResponseDTO<LineaPedidoCompraDTO> obtenerTodasLasLineasPedidoCompra(int page, int size, String proveedor, String cliente, String sortBy, String sortDir) {
        String url = getLineasPedidoCompra + "?page=" + page + "&size=" + size +
                (proveedor != null ? "&proveedor=" + proveedor : "") +
                (cliente != null ? "&cliente=" + cliente : "") +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<LineaPedidoCompraDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<LineaPedidoCompraDTO>>() {}
        );

        return response.getBody();
    }



}