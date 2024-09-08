package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.DTO.LineaPedidoCompraDTO;
import com.app.microservicio.compras.services.CalculoService;
import com.app.microservicio.compras.services.LineaPedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping()
    public ResponseEntity<List<LineaPedidoCompraDTO>> obtenerTodasLasLineasPedidoCompra() {
        List<LineaPedidoCompraDTO> lineasPedido = lineaPedidoCompraService.obtenerTodasLasLineasPedidoCompra();
        return ResponseEntity.ok(lineasPedido);
    }

    @GetMapping("/{idPedidoCompra}")
    public ResponseEntity<List<LineaPedidoCompraDTO>> getLineasByPedidoCompra(@PathVariable Long idPedidoCompra) {
        List<LineaPedidoCompraDTO> lineas = lineaPedidoCompraService.getLineasByPedidoCompra(idPedidoCompra);
        return ResponseEntity.ok(lineas);
    }

    @PostMapping
    public ResponseEntity<LineaPedidoCompraDTO> crearLineaPedido(@RequestBody LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        LineaPedidoCompraDTO nuevaLinea = lineaPedidoCompraService.crearLineaPedido(lineaPedidoCompraDTO);
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
