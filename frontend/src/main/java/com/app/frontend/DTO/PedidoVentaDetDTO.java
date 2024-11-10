package com.app.frontend.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PedidoVentaDetDTO {

    private Long idPedidoVentaDet;
    private Long idPedidoVenta;
    private BigDecimal pesoNetoTotal;
    private Long totalBultos;
    private BigDecimal precioTotal;
    private BigDecimal promedio;
    private BigDecimal valorVentaTotal;
    private String importador;

}
