package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.DTO.LineaPedidoCompraDTO;
import com.app.microservicio.compras.DTO.PedidoCompraDTO;
import com.app.microservicio.compras.services.CalculoService;
import com.app.microservicio.compras.services.LineaPedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras/lineas_pedidos_compra")
@CrossOrigin(origins = "http://localhost:8708")
public class LineaPedidoCompraController {

    @Autowired
    private LineaPedidoCompraService lineaPedidoCompraService;

    @Autowired
    private CalculoService calculoService;

    @GetMapping
    public ResponseEntity<Page<LineaPedidoCompraDTO>> listarLineasPedidoCompra(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) String cliente,
            @RequestParam(defaultValue = "pedidoCompra.idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> searchFields
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LineaPedidoCompraDTO> lineasPedidosPage = lineaPedidoCompraService.listarLineasPedidoCompra(pageable, proveedor, cliente, search, searchFields);
        return ResponseEntity.ok(lineasPedidosPage);
    }

    @GetMapping("/{idPedidoCompra}")
    public ResponseEntity<List<LineaPedidoCompraDTO>> getLineasByPedidoCompra(@PathVariable Long idPedidoCompra) {
        List<LineaPedidoCompraDTO> lineas = lineaPedidoCompraService.getLineasByPedidoCompra(idPedidoCompra);
        return ResponseEntity.ok(lineas);
    }

    @PostMapping
    public ResponseEntity<LineaPedidoCompraDTO> crearLineaPedido(@RequestBody LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        LineaPedidoCompraDTO nuevaLinea = lineaPedidoCompraService.crearLineaPedido(lineaPedidoCompraDTO);
        calculoService.calcularValorCompraTotalLinea(nuevaLinea.getIdNumeroLinea());
        calculoService.recalcularPesoNetoTotal(nuevaLinea.getIdPedidoCompra());
        calculoService.recalcularTotalBultos(nuevaLinea.getIdPedidoCompra());
        calculoService.recalcularValoresCompra(nuevaLinea.getIdPedidoCompra());
        calculoService.recalcularPromedio(nuevaLinea.getIdPedidoCompra());
        return ResponseEntity.ok(nuevaLinea);
    }

    @PutMapping("/{idNumeroLinea}")
    public ResponseEntity<LineaPedidoCompraDTO> actualizarLineaPedido(@PathVariable Long idNumeroLinea, @RequestBody LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        LineaPedidoCompraDTO lineaActualizada = lineaPedidoCompraService.actualizarLineaPedido(idNumeroLinea, lineaPedidoCompraDTO);
        return ResponseEntity.ok(lineaActualizada);
    }

    @DeleteMapping("/{idNumeroLinea}")
    public ResponseEntity<Void> eliminarLineaPedido(@PathVariable Long idNumeroLinea) {
        lineaPedidoCompraService.eliminarLineaPedido(idNumeroLinea);
        return ResponseEntity.noContent().build();
    }


}
