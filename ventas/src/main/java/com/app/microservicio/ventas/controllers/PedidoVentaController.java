package com.app.microservicio.ventas.controllers;

import com.app.microservicio.ventas.dto.PedidoVentaDTO;
import com.app.microservicio.ventas.services.PedidoVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas/pedidos_venta")
@CrossOrigin(origins = "http://localhost:8708")
public class PedidoVentaController {

    @Autowired
    private PedidoVentaService pedidoVentaService;

    @GetMapping
    public ResponseEntity<Page<PedidoVentaDTO>> listarPedidosVenta(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idPedidoVenta") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> searchFields
    ) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PedidoVentaDTO> pedidosPage = pedidoVentaService.listarPedidosVenta(pageable, search, searchFields);
        return ResponseEntity.ok(pedidosPage);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Optional<PedidoVentaDTO>> obtenerPedidoVenta(@PathVariable Long id) {
        return ResponseEntity.of(Optional.ofNullable(pedidoVentaService.obtenerPedidoVenta(id)));
    }
    
    @CacheEvict(value = "pedidosVenta", allEntries = true)
    @PostMapping()
    public ResponseEntity<PedidoVentaDTO> crearPedidoVenta(@RequestBody PedidoVentaDTO pedidoVentaDTO) {
        PedidoVentaDTO nuevoPedido = pedidoVentaService.guardarPedidoVenta(pedidoVentaDTO);
        return ResponseEntity.ok(nuevoPedido);
    }

    @CacheEvict(value = "pedidosVenta", allEntries = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedidoVenta(@PathVariable Long id) {
        boolean eliminado = pedidoVentaService.eliminarPedidoVenta(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CacheEvict(value = "pedidosVenta", allEntries = true)
    @PutMapping("/{id}")
    public ResponseEntity<PedidoVentaDTO> actualizarPedidoVenta(@PathVariable Long id, @RequestBody PedidoVentaDTO pedidoActualizado) {
        PedidoVentaDTO pedido = pedidoVentaService.actualizarPedidoVenta(id, pedidoActualizado);
        if (pedido != null) {
            return new ResponseEntity<>(pedido, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoVentaDTO> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (!body.containsKey("status")) {
            return ResponseEntity.badRequest().body(null);
        }

        String statusStr = body.get("status");
        if (statusStr == null || !(statusStr.equals("P") || statusStr.equals("T"))) {
            return ResponseEntity.badRequest().body(null);
        }

        char status = statusStr.charAt(0);
        PedidoVentaDTO actualizado = pedidoVentaService.cambiarEstadoPedidoVenta(id, status);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{idPedidoVenta}/exists")
    public ResponseEntity<Map<String, Boolean>> existePedidoVenta(@PathVariable Long idPedidoVenta) {
        boolean existe = pedidoVentaService.existePedidoVenta(idPedidoVenta);
        return ResponseEntity.ok(Collections.singletonMap("existe", existe));
    }

    @GetMapping("/nOperacion/{nOperacion}")
    public ResponseEntity<List<PedidoVentaDTO>> obtenerPedidosVentaPorOperacion(@PathVariable Long nOperacion) {
        List<PedidoVentaDTO> pedidosVenta = pedidoVentaService.obtenerPedidosVentaPorOperacion(nOperacion);
        if (pedidosVenta.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(pedidosVenta); // 200 OK
    }

}
