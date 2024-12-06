package com.app.frontend.service;

import com.app.frontend.DTO.PedidoCompraDTO;
import com.app.frontend.DTO.PagedResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PedidoCompraService {

    private final RestTemplate restTemplate;

    @Autowired
    public PedidoCompraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.getPedidosCompra}")
    private String getPedidosCompra;

    public PagedResponseDTO<PedidoCompraDTO> obtenerPedidosCompra(int page, int size, String sortBy, String sortDir) {
        String url = getPedidosCompra + "?page=" + page + "&size=" + size +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<PedidoCompraDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<PedidoCompraDTO>>() {}
        );

        return response.getBody();
    }


}
