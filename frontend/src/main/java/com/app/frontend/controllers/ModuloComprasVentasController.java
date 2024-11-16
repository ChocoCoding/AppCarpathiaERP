package com.app.frontend.controllers;

import com.app.frontend.DTO.ComprasVentasDTO;
import com.app.frontend.service.ComprasVentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ModuloComprasVentasController {

    @Autowired
    private ComprasVentasService comprasVentasService;

    @GetMapping("/compras-ventas")
    public String mostrarFormularioComprasVentas() {
        return "compras_ventas_form"; // Nombre del template del formulario
    }

    @GetMapping("/procesar-compras-ventas")
    public String procesarComprasVentas(
            @RequestParam("nOperacion") Long nOperacion,
            Model model) {

        ComprasVentasDTO comprasVentasDTO = comprasVentasService.obtenerComprasVentas(nOperacion);

        if (comprasVentasDTO == null || (comprasVentasDTO.getPedidoCompra() == null
                && (comprasVentasDTO.getPedidosVenta() == null || comprasVentasDTO.getPedidosVenta().isEmpty()))) {
            model.addAttribute("mensaje", "El número de operación no tiene compras y ventas ligadas.");
            return "compras_ventas_result"; // Nombre del template para mostrar el mensaje
        }

        model.addAttribute("comprasVentas", comprasVentasDTO);
        return "compras_ventas_result"; // Nombre del template para mostrar los datos
    }
}
