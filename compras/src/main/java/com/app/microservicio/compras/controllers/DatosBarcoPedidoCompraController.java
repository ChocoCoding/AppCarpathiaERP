package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.DTO.DatosBarcoDTO;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;
import com.app.microservicio.compras.services.DatosBarcoPedidoCompraService;
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
@RequestMapping("/api/compras/datos_barco")
@CrossOrigin(origins = "http://localhost:8708")
public class DatosBarcoPedidoCompraController {

    @Autowired
    private DatosBarcoPedidoCompraService datosBarcoPedidoCompraService;

    @GetMapping
    public ResponseEntity<Page<DatosBarcoDTO>> listarDatosBarco(
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

        Page<DatosBarcoDTO> datosBarcoPage = datosBarcoPedidoCompraService.listarDatosBarco(pageable, search, searchFields);
        return ResponseEntity.ok(datosBarcoPage);
    }

    @GetMapping("/{idPedidoCompra}")
    public ResponseEntity<Optional<DatosBarcoDTO>> obtenerDatosBarco(@PathVariable Long idPedidoCompra) {
        return ResponseEntity.of(Optional.ofNullable(datosBarcoPedidoCompraService.obtenerDatoBarco(idPedidoCompra)));
    }

    @PostMapping()
    public ResponseEntity<DatosBarcoDTO> crearDatosBarco(@RequestBody DatosBarcoDTO datosBarcoDTO) {
        return ResponseEntity.ok(datosBarcoPedidoCompraService.crearDatosBarco(datosBarcoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DatosBarcoDTO> actualizarDatosBarco(@PathVariable Long id, @RequestBody DatosBarcoDTO datosBarcoDTO) {
        DatosBarcoDTO actualizado = datosBarcoPedidoCompraService.actualizarDatosBarco(id, datosBarcoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{idDatosBarco}")
    public ResponseEntity<Void> eliminarDatosBarco(@PathVariable Long idDatosBarco) {
        datosBarcoPedidoCompraService.eliminarDatosBarco(idDatosBarco);
        return ResponseEntity.noContent().build();
    }
}
