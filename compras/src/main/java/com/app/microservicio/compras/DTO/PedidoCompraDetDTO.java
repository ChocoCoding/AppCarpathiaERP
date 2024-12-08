package com.app.microservicio.compras.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class PedidoCompraDetDTO {
    private Long idPedidoCompraDet;
    private Long idPedidoCompra;
    private Long n_operacion;
    private String contratoCompra;
    private char terminado;
    private String factProveedor;
    private String n_fact_flete;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha_pago_flete;

    private String n_bl;
    private BigDecimal pesoNetoTotal;
    private Long totalBultos;
    private BigDecimal promedio;
    private BigDecimal valorCompraTotal;
    private String observaciones;

    @JsonProperty("status")
    private char status;

    // Método para obtener la fecha como String en formato dd/MM/yyyy
    public String getFechaPagoFleteFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha_pago_flete != null ? fecha_pago_flete.format(formatter) : "";
    }
}
