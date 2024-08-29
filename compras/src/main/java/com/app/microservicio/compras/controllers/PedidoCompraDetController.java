package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.services.PedidoCompraDetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;

import java.util.Optional;

@RestController
@RequestMapping("/api/compras/pedidos_compra_det")
public class PedidoCompraDetController {

    @Autowired
    private PedidoCompraDetService pedidoCompraDetService;

    @GetMapping("/{idPedidoCompra}")
    public ResponseEntity<Optional<PedidoCompraDetDTO>> obtenerPedidoCompraDet(@PathVariable Long idPedidoCompra) {
        return ResponseEntity.of(Optional.ofNullable(pedidoCompraDetService.obtenerPedidoCompraDet(idPedidoCompra)));
    }

    @PostMapping("/crear")
    public ResponseEntity<PedidoCompraDetDTO> crearPedidoCompraDet(@RequestBody PedidoCompraDetDTO pedidoCompraDetDTO) {
        return ResponseEntity.ok(pedidoCompraDetService.guardarPedidoCompraDet(pedidoCompraDetDTO));
    }

        // Endpoint para actualizar PedidoCompraDet
        @PutMapping("/{id}")
        public ResponseEntity<PedidoCompraDet> actualizarPedidoCompraDet(
                @PathVariable Long id, @RequestBody PedidoCompraDetDTO pedidoCompraDetDTO) {
            PedidoCompraDet actualizado = pedidoCompraDetService.actualizarPedidoCompraDet(id, pedidoCompraDetDTO);
            return ResponseEntity.ok(actualizado);
        }


    }

