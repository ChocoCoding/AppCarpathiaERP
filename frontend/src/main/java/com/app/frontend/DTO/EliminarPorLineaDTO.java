package com.app.frontend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EliminarPorLineaDTO{
    private Long idPedidoCompra;
    private Long nLinea;
}
