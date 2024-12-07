package com.app.frontend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoVentaDTO {
    private Long idPedidoVenta;
    private Long n_operacion;
    private String n_contenedor;
    private String proforma;
    private String proveedor;
    private String incoterm;
    private String referenciaProveedor;
    private char status;
}
