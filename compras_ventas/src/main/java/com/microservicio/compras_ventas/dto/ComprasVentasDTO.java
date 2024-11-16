package com.microservicio.compras_ventas.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComprasVentasDTO {

    private PedidoCompraDTO pedidoCompra;
    private List<PedidoVentaDTO> pedidosVenta;

}
