package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.services.PedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.microservicio.compras.DTO.PedidoCompraDTO;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras/pedidos_compra")
@CrossOrigin(origins = "http://localhost:8708")
public class PedidoCompraController {

    @Autowired
    private PedidoCompraService pedidoCompraService;

    @GetMapping
    public ResponseEntity<Page<PedidoCompraDTO>> listarPedidosCompra(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> searchFields
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PedidoCompraDTO> pedidosPage = pedidoCompraService.listarPedidosCompra(pageable, search, searchFields);
        return ResponseEntity.ok(pedidosPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PedidoCompraDTO>> obtenerPedidoCompra(@PathVariable Long id) {
        return ResponseEntity.of(Optional.ofNullable(pedidoCompraService.obtenerPedidoCompra(id)));
    }

    @CacheEvict(value = "pedidosCompra", allEntries = true)
    @PostMapping()
    public ResponseEntity<PedidoCompraDTO> crearPedidoCompra(@RequestBody PedidoCompraDTO pedidoCompraDTO) {
        PedidoCompraDTO nuevoPedido = pedidoCompraService.guardarPedidoCompra(pedidoCompraDTO);
        return ResponseEntity.ok(nuevoPedido);
    }

    @CacheEvict(value = "pedidosCompra", allEntries = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedidoCompra(@PathVariable Long id) {
        boolean eliminado = pedidoCompraService.eliminarPedidoCompra(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CacheEvict(value = "pedidosCompra", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<PedidoCompraDTO> actualizarPedidoCompra(@PathVariable Long id, @RequestBody PedidoCompraDTO pedidoActualizado) {
        PedidoCompraDTO pedido = pedidoCompraService.actualizarPedidoCompra(id, pedidoActualizado);
        if (pedido != null) {
            return new ResponseEntity<>(pedido, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoCompraDTO> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (!body.containsKey("status")) {
            return ResponseEntity.badRequest().body(null);
        }

        String statusStr = body.get("status");
        if (statusStr == null || !(statusStr.equals("P") || statusStr.equals("T"))) {
            return ResponseEntity.badRequest().body(null);
        }

        char status = statusStr.charAt(0);
        PedidoCompraDTO actualizado = pedidoCompraService.cambiarEstadoPedidoCompra(id, status);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{idPedidoCompra}/exists")
    public ResponseEntity<Map<String, Boolean>> existePedidoCompra(@PathVariable Long idPedidoCompra) {
        boolean existe = pedidoCompraService.existePedidoCompra(idPedidoCompra);
        return ResponseEntity.ok(Collections.singletonMap("existe", existe));
    }

    @GetMapping("/nOperacion/{nOperacion}")
    public ResponseEntity<Optional<PedidoCompraDTO>> obtenerPedidoCompraPorOperacion(@PathVariable Long nOperacion){
            return ResponseEntity.of(Optional.ofNullable(pedidoCompraService.obtenerPedidoCompraPorOperacion(nOperacion)));

    }

}

