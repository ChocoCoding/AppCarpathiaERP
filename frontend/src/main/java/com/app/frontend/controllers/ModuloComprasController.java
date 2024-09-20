package com.app.frontend.controllers;

import com.app.frontend.DTO.CostesDTO;
import com.app.frontend.DTO.LineaPedidoCompraDTO;
import com.app.frontend.DTO.PedidoCompraDTO;
import com.app.frontend.DTO.PedidoCompraDetDTO;
import com.app.frontend.service.CostesPedidoCompraService;
import com.app.frontend.service.LineaPedidoCompraService;
import com.app.frontend.service.PedidoCompraDetService;
import com.app.frontend.service.PedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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




    @GetMapping("${modulo.compras.url.pedidos-compra}")
    public String gestionarPedidosCompra(Model model) {
        List<PedidoCompraDTO> pedidosCompra = pedidoCompraService.obtenerTodosLosPedidosCompra();
        model.addAttribute(pedidosCompraModel, pedidosCompra);
        return pedidosCompraView;
    }

    @GetMapping("${modulo.compras.url.lineas-pedido-compra}")
    public String gestionarLineasPedidoCompra(Model model) {
        List<LineaPedidoCompraDTO> lineasPedidoCompra = lineaPedidoCompraService.obtenerTodasLasLineasPedidoCompra();
        model.addAttribute(lineasPedidoCompraModel, lineasPedidoCompra);
        return lineasPedidoCompraView;
    }

    @GetMapping("${modulo.compras.url.detalles-pedido-compra}")
    public String gestionarDetallesPedidoCompra(Model model) {
        List<PedidoCompraDetDTO> detallesPedidoCompra = pedidoCompraDetService.obtenerTodosLosDetallesPedidoCompra();
        model.addAttribute(detallesPedidoCompraModel, detallesPedidoCompra);
        return detallesPedidoCompraView;
    }

    @GetMapping("${modulo.compras.url.costes-pedido-compra}")
    public String gestionarCostesPedidoCompra(Model model) {
        List<CostesDTO> costesPedidoCompra = costesPedidoCompraService.obtenerTodosLosCostesPedidoCompra();
        model.addAttribute(costesPedidoCompraModel, costesPedidoCompra);
        return costesPedidoCompraView;
    }


}
