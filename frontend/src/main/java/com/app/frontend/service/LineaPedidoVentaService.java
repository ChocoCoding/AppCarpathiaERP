package com.app.frontend.service;

import com.app.frontend.DTO.LineaPedidoVentaDTO;
import com.app.frontend.DTO.PagedResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LineaPedidoVentaService {


    private final RestTemplate restTemplate;


    @Autowired
    public LineaPedidoVentaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.getLineasPedidoVenta}")
    private String getLineasPedidoVenta;


    public PagedResponseDTO<LineaPedidoVentaDTO> obtenerTodasLasLineasPedidoVenta(int page, int size, String sortBy, String sortDir) {
        String url = getLineasPedidoVenta + "?page=" + page + "&size=" + size +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<LineaPedidoVentaDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<LineaPedidoVentaDTO>>() {}
        );

        return response.getBody();
    }



}