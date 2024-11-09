package com.app.frontend.service;

import com.app.frontend.DTO.LineaPedidoCompraDTO;
import com.app.frontend.DTO.PagedResponseDTO;
import com.app.frontend.DTO.PedidoCompraDTO;
import com.app.frontend.DTO.PedidoCompraDetDTO;
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
public class PedidoCompraDetService {


    private final RestTemplate restTemplate;
    @Autowired
    public PedidoCompraDetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.getPedidoCompraDet}")
    private String getPedidoCompraDet;

    public PagedResponseDTO<PedidoCompraDetDTO> obtenerPedidosCompraDet(int page, int size, String sortBy, String sortDir) {
        String url = getPedidoCompraDet + "?page=" + page + "&size=" + size +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<PedidoCompraDetDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<PedidoCompraDetDTO>>() {}
        );

        return response.getBody();
    }
}