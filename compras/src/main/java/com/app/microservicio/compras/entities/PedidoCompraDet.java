package com.app.microservicio.compras.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos_compra_det")
public class PedidoCompraDet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_compra_det")
    private Long idPedidoCompraDet;

    @Column(name = "n_operacion")
    private Long nOperacion;

    @Column(name = "contrato_compra", length = 50)
    private String contratoCompra;

    @Column(name = "terminado", length = 1)
    private char terminado;

    @Column(name = "fact_proveedor", length = 50)
    private String factProveedor;

    @Column(name = "n_fact_flete", length = 50)
    private String nFactFlete;

    @Column(name = "fecha_pago_flete")
    private java.sql.Date fechaPagoFlete;

    @Column(name = "n_bl", length = 20)
    private String nBl;

    @Column(name = "peso_neto_total", precision = 15, scale = 6)
    private BigDecimal pesoNetoTotal;

    @Column(name = "total_bultos")
    private Long totalBultos;

    @Column(name = "promedio", precision = 15, scale = 6)
    private BigDecimal promedio;

    @Column(name = "valor_compra_total", precision = 15, scale = 6)
    private BigDecimal valorCompraTotal;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @OneToOne
    @JoinColumn(name = "id_pedido_compra")
    private PedidoCompra pedidoCompra;

}