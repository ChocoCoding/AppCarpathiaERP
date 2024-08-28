package com.app.microservicio.compras.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineaPedidoCompraDTO {
    private Long idNumeroLinea;
    private Long idPedidoCompra;
    private Long n_linea;
    private Long n_operacion;
    private String proveedor;
    private String cliente;
    private String n_contenedor;
    private String producto;
    private String talla;
    private BigDecimal p_neto;
    private String unidad;
    private Long bultos;
    private BigDecimal precio;
    private BigDecimal valor_compra;
    private String moneda;
    private String paisOrigen;
}
