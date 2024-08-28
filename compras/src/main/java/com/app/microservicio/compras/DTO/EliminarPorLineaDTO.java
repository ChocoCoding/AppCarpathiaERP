package com.app.microservicio.compras.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EliminarPorLineaDTO{
    private Long idPedidoCompra;
    private Long nLinea;
}
