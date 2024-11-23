package com.app.microservicio.compras.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineaPedidoCompraDTO {
    private Long idNumeroLinea;
    private Long idPedidoCompra;
    private Long n_linea;
    private Long n_operacion;
    private String proveedor;
    private String cliente;
    private String n_contenedor;
    private String producto;
    private String talla;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal p_neto;
    private String unidad;
    private Long bultos;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal precio;

    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal valor_compra;
    private String moneda;
    private String paisOrigen;
}
