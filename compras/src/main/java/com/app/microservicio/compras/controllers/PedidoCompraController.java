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
@CrossOrigin(origins = "http://localhost:8708")
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

    @PostMapping()
    public ResponseEntity<PedidoCompraDTO> crearPedidoCompra(@RequestBody PedidoCompraDTO pedidoCompraDTO) {
        return ResponseEntity.ok(pedidoCompraService.guardarPedidoCompra(pedidoCompraDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedidoCompra(@PathVariable Long id) {
        boolean eliminado = pedidoCompraService.eliminarPedidoCompra(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoCompraDTO> actualizarPedidoCompra(@PathVariable Long id, @RequestBody PedidoCompraDTO pedidoCompraDTO) {
        PedidoCompraDTO actualizado = pedidoCompraService.actualizarPedidoCompra(id, pedidoCompraDTO);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
