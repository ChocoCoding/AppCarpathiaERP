package com.app.microservicio.compras.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "costes_compra")
public class CostePedidoCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coste")
    private Long idCosteCompra;

    @Column(name = "n_operacion")
    private Long nOperacion;

    @Column(name = "n_contenedor", length = 20)
    private String nContenedor;

    @Column(name = "arancel", precision = 15, scale = 6)
    private BigDecimal arancel;

    @Column(name = "sanidad", precision = 15, scale = 6)
    private BigDecimal sanidad;

    @Column(name = "plastico", precision = 15, scale = 6)
    private BigDecimal plastico;

    @Column(name = "carga", precision = 15, scale = 6)
    private BigDecimal carga;

    @Column(name = "inland", precision = 15, scale = 6)
    private BigDecimal inland;

    @Column(name = "muellaje", precision = 15, scale = 6)
    private BigDecimal muellaje;

    @Column(name = "pif", precision = 15, scale = 6)
    private BigDecimal pif;

    @Column(name = "despacho", precision = 15, scale = 6)
    private BigDecimal despacho;

    @Column(name = "conexiones", precision = 15, scale = 6)
    private BigDecimal conexiones;

    @Column(name = "iva", precision = 15, scale = 6)
    private BigDecimal iva;

    @Column(name = "dec_iva", length = 50)
    private String dec_iva;

    @Column(name = "tasa_sanitaria", precision = 15, scale = 6)
    private BigDecimal tasa_sanitaria;

    @Column(name = "gasto_total", precision = 15, scale = 6)
    private BigDecimal gasto_total;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido_compra")
    private PedidoCompra pedidoCompra;


}
