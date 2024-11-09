package com.app.frontend.service;

import com.app.frontend.DTO.DatosBarcoDTO;
import com.app.frontend.DTO.PagedResponseDTO;
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
public class DatosBarcoPedidoCompraService {
    private final RestTemplate restTemplate;
    @Autowired
    public DatosBarcoPedidoCompraService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.getDatosBarcoPedidoCompra}")
    private String getDatosBarcoPedidoCompra;



    public PagedResponseDTO<DatosBarcoDTO> obtenerTodosLosDatosBarcoPedidoCompra(int page, int size, String sortBy, String sortDir) {
        String url = getDatosBarcoPedidoCompra + "?page=" + page + "&size=" + size +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<DatosBarcoDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<DatosBarcoDTO>>() {}
        );

        return response.getBody();
    }

}
