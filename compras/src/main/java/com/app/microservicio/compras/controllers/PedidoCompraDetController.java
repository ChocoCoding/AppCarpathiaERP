package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.services.PedidoCompraDetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras/pedidos_compra_det")
@CrossOrigin(origins = "http://localhost:8708")
public class PedidoCompraDetController {

    @Autowired
    private PedidoCompraDetService pedidoCompraDetService;

    // Listar detalles de pedido de compra con paginación, ordenamiento y búsqueda
    @GetMapping
    public ResponseEntity<Page<PedidoCompraDetDTO>> listarPedidosCompraDet(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pedidoCompra.idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> searchFields
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PedidoCompraDetDTO> pedidosCompraDetPage = pedidoCompraDetService.listarPedidosCompraDet(pageable, search, searchFields);
        return ResponseEntity.ok(pedidosCompraDetPage);
    }

    @GetMapping("/{idPedidoCompra}")
    public ResponseEntity<Optional<PedidoCompraDetDTO>> obtenerPedidoCompraDet(@PathVariable Long idPedidoCompra) {
        return ResponseEntity.of(Optional.ofNullable(pedidoCompraDetService.obtenerPedidoCompraDet(idPedidoCompra)));
    }

    @PostMapping()
    public ResponseEntity<PedidoCompraDetDTO> crearPedidoCompraDet(@RequestBody PedidoCompraDetDTO pedidoCompraDetDTO) {
        System.out.println("id:" + pedidoCompraDetDTO.getIdPedidoCompra());
        PedidoCompraDetDTO nuevoDetallePedido = pedidoCompraDetService.crearPedidoCompraDet(pedidoCompraDetDTO);

        return ResponseEntity.ok(nuevoDetallePedido);
    }

        // Endpoint para actualizar PedidoCompraDet
    @PutMapping("/{id}")
    public ResponseEntity<PedidoCompraDetDTO> actualizarPedidoCompraDet(@PathVariable Long id, @RequestBody PedidoCompraDetDTO pedidoCompraDetDTO) {
        PedidoCompraDetDTO actualizado = pedidoCompraDetService.actualizarPedidoCompraDet(id, pedidoCompraDetDTO);
        return ResponseEntity.ok(actualizado);
        }

    @DeleteMapping("/{idPedidoCompraDet}")
    public ResponseEntity<Void> eliminarPedidoDet(@PathVariable Long idPedidoCompraDet) {
        pedidoCompraDetService.eliminarPedidoCompraDet(idPedidoCompraDet);
        return ResponseEntity.noContent().build();
    }
    }

