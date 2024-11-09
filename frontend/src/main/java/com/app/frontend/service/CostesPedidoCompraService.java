package com.app.frontend.service;

import com.app.frontend.DTO.CostesDTO;
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

    public PagedResponseDTO<CostesDTO> obtenerTodosLosCostesPedidoCompra(int page, int size, String sortBy, String sortDir) {
        String url = getCostesPedidoCompra + "?page=" + page + "&size=" + size +
                "&sortBy=" + sortBy + "&sortDir=" + sortDir;

        ResponseEntity<PagedResponseDTO<CostesDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<PagedResponseDTO<CostesDTO>>() {}
        );

        return response.getBody();
    }
}