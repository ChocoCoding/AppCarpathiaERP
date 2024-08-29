package com.app.frontend.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PedidoCompraDetDTO {
    private Long idPedidoCompraDet;
    private Long idPedidoCompra;
    private Long n_operacion;
    private String contratoCompra;
    private char terminado;
    private String factProveedor;
    private String n_fact_flete;
    private String fecha_pago_flete;
    private String n_bl;
    private BigDecimal pesoNetoTotal;
    private Long totalBultos;
    private BigDecimal promedio;
    private BigDecimal valorCompraTotal;
    private String observaciones;
}
