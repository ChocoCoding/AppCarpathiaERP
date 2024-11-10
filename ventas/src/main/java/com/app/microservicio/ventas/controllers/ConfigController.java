package com.app.microservicio.ventas.controllers;

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
    @Value("${endpoint.api.pedidos_venta}")
    private String pedidosVentaEndpoint;

    @Value("${endpoint.api.pedido_venta_id}")
    private String pedidoVentaIdEndpoint;

    @Value("${endpoint.api.lineas_pedidos_venta}")
    private String lineasPedidosVentaEndpoint;

    @Value("${endpoint.api.lineas_pedidos_venta_id}")
    private String lineasPedidosVentaIdEndpoint;

    @Value("${endpoint.api.pedidos_venta_det}")
    private String pedidosVentaDetEndpoint;

    @Value("${endpoint.api.pedido_venta_det_id}")
    private String pedidoVentaDetIdEndpoint;


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



    @Value("${mensaje.error_id_invalido}")
    private String errorIdInvalido;

    @Value("${mensaje.detalle_creado_exito}")
    private String detalleCreadoExito;

    @Value("${mensaje.error_crear_detalle}")
    private String errorCrearDetalle;

    @Value("${mensaje.confirmar_eliminar_detalle}")
    private String confirmarEliminarDetalle;

    @Value("${mensaje.confirmar_eliminar_detalle_texto}")
    private String confirmarEliminarDetalleTexto;

    @Value("${mensaje.error_detalle_existe}")
    private String detalleYaExiste;

    @Value("${mensaje.detalle_eliminado_exito}")
    private String detalleEliminadoExito;

    @Value("${mensaje.error_eliminar_detalle}")
    private String errorEliminarDetalle;

    @Value("${mensaje.confirmar_eliminar}")
    private String confirmarEliminar;

    @Value("${mensaje.cancelar}")
    private String cancelar;

    @Value("${mensaje.eliminado}")
    private String eliminado;

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


    @GetMapping("/api/ventas/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();

        // Endpoints
        config.put("pedidosVentaEndpoint", pedidosVentaEndpoint);
        config.put("pedidoVentaIdEndpoint", pedidoVentaIdEndpoint);
        config.put("lineasPedidosVentaEndpoint", lineasPedidosVentaEndpoint);
        config.put("lineasPedidosVentaIdEndpoint", lineasPedidosVentaIdEndpoint);
        config.put("pedidosVentaDetEndpoint", pedidosVentaDetEndpoint);
        config.put("pedidoVentaDetIdEndpoint", pedidoVentaDetIdEndpoint);


        // Mensajes genericos
        config.put("creacionExitosa", creacionExitosa);
        config.put("pedidoCreadoExito", pedidoCreadoExito);
        config.put("error", error);
        config.put("errorCrearPedido", errorCrearPedido);
        config.put("guardadoExitoso", guardadoExitoso);
        config.put("cambiosGuardadosExito", cambiosGuardadosExito);
        config.put("errorGuardarCambios", errorGuardarCambios);
        config.put("errorIdPedidoInvalido", errorIdPedidoInvalido);
        config.put("errorIdInvalido", errorIdInvalido);
        config.put("confirmarEliminar", confirmarEliminar);
        config.put("cancelar", cancelar);
        config.put("eliminado", eliminado);
        config.put("errorFormatoFechaIncorrecto", errorFormatoFechaIncorrecto);
        config.put("errorFechaFormato", errorFechaFormato);
        config.put("errorValorIncorrecto", errorValorIncorrecto);

        //Mensajes Pedido Venta
        config.put("eliminarPedidoConfirmacion", eliminarPedidoConfirmacion);
        config.put("pedidoEliminadoExito", pedidoEliminadoExito);
        config.put("errorEliminarPedido", errorEliminarPedido);

        //Mensajes LineaPedido Venta
        config.put("lineaCreadaExito", lineaCreadaExito);
        config.put("errorCrearLinea", errorCrearLinea);
        config.put("eliminarLineaConfirmacion", eliminarLineaConfirmacion);
        config.put("lineaEliminadaExito", lineaEliminadaExito);
        config.put("errorEliminarLinea", errorEliminarLinea);
        config.put("confirmarEliminarLinea", confirmarEliminarLinea);
        config.put("confirmarEliminarLineaTexto", confirmarEliminarLineaTexto);

        //Mensajes Detalles
        config.put("detalleCreadoExito", detalleCreadoExito);
        config.put("errorCrearDetalle", errorCrearDetalle);
        config.put("confirmarEliminarDetalle", confirmarEliminarDetalle);
        config.put("confirmarEliminarDetalleTexto", confirmarEliminarDetalleTexto);
        config.put("detalleEliminadoExito", detalleEliminadoExito);
        config.put("errorEliminarDetalle", errorEliminarDetalle);
        config.put("errorTerminado", errorTerminado);
        config.put("detalleYaExiste",detalleYaExiste);

        return config;
    }
}
