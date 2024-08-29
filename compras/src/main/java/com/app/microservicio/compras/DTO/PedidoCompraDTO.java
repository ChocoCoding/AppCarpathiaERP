package com.app.microservicio.compras.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoCompraDTO {
    private Long idPedidoCompra;
    private Long n_operacion;
    private String n_contenedor;
    private String proforma;
    private String proveedor;
    private String cliente;
    private String incoterm;
    private String referenciaProveedor;
}