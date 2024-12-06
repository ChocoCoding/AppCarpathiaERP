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
public class ModuloComprasController {

    @Autowired
    private PedidoCompraService pedidoCompraService;

    @Autowired
    private LineaPedidoCompraService lineaPedidoCompraService;

    @Autowired
    private PedidoCompraDetService pedidoCompraDetService;

    @Autowired
    private CostesPedidoCompraService costesPedidoCompraService;

    @Autowired
    private DatosBarcoPedidoCompraService datosBarcoPedidoCompraService;

    // Inyectar las URLs desde el archivo application.properties
    @Value("${modulo.compras.url.pedidos-compra}")
    private String pedidosCompraUrl;

    @Value("${modulo.compras.url.lineas-pedido-compra}")
    private String lineasPedidoCompraUrl;

    @Value("${modulo.compras.model.pedidos-compra}")
    private String pedidosCompraModel;

    @Value("${modulo.compras.model.lineas-pedido-compra}")
    private String lineasPedidoCompraModel;

    @Value("${modulo.compras.view.lineas-pedido-compra}")
    private String lineasPedidoCompraView;

    @Value("${modulo.compras.view.pedidos-compra}")
    private String pedidosCompraView;

    @Value("${modulo.compras.url.detalles-pedido-compra}")
    private String detallesPedidoCompraUrl;

    @Value("${modulo.compras.model.detalles-pedido-compra}")
    private String detallesPedidoCompraModel;

    @Value("${modulo.compras.view.detalles-pedido-compra}")
    private String detallesPedidoCompraView;



    @Value("${modulo.compras.view.costes-pedido-compra}")
    private String costesPedidoCompraView;

    @Value("${modulo.compras.model.costes-pedido-compra}")
    private String costesPedidoCompraModel;

    @Value("${modulo.compras.url.costes-pedido-compra}")
    private String costesPedidoCompraUrl;


    @Value("${modulo.compras.model.datos-barco-pedido-compra}")
    private String datosBarcoPedidoCompraModel;

    @Value("${modulo.compras.view.datos-barco-pedido-compra}")
    private String datosBarcoPedidoCompraView;

    @Value("${modulo.compras.url.datos-barco-pedido-compra}")
    private String datosBarcoPedidoCompraUrl;



    @GetMapping("/pedidos-compra")
    public String gestionarPedidosCompra(
            Model model,
            @RequestParam(defaultValue = "1") int page, // uno-based
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Convertir 'page' a basado en cero para Spring Data
        int backendPage = page - 1;
        if (backendPage < 0) backendPage = 0;

        PagedResponseDTO<PedidoCompraDTO> pedidosPage = pedidoCompraService.obtenerPedidosCompra(backendPage, size, sortBy, sortDir);
        List<PedidoCompraDTO> pedidosCompra = pedidosPage.getContent();

        model.addAttribute("pedidosCompra", pedidosCompra);
        model.addAttribute("currentPage", page); // uno-based
        model.addAttribute("totalPages", pedidosPage.getTotalPages());
        model.addAttribute("totalElements", pedidosPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("desc") ? "asc" : "desc");

        return "pedidos_compra";
    }

    @GetMapping("/lineas-pedido-compra")
    public String gestionarLineasPedidoCompra(
            Model model,
            @RequestParam(defaultValue = "1") int page, // uno-based
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String proveedor,
            @RequestParam(required = false) String cliente,
            @RequestParam(defaultValue = "pedidoCompra.idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        // Convertir 'page' a basado en cero para Spring Data
        int backendPage = page - 1;
        if (backendPage < 0) backendPage = 0;

        PagedResponseDTO<LineaPedidoCompraDTO> lineasPage = lineaPedidoCompraService.obtenerTodasLasLineasPedidoCompra(backendPage, size, proveedor, cliente, sortBy, sortDir);
        List<LineaPedidoCompraDTO> lineasCompra = lineasPage.getContent();

        model.addAttribute("lineasPedidoCompra", lineasCompra);
        model.addAttribute("currentPage", page); // uno-based
        model.addAttribute("totalPages", lineasPage.getTotalPages());
        model.addAttribute("totalElements", lineasPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("cliente", cliente);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");


        return "lineas_pedido_compra";
    }

    @GetMapping("${modulo.compras.url.detalles-pedido-compra}")
    public String gestionarDetallesPedidoCompra(
            Model model,
            @RequestParam(defaultValue = "1") int page, // uno-based
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pedidoCompra.idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        // Convertir 'page' a basado en cero para Spring Data
        int backendPage = page - 1;
        if (backendPage < 0) backendPage = 0;

        PagedResponseDTO<PedidoCompraDetDTO> lineasPage = pedidoCompraDetService.obtenerPedidosCompraDet(backendPage, size, sortBy, sortDir);
        List<PedidoCompraDetDTO> pedidosCompraDet = lineasPage.getContent();

        model.addAttribute("pedidosCompraDet", pedidosCompraDet);
        model.addAttribute("currentPage", page); // uno-based
        model.addAttribute("totalPages", lineasPage.getTotalPages());
        model.addAttribute("totalElements", lineasPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return detallesPedidoCompraView;
    }

    @GetMapping("${modulo.compras.url.costes-pedido-compra}")
        public String gestionarCostesPedidoCompra(
                Model model,
        @RequestParam(defaultValue = "1") int page, // uno-based
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "pedidoCompra.idPedidoCompra") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir
    ) {

            // Convertir 'page' a basado en cero para Spring Data
            int backendPage = page - 1;
            if (backendPage < 0) backendPage = 0;

            PagedResponseDTO<CostesDTO> lineasPage = costesPedidoCompraService.obtenerTodosLosCostesPedidoCompra(backendPage, size, sortBy, sortDir);
            List<CostesDTO> costesPedidoCompra = lineasPage.getContent();

            model.addAttribute("costesPedidoCompra", costesPedidoCompra);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", lineasPage.getTotalPages());
            model.addAttribute("totalElements", lineasPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

            return costesPedidoCompraView;
        }

    @GetMapping("${modulo.compras.url.datos-barco-pedido-compra}")
    public String gestionarDatosBarcoPedidoCompra(
            Model model,
            @RequestParam(defaultValue = "1") int page, // uno-based
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pedidoCompra.idPedidoCompra") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {

        // Convertir 'page' a basado en cero para Spring Data
        int backendPage = page - 1;
        if (backendPage < 0) backendPage = 0;

        PagedResponseDTO<DatosBarcoDTO> lineasPage = datosBarcoPedidoCompraService.obtenerTodosLosDatosBarcoPedidoCompra(backendPage, size, sortBy, sortDir);
        List<DatosBarcoDTO> datosBarcoPedidoCompra = lineasPage.getContent();

        model.addAttribute("datosBarcoPedidoCompra", datosBarcoPedidoCompra);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", lineasPage.getTotalPages());
        model.addAttribute("totalElements", lineasPage.getTotalElements());
        model.addAttribute("size", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return datosBarcoPedidoCompraView;
    }

    @GetMapping("/formulario-compras")
    public String gestionarFormularioCompra(){
        return "formulario_compras";
    }


}
