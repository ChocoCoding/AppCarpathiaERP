package com.app.frontend.DTO;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineaPedidoVentaDTO {
    private Long idLineaPedidoVenta;
    private Long idPedidoVenta;
    private Long n_linea;
    private Long n_operacion;
    private String proveedor;
    private String cliente;
    private String contratoVenta;
    private String facturaVenta;
    private String n_contenedor;
    private String producto;
    private String talla;
    private String paisOrigen;
    private BigDecimal p_neto;
    private String unidad;
    private Long bultos;
    private BigDecimal precio;
    private BigDecimal valor_venta;
    private String incoterm;
    private String moneda;
    private String comerciales;
    private BigDecimal transporte;

}
