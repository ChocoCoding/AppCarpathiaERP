package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.services.LineaPedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.microservicio.compras.DTO.LineaPedidoCompraDTO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras/lineas_pedidos_compra")
public class LineaPedidoCompraController {

    @Autowired
    private LineaPedidoCompraService lineaPedidoCompraService;

    @GetMapping
    public ResponseEntity<List<LineaPedidoCompraDTO>> listarLineasPedidosCompra() {
        return ResponseEntity.ok(lineaPedidoCompraService.listarLineasPedidosCompra());
    }

    @GetMapping("/{idPedidoCompra}/{n_linea}")
    public ResponseEntity<Optional<LineaPedidoCompraDTO>> obtenerLineaPedidoCompra(
            @PathVariable Long idPedidoCompra, @PathVariable Long n_linea) {
        return ResponseEntity.of(Optional.ofNullable(lineaPedidoCompraService.obtenerLineaPedidoCompra(idPedidoCompra, n_linea)));
    }

    @GetMapping("/lineas_por_pedido/{idPedidoCompra}")
    public ResponseEntity<List<LineaPedidoCompraDTO>> listarLineasPedidoCompra(@PathVariable Long idPedidoCompra) {
        return ResponseEntity.ok(lineaPedidoCompraService.listarLineasPedidoCompra(idPedidoCompra));
    }

    @PostMapping("/crear")
    public ResponseEntity<LineaPedidoCompraDTO> crearLineaPedidoCompra(@RequestBody LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        return ResponseEntity.ok(lineaPedidoCompraService.guardarLineaPedidoCompra(lineaPedidoCompraDTO));
    }

    @DeleteMapping("/{idPedidoCompra}/{nLinea}")
    public ResponseEntity<Void> eliminarLineaPedidoCompra(
            @PathVariable Long idPedidoCompra, @PathVariable Long nLinea) {
        lineaPedidoCompraService.eliminarLineaPedidoCompra(idPedidoCompra, nLinea);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idPedidoCompra}/{nLinea}")
    public ResponseEntity<LineaPedidoCompraDTO> actualizarLineaPedidoCompra(
            @PathVariable Long idPedidoCompra,
            @PathVariable Long nLinea,
            @RequestBody LineaPedidoCompraDTO lineaPedidoCompraDTO) {

        LineaPedidoCompraDTO updatedLinea = lineaPedidoCompraService.actualizarLineaPedidoCompra(idPedidoCompra, nLinea, lineaPedidoCompraDTO);
        return ResponseEntity.ok(updatedLinea);
    }
}

