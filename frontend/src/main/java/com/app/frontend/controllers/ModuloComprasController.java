package com.app.frontend.controllers;

import com.app.frontend.DTO.LineaPedidoCompraDTO;
import com.app.frontend.DTO.PedidoCompraDTO;
import com.app.frontend.DTO.PedidoCompraDetDTO;
import com.app.frontend.service.LineaPedidoCompraService;
import com.app.frontend.service.PedidoCompraDetService;
import com.app.frontend.service.PedidoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/pedidos-compra")
    public String gestionarPedidosCompra(Model model) {
        List<PedidoCompraDTO> pedidosCompra = pedidoCompraService.obtenerTodosLosPedidosCompra();
        model.addAttribute("pedidosCompra", pedidosCompra);
        return "pedidos_compra";
    }

    @GetMapping("/lineas-pedido-compra")
    public String gestionarLineasPedidoCompra(Model model) {
        List<LineaPedidoCompraDTO> lineasPedidoCompra = lineaPedidoCompraService.obtenerTodasLasLineasPedidoCompra();
        model.addAttribute("lineasPedidoCompra", lineasPedidoCompra);
        return "lineas_pedido_compra";
    }

    @GetMapping("/detalles-pedido-compra")
    public String gestionarDetallesPedidoCompra(Model model) {
        List<PedidoCompraDetDTO> detallesPedidoCompra = pedidoCompraDetService.obtenerTodosLosDetallesPedidoCompra();
        model.addAttribute("detallesPedidoCompra", detallesPedidoCompra);
        return "pedidos_compra_det";
    }
}
