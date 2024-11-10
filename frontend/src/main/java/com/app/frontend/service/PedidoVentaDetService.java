package com.app.frontend.service;

import com.app.frontend.DTO.PagedResponseDTO;
import com.app.frontend.DTO.PedidoVentaDetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PedidoVentaDetService {


    private final RestTemplate restTemplate;
    @Autowired
    public PedidoVentaDetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.getPedidoVentaDet}")
    private String getPedidoVentaDet;

    public PagedResponseDTO<PedidoVentaDetDTO> obtenerPedidosVentaDet(int page, int size, String sortBy, String sortDir) {
        String url = getPedidoVentaDet + "?page=" + page + "&size=" + size +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<PedidoVentaDetDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<PedidoVentaDetDTO>>() {}
        );

        return response.getBody();
    }
}