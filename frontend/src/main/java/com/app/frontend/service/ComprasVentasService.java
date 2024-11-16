package com.app.frontend.service;

import com.app.frontend.DTO.ComprasVentasDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ComprasVentasService {

    private final RestTemplate restTemplate;

    @Autowired
    public ComprasVentasService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.compras-ventas}")
    private String comprasVentasUrl;

    public ComprasVentasDTO obtenerComprasVentas(Long nOperacion) {
        String url = comprasVentasUrl + "?nOperacion=" + nOperacion;

        ResponseEntity<ComprasVentasDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                ComprasVentasDTO.class
        );

        return response.getBody();
    }
}
