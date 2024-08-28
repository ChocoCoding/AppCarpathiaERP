package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.services.PedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.microservicio.compras.DTO.PedidoCompraDTO;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras/pedidos_compra")
public class PedidoCompraController {

    @Autowired
    private PedidoCompraService pedidoCompraService;

    @GetMapping
    public ResponseEntity<List<PedidoCompraDTO>> listarPedidosCompra() {
        return ResponseEntity.ok(pedidoCompraService.listarPedidosCompra());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PedidoCompraDTO>> obtenerPedidoCompra(@PathVariable Long id) {
        return ResponseEntity.of(Optional.ofNullable(pedidoCompraService.obtenerPedidoCompra(id)));
    }

    @PostMapping("/crear")
    public ResponseEntity<PedidoCompraDTO> crearPedidoCompra(@RequestBody PedidoCompraDTO pedidoCompraDTO) {
        return ResponseEntity.ok(pedidoCompraService.guardarPedidoCompra(pedidoCompraDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedidoCompra(@PathVariable Long id) {
        pedidoCompraService.eliminarPedidoCompra(id);
        return ResponseEntity.noContent().build();
    }
}
