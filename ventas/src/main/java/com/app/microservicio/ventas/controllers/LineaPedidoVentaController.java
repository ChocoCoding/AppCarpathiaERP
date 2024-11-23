package com.app.microservicio.ventas.controllers;


import com.app.microservicio.ventas.dto.LineaPedidoVentaDTO;
import com.app.microservicio.ventas.services.CalculoService;
import com.app.microservicio.ventas.services.LineaPedidoVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas/lineas_pedidos_venta")
@CrossOrigin(origins = "http://localhost:8708")
public class LineaPedidoVentaController {

    @Autowired
    private LineaPedidoVentaService lineaPedidoVentaService;

    @Autowired
    private CalculoService calculoService;

    @GetMapping
    public ResponseEntity<Page<LineaPedidoVentaDTO>> listarLineasPedidoVenta(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) String cliente,
            @RequestParam(defaultValue = "pedidoVenta.idPedidoVenta") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> searchFields
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LineaPedidoVentaDTO> lineasPedidosPage = lineaPedidoVentaService.listarLineasPedidoVenta(pageable, proveedor, cliente, search, searchFields);
        return ResponseEntity.ok(lineasPedidosPage);
    }

    @GetMapping("/{idPedidoVenta}")
    public ResponseEntity<List<LineaPedidoVentaDTO>> getLineasByPedidoVenta(@PathVariable Long idPedidoVenta) {
        List<LineaPedidoVentaDTO> lineas = lineaPedidoVentaService.getLineasByPedidoVenta(idPedidoVenta);
        return ResponseEntity.ok(lineas);
    }

    @PostMapping
    public ResponseEntity<LineaPedidoVentaDTO> crearLineaPedido(@RequestBody LineaPedidoVentaDTO lineaPedidoVentaDTO) {
        LineaPedidoVentaDTO nuevaLinea = lineaPedidoVentaService.crearLineaPedido(lineaPedidoVentaDTO);
        calculoService.calcularValorVentaTotalLinea(nuevaLinea.getIdLineaPedidoVenta());
        calculoService.recalcularPesoNetoTotal(nuevaLinea.getIdPedidoVenta());
        calculoService.recalcularTotalBultos(nuevaLinea.getIdPedidoVenta());
        calculoService.recalcularValoresVenta(nuevaLinea.getIdPedidoVenta());
        calculoService.recalcularPromedio(nuevaLinea.getIdPedidoVenta());
        calculoService.recalcularPrecioTotalVenta(nuevaLinea.getIdPedidoVenta());
        return ResponseEntity.ok(nuevaLinea);
    }

    @PutMapping("/{idNumeroLinea}")
    public ResponseEntity<LineaPedidoVentaDTO> actualizarLineaPedido(@PathVariable Long idNumeroLinea, @RequestBody LineaPedidoVentaDTO lineaPedidoVentaDTO) {
        LineaPedidoVentaDTO lineaActualizada = lineaPedidoVentaService.actualizarLineaPedido(idNumeroLinea, lineaPedidoVentaDTO);
        return ResponseEntity.ok(lineaActualizada);
    }

    @DeleteMapping("/{idNumeroLinea}")
    public ResponseEntity<Void> eliminarLineaPedido(@PathVariable Long idNumeroLinea) {
        lineaPedidoVentaService.eliminarLineaPedido(idNumeroLinea);
        return ResponseEntity.noContent().build();
    }
}
