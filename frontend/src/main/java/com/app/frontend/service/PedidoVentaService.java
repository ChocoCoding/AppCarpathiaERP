package com.app.frontend.service;

import com.app.frontend.DTO.PagedResponseDTO;
import com.app.frontend.DTO.PedidoVentaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PedidoVentaService {

    private final RestTemplate restTemplate;

    @Autowired
    public PedidoVentaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${api.url.getPedidosVenta}")
    private String getPedidosVenta;

    public PagedResponseDTO<PedidoVentaDTO> obtenerPedidosVenta(int page, int size, String proveedor,  String sortBy, String sortDir) {
        String url = getPedidosVenta + "?page=" + page + "&size=" + size +
                (proveedor != null ? "&proveedor=" + proveedor : "") +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<PedidoVentaDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<PedidoVentaDTO>>() {}
        );

        return response.getBody();
    }


}
