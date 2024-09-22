package com.app.frontend.service;

import com.app.frontend.DTO.DatosBarcoDTO;
import com.app.frontend.DTO.PedidoCompraDetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public List<DatosBarcoDTO> obtenerTodosLosDatosBarcoPedidoCompra() {
        String url = getDatosBarcoPedidoCompra;  // URL del endpoint en el backend
        DatosBarcoDTO[] datosBarco = restTemplate.getForObject(url, DatosBarcoDTO[].class);
        return Arrays.asList(datosBarco);
    }

}
