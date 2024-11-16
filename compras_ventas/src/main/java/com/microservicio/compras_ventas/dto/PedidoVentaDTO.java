package com.microservicio.compras_ventas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoVentaDTO {
    private Long idPedidoVenta;
    private Long n_operacion;
    private String proforma;
    private String proveedor;
    private String incoterm;
    private String referenciaProveedor;
}
