package com.app.microservicio.compras.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatosBarcoDTO {
    private Long idPedidoCompra;
    private Long idDatosBarco;
    private Long n_operacion;
    private String nombreBarco;
    private String viaje;
    private String naviera;
    private String puertoEmbarque;
    private String puertoLlegada;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha_embarque;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha_llegada;

    private BigDecimal flete;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha_pago_flete;

    private String facturaFlete;

    // Método para obtener la fecha como String en formato dd/MM/yyyy
    public String getFechaPagoFleteBarcoFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha_pago_flete != null ? fecha_pago_flete.format(formatter) : "";
    }

    // Método para obtener la fecha como String en formato dd/MM/yyyy
    public String getFechaEmbarqueFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha_embarque != null ? fecha_embarque.format(formatter) : "";
    }

    // Método para obtener la fecha como String en formato dd/MM/yyyy
    public String getFechaLlegadaFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha_llegada != null ? fecha_llegada.format(formatter) : "";
    }
}
