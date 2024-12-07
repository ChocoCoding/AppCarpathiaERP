package com.app.microservicio.compras.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "datos_barco")
public class DatosBarcoPedidoCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_datos_barco")
    private Long idDatosBarco;

    @Column(name = "n_operacion")
    private Long nOperacion;

    @Column(name = "n_contenedor", length = 200)
    private String nContenedor;

    @Column(name = "nombre_barco", length = 50)
    private String nombreBarco;

    @Column(name = "viaje", length = 50)
    private String viaje;

    @Column(name = "naviera", length = 50)
    private String naviera;

    @Column(name = "puerto_embarque", length = 50)
    private String puertoEmbarque;

    @Column(name = "puerto_llegada", length = 50)
    private String puertoLlegada;

    @Column(name = "fecha_embarque")
    private LocalDate fecha_embarque;

    @Column(name = "fecha_llegada")
    private LocalDate fecha_llegada;

    @Column(name = "flete", precision = 15, scale = 6)
    private BigDecimal flete;

    @Column(name = "fecha_pago_flete")
    private LocalDate fecha_pago_flete;

    @Column(name = "factura_flete", length = 50)
    private String factura_flete;

    @OneToOne
    @JoinColumn(name = "id_pedido_compra")
    private PedidoCompra pedidoCompra;

}
