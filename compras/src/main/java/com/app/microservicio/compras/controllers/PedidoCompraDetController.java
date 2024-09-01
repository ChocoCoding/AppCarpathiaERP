package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.services.PedidoCompraDetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras/pedidos_compra_det")
public class PedidoCompraDetController {

    @Autowired
    private PedidoCompraDetService pedidoCompraDetService;

    @GetMapping()
    public ResponseEntity<List<PedidoCompraDetDTO>> listarPedidosCompraDet(){
        return ResponseEntity.ok(pedidoCompraDetService.listarPedidosCompraDet());
    }

    @GetMapping("/{idPedidoCompra}")
    public ResponseEntity<Optional<PedidoCompraDetDTO>> obtenerPedidoCompraDet(@PathVariable Long idPedidoCompra) {
        return ResponseEntity.of(Optional.ofNullable(pedidoCompraDetService.obtenerPedidoCompraDet(idPedidoCompra)));
    }

    @PostMapping()
    public ResponseEntity<PedidoCompraDetDTO> crearPedidoCompraDet(@RequestBody PedidoCompraDetDTO pedidoCompraDetDTO) {
        return ResponseEntity.ok(pedidoCompraDetService.crearPedidoCompraDet(pedidoCompraDetDTO));
    }

        // Endpoint para actualizar PedidoCompraDet
        @PutMapping("/{id}")
        public ResponseEntity<PedidoCompraDetDTO> actualizarPedidoCompraDet(
                @PathVariable Long id, @RequestBody PedidoCompraDetDTO pedidoCompraDetDTO) {
            PedidoCompraDetDTO actualizado = pedidoCompraDetService.actualizarPedidoCompraDet(id, pedidoCompraDetDTO);
            return ResponseEntity.ok(actualizado);
        }

    @DeleteMapping("/{idPedidoCompraDet}")
    public ResponseEntity<Void> eliminarPedidoDet(@PathVariable Long idPedidoCompraDet) {
        pedidoCompraDetService.eliminarPedidoCompraDet(idPedidoCompraDet);
        return ResponseEntity.noContent().build();
    }

    }

