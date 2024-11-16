package com.app.frontend.DTO;

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
