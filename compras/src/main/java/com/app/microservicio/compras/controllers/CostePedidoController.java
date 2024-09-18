package com.app.microservicio.compras.controllers;


import com.app.microservicio.compras.DTO.CostesDTO;
import com.app.microservicio.compras.services.CostePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras/costes_compra")
@CrossOrigin(origins = "http://localhost:8708")
public class CostePedidoController {
    @Autowired
    private CostePedidoService costePedidoService;

    // Crear nuevo coste
    @PostMapping
    public ResponseEntity<CostesDTO> crearCoste(@RequestBody CostesDTO costesDTO) {
        CostesDTO nuevoCoste = costePedidoService.crearCoste(costesDTO);
        return ResponseEntity.ok(nuevoCoste);
    }

    // Obtener coste por ID
    @GetMapping("/{id}")
    public ResponseEntity<CostesDTO> obtenerCostePorId(@PathVariable Long id) {
        CostesDTO costeDTO = costePedidoService.obtenerCostePorId(id);
        return ResponseEntity.ok(costeDTO);
    }

    // Obtener todos los costes
    @GetMapping
    public ResponseEntity<List<CostesDTO>> obtenerTodosLosCostes() {
        List<CostesDTO> costes = costePedidoService.obtenerTodosLosCostes();
        return ResponseEntity.ok(costes);
    }

    // Actualizar coste
    @PutMapping("/{id}")
    public ResponseEntity<CostesDTO> actualizarCoste(@PathVariable Long id, @RequestBody CostesDTO costesDTO) {
        CostesDTO actualizado = costePedidoService.actualizarCoste(id, costesDTO);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar coste
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCoste(@PathVariable Long id) {
        costePedidoService.eliminarCoste(id);
        return ResponseEntity.noContent().build();
    }
}

