package com.app.microservicio.ventas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos_venta_det")
public class PedidoVentaDet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_venta_det")
    private Long idPedidoVentaDet;

    @Column(name = "peso_neto_total", precision = 15, scale = 6)
    private BigDecimal pesoNetoTotal;

    @Column(name = "total_bultos")
    private Long totalBultos;

    @Column(name = "precio_total", precision = 15, scale = 6)
    private BigDecimal precioTotal;

    @Column(name = "promedio", precision = 15, scale = 6)
    private BigDecimal promedio;

    @Column(name = "valor_venta_total", precision = 15, scale = 6)
    private BigDecimal valorVentaTotal;

    @Column(name = "importador", length = 200)
    private String importador;

    @Column(name = "status", length = 1)
    private char status;

    @OneToOne
    @JoinColumn(name = "id_pedido_venta")
    private PedidoVenta pedidoVenta;
}
