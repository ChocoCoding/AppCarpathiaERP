package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.DTO.DatosBarcoDTO;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;
import com.app.microservicio.compras.services.DatosBarcoPedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping()
    public ResponseEntity<List<DatosBarcoDTO>> listarDatosBarco(){
        return ResponseEntity.ok(datosBarcoPedidoCompraService.listarDatosBarco());
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
