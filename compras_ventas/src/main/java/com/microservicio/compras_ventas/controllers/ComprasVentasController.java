package com.microservicio.compras_ventas.controllers;

import com.microservicio.compras_ventas.dto.ComprasVentasDTO;
import com.microservicio.compras_ventas.services.ComprasVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/compras_ventas")
@CrossOrigin(origins = "http://localhost:8708") // Ajusta el origen según tu configuración
public class ComprasVentasController {

    @Autowired
    private ComprasVentasService comprasVentasService;

    /**
     * Endpoint para obtener información de Compras y Ventas filtrada por nOperacion.
     *
     * @param nOperacion Número de operación para filtrar los pedidos.
     * @return Objeto ComprasVentasDTO con detalles de la compra y lista de ventas.
     */
    @GetMapping
    public ResponseEntity<ComprasVentasDTO> getComprasVentasByNOperacion(
            @RequestParam("nOperacion") @Positive(message = "El número de operación debe ser positivo.") Long nOperacion
    ) {
        ComprasVentasDTO comprasVentasDTO = comprasVentasService.getComprasVentasByNOperacion(nOperacion);
        if (comprasVentasDTO.getPedidoCompra() == null && (comprasVentasDTO.getPedidosVenta() == null || comprasVentasDTO.getPedidosVenta().isEmpty())) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(comprasVentasDTO); // 200 OK
    }
}

