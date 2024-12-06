package com.app.frontend.controllers;

import com.app.frontend.DTO.*;
import com.app.frontend.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ModuloVentasController {

    @Autowired
    private PedidoVentaService pedidoVentaService;

    @Autowired
    private LineaPedidoVentaService lineaPedidoVentaService;

    @Autowired
    private PedidoVentaDetService pedidoVentaDetService;


    // Inyectar las URLs desde el archivo application.properties
    @Value("${modulo.ventas.url.pedidos-venta}")
    private String pedidosVentaUrl;

    @Value("${modulo.ventas.url.lineas-pedido-venta}")
    private String lineasPedidoVentaUrl;

    @Value("${modulo.ventas.model.pedidos-venta}")
    private String pedidosVentaModel;

    @Value("${modulo.ventas.model.lineas-pedido-venta}")
    private String lineasPedidoVentaModel;

    @Value("${modulo.ventas.view.lineas-pedido-venta}")
    private String lineasPedidoVentaView;

    @Value("${modulo.ventas.view.pedidos-venta}")
    private String pedidosVentaView;

    @Value("${modulo.ventas.url.detalles-pedido-venta}")
    private String detallesPedidoVentaUrl;

    @Value("${modulo.ventas.model.detalles-pedido-venta}")
    private String detallesPedidoVentaModel;

    @Value("${modulo.ventas.view.detalles-pedido-venta}")
    private String detallesPedidoVentaView;

    @GetMapping("/pedidos-venta")
    public String gestionarPedidosVenta(
            Model model,
            @RequestParam(defaultValue = "1") int page, // uno-based
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idPedidoVenta") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Convertir 'page' a basado en cero para Spring Data
        int backendPage = page - 1;
        if (backendPage < 0) backendPage = 0;

        PagedResponseDTO<PedidoVentaDTO> pedidosPage = pedidoVentaService.obtenerPedidosVenta(backendPage, size, sortBy, sortDir);
        List<PedidoVentaDTO> pedidosVenta = pedidosPage.getContent();

        model.addAttribute("pedidosVenta", pedidosVenta);
        model.addAttribute("currentPage", page); // uno-based
        model.addAttribute("totalPages", pedidosPage.getTotalPages());
        model.addAttribute("totalElements", pedidosPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("desc") ? "asc" : "desc");

        return "pedidos_venta";
    }

    @GetMapping("/lineas-pedido-venta")
    public String gestionarLineasPedidoVenta(
            Model model,
            @RequestParam(defaultValue = "1") int page, // uno-based
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) String cliente,
            @RequestParam(defaultValue = "pedidoVenta.idPedidoVenta") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        // Convertir 'page' a basado en cero para Spring Data
        int backendPage = page - 1;
        if (backendPage < 0) backendPage = 0;

        PagedResponseDTO<LineaPedidoVentaDTO> lineasPage = lineaPedidoVentaService.obtenerTodasLasLineasPedidoVenta(backendPage, size, proveedor, cliente, sortBy, sortDir);
        List<LineaPedidoVentaDTO> lineasVenta = lineasPage.getContent();

        model.addAttribute("lineasPedidoVenta", lineasVenta);
        model.addAttribute("currentPage", page); // uno-based
        model.addAttribute("totalPages", lineasPage.getTotalPages());
        model.addAttribute("totalElements", lineasPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("cliente", cliente);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");


        return "lineas_pedido_venta";
    }

    @GetMapping("${modulo.ventas.url.detalles-pedido-venta}")
    public String gestionarDetallesPedidoVenta(
            Model model,
            @RequestParam(defaultValue = "1") int page, // uno-based
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pedidoVenta.idPedidoVenta") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        // Convertir 'page' a basado en cero para Spring Data
        int backendPage = page - 1;
        if (backendPage < 0) backendPage = 0;

        PagedResponseDTO<PedidoVentaDetDTO> lineasPage = pedidoVentaDetService.obtenerPedidosVentaDet(backendPage, size, sortBy, sortDir);
        List<PedidoVentaDetDTO> pedidosVentaDet = lineasPage.getContent();

        model.addAttribute("pedidosVentaDet", pedidosVentaDet);
        model.addAttribute("currentPage", page); // uno-based
        model.addAttribute("totalPages", lineasPage.getTotalPages());
        model.addAttribute("totalElements", lineasPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return detallesPedidoVentaView;
    }

    @GetMapping("/formulario-ventas")
    public String gestionarFormularioVenta(){
        return "formulario_ventas";
    }


}
