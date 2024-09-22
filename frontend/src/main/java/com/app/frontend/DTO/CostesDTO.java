package com.app.frontend.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostesDTO {
    private Long idCosteCompra;
    private Long idPedidoCompra;
    private Long n_operacion;
    private String n_contenedor;
    private BigDecimal arancel;
    private BigDecimal sanidad;
    private BigDecimal plastico;
    private BigDecimal carga;
    private BigDecimal inland;
    private BigDecimal muellaje;
    private BigDecimal pif;
    private BigDecimal despacho;
    private BigDecimal conexiones;
    private BigDecimal iva;
    private String dec_iva;
    private BigDecimal tasa_sanitaria;
    private BigDecimal suma_costes;
    private BigDecimal gasto_total;
}
