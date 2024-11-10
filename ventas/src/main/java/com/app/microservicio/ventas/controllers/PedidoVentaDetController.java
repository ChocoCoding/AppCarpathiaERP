package com.app.microservicio.ventas.controllers;

import com.app.microservicio.ventas.dto.PedidoVentaDetDTO;
import com.app.microservicio.ventas.services.PedidoVentaDetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas/pedidos_venta_det")
@CrossOrigin(origins = "http://localhost:8708")
public class PedidoVentaDetController {

    @Autowired
    private PedidoVentaDetService pedidoVentaDetService;

    // Listar detalles de pedido de compra con paginación, ordenamiento y búsqueda
    @GetMapping
    public ResponseEntity<Page<PedidoVentaDetDTO>> listarPedidosVentaDet(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pedidoVenta.idPedidoVenta") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> searchFields
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PedidoVentaDetDTO> pedidosVentaDetPage = pedidoVentaDetService.listarPedidosVentaDet(pageable, search, searchFields);
        return ResponseEntity.ok(pedidosVentaDetPage);
    }

    @GetMapping("/{idPedidoVenta}")
    public ResponseEntity<Optional<PedidoVentaDetDTO>> obtenerPedidoVentaDet(@PathVariable Long idPedidoVenta) {
        return ResponseEntity.of(Optional.ofNullable(pedidoVentaDetService.obtenerPedidoVentaDet(idPedidoVenta)));
    }

    @PostMapping()
    public ResponseEntity<PedidoVentaDetDTO> crearPedidoVentaDet(@RequestBody PedidoVentaDetDTO pedidoVentaDetDTO) {
        System.out.println("id:" + pedidoVentaDetDTO.getIdPedidoVenta());
        PedidoVentaDetDTO nuevoDetallePedido = pedidoVentaDetService.crearPedidoVentaDet(pedidoVentaDetDTO);

        return ResponseEntity.ok(nuevoDetallePedido);
    }

    // Endpoint para actualizar PedidoVentaDet
    @PutMapping("/{id}")
    public ResponseEntity<PedidoVentaDetDTO> actualizarPedidoVentaDet(@PathVariable Long id, @RequestBody PedidoVentaDetDTO pedidoVentaDetDTO) {
        PedidoVentaDetDTO actualizado = pedidoVentaDetService.actualizarPedidoVentaDet(id, pedidoVentaDetDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{idPedidoVentaDet}")
    public ResponseEntity<Void> eliminarPedidoDet(@PathVariable Long idPedidoVentaDet) {
        pedidoVentaDetService.eliminarPedidoVentaDet(idPedidoVentaDet);
        return ResponseEntity.noContent().build();
    }
}
