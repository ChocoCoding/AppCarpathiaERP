package com.app.microservicio.compras.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@PropertySource("classpath:endpoints.properties")
@PropertySource("classpath:strings.properties")
public class ConfigController {

    // Endpoints
    @Value("${endpoint.api.pedidos_compra}")
    private String pedidosCompraEndpoint;

    @Value("${endpoint.api.pedido_compra_id}")
    private String pedidoCompraIdEndpoint;

    @Value("${endpoint.api.lineas_pedidos_compra}")
    private String lineasPedidosCompraEndpoint;

    @Value("${endpoint.api.lineas_pedidos_compra_id}")
    private String lineasPedidosCompraIdEndpoint;

    @Value("${endpoint.api.pedidos_compra_det}")
    private String pedidosCompraDetEndpoint;

    @Value("${endpoint.api.pedido_compra_det_id}")
    private String pedidoCompraDetIdEndpoint;

    // Mensajes
    @Value("${mensaje.creacion_exitosa}")
    private String creacionExitosa;

    @Value("${mensaje.pedido_creado_exito}")
    private String pedidoCreadoExito;

    @Value("${mensaje.error}")
    private String error;

    @Value("${mensaje.error_crear_pedido}")
    private String errorCrearPedido;

    @Value("${mensaje.guardado_exitoso}")
    private String guardadoExitoso;

    @Value("${mensaje.cambios_guardados_exito}")
    private String cambiosGuardadosExito;

    @Value("${mensaje.error_guardar_cambios}")
    private String errorGuardarCambios;

    @Value("${mensaje.eliminar_pedido_confirmacion}")
    private String eliminarPedidoConfirmacion;

    @Value("${mensaje.pedido_eliminado_exito}")
    private String pedidoEliminadoExito;

    @Value("${mensaje.error_eliminar_pedido}")
    private String errorEliminarPedido;

    @Value("${mensaje.linea_creada_exito}")
    private String lineaCreadaExito;

    @Value("${mensaje.error_crear_linea}")
    private String errorCrearLinea;

    @Value("${mensaje.error_id_pedido_invalido}")
    private String errorIdPedidoInvalido;

    @Value("${mensaje.eliminar_linea_confirmacion}")
    private String eliminarLineaConfirmacion;

    @Value("${mensaje.linea_eliminada_exito}")
    private String lineaEliminadaExito;

    @Value("${mensaje.error_eliminar_linea}")
    private String errorEliminarLinea;

    @Value("${mensaje.detalle_creado_exito}")
    private String detalleCreadoExito;

    @Value("${mensaje.error_crear_detalle}")
    private String errorCrearDetalle;

    @Value("${mensaje.error_id_invalido}")
    private String errorIdInvalido;

    @Value("${mensaje.confirmar_eliminar_detalle}")
    private String confirmarEliminarDetalle;

    @Value("${mensaje.confirmar_eliminar_detalle_texto}")
    private String confirmarEliminarDetalleTexto;

    @Value("${mensaje.confirmar_eliminar}")
    private String confirmarEliminar;

    @Value("${mensaje.cancelar}")
    private String cancelar;

    @Value("${mensaje.eliminado}")
    private String eliminado;

    @Value("${mensaje.detalle_eliminado_exito}")
    private String detalleEliminadoExito;

    @Value("${mensaje.error_eliminar_detalle}")
    private String errorEliminarDetalle;

    @Value("${mensaje.error_formato_fecha_incorrecto}")
    private String errorFormatoFechaIncorrecto;

    @Value("${mensaje.error_fecha_formato}")
    private String errorFechaFormato;

    @Value("${mensaje.error_valor_incorrecto}")
    private String errorValorIncorrecto;

    @Value("${mensaje.error_terminado}")
    private String errorTerminado;

    @Value("${mensaje.confirmarEliminarLinea}")
    private String confirmarEliminarLinea;

    @Value("${mensaje.confirmarEliminarLineaTexto}")
    private String confirmarEliminarLineaTexto;

    @GetMapping("/api/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();

        // Endpoints
        config.put("pedidosCompraEndpoint", pedidosCompraEndpoint);
        config.put("pedidoCompraIdEndpoint", pedidoCompraIdEndpoint);
        config.put("lineasPedidosCompraEndpoint", lineasPedidosCompraEndpoint);
        config.put("lineasPedidosCompraIdEndpoint", lineasPedidosCompraIdEndpoint);
        config.put("pedidosCompraDetEndpoint", pedidosCompraDetEndpoint);
        config.put("pedidoCompraDetIdEndpoint", pedidoCompraDetIdEndpoint);

        // Mensajes
        config.put("creacionExitosa", creacionExitosa);
        config.put("pedidoCreadoExito", pedidoCreadoExito);
        config.put("error", error);
        config.put("errorCrearPedido", errorCrearPedido);
        config.put("guardadoExitoso", guardadoExitoso);
        config.put("cambiosGuardadosExito", cambiosGuardadosExito);
        config.put("errorGuardarCambios", errorGuardarCambios);
        config.put("eliminarPedidoConfirmacion", eliminarPedidoConfirmacion);
        config.put("pedidoEliminadoExito", pedidoEliminadoExito);
        config.put("errorEliminarPedido", errorEliminarPedido);
        config.put("lineaCreadaExito", lineaCreadaExito);
        config.put("errorCrearLinea", errorCrearLinea);
        config.put("errorIdPedidoInvalido", errorIdPedidoInvalido);
        config.put("eliminarLineaConfirmacion", eliminarLineaConfirmacion);
        config.put("lineaEliminadaExito", lineaEliminadaExito);
        config.put("errorEliminarLinea", errorEliminarLinea);
        config.put("detalleCreadoExito", detalleCreadoExito);
        config.put("errorCrearDetalle", errorCrearDetalle);
        config.put("errorIdInvalido", errorIdInvalido);
        config.put("confirmarEliminarDetalle", confirmarEliminarDetalle);
        config.put("confirmarEliminarDetalleTexto", confirmarEliminarDetalleTexto);
        config.put("confirmarEliminar", confirmarEliminar);
        config.put("cancelar", cancelar);
        config.put("eliminado", eliminado);
        config.put("detalleEliminadoExito", detalleEliminadoExito);
        config.put("errorEliminarDetalle", errorEliminarDetalle);
        config.put("errorFormatoFechaIncorrecto", errorFormatoFechaIncorrecto);
        config.put("errorFechaFormato", errorFechaFormato);
        config.put("errorValorIncorrecto", errorValorIncorrecto);
        config.put("errorTerminado", errorTerminado);
        config.put("confirmarEliminarLinea", confirmarEliminarLinea);
        config.put("confirmarEliminarLineaTexto", confirmarEliminarLineaTexto);

        return config;
    }
}