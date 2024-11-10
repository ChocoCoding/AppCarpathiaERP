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
@Table(name = "lineas_pedidos_venta")
public class LineaPedidoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_linea_pedido_venta")
    private Long idLineaPedidoVenta;

    @Column(name = "n_linea")
    private Long nLinea;

    @Column(name = "n_operacion")
    private Long nOperacion;

    @Column(name = "proveedor", length = 200)
    private String proveedor;

    @Column(name = "cliente", length = 200)
    private String cliente;

    @Column(name = "contrato_venta", length = 200)
    private String contratoVenta;

    @Column(name = "factura_venta", length = 200)
    private String facturaVenta;

    @Column(name = "n_contenedor", length = 200)
    private String nContenedor;

    @Column(name = "producto", length = 200)
    private String producto;

    @Column(name = "talla", length = 200)
    private String talla;

    @Column(name = "pais_origen", length = 200)
    private String paisOrigen;

    @Column(name = "p_neto", precision = 15, scale = 6)
    private BigDecimal pNeto;

    @Column(name = "unidad", length = 200)
    private String unidad;

    @Column(name = "bultos")
    private Long bultos;

    @Column(name = "precio", precision = 15, scale = 4)
    private BigDecimal precio;

    @Column(name = "valor_venta", precision = 15, scale = 6)
    private BigDecimal valorVenta;

    @Column(name = "incoterm", length = 200)
    private String incoterm;

    @Column(name = "moneda", length = 200)
    private String moneda;

    @Column(name = "comerciales", length = 200)
    private String comerciales;

    @Column(name = "transporte", length = 200)
    private BigDecimal transporte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido_venta")
    private PedidoVenta pedidoVenta;
}
