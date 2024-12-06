package com.app.microservicio.compras.controllers;


import com.app.microservicio.compras.DTO.CostesDTO;
import com.app.microservicio.compras.services.CostePedidoService;
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
@RequestMapping("/api/compras/costes_compra")
@CrossOrigin(origins = "http://localhost:8708")
public class CostePedidoCompraController {
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
    public ResponseEntity<Optional<CostesDTO>> obtenerCoste(@PathVariable Long idPedidoCompra) {
        return ResponseEntity.of(Optional.ofNullable(costePedidoService.obtenerCostePedidoCompra(idPedidoCompra)));
    }

    // Obtener todos los costes
    @GetMapping
    public ResponseEntity<Page<CostesDTO>> listarCostesCompra(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pedidoCompra.idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> searchFields
    ) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CostesDTO> costesPage = costePedidoService.listarCostes(pageable, search, searchFields);
        return ResponseEntity.ok(costesPage);
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

