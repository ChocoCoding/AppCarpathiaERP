package com.app.microservicio.compras.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lineas_pedidos_compra")
public class LineaPedidoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNumeroLinea;

    @Column(name = "n_linea")
    private Long nLinea;

    @Column(name = "n_operacion")
    private Long nOperacion;

    @Column(name = "proveedor", length = 50)
    private String proveedor;

    @Column(name = "cliente", length = 50)
    private String cliente;

    @Column(name = "n_contenedor", length = 20)
    private String nContenedor;

    @Column(name = "producto", length = 50)
    private String producto;

    @Column(name = "talla", length = 20)
    private String talla;

    @Column(name = "p_neto", precision = 15, scale = 6)
    private BigDecimal pNeto;

    @Column(name = "unidad", length = 10)
    private String unidad;

    @Column(name = "bultos")
    private Long bultos;

    @Column(name = "precio", precision = 15, scale = 4)
    private BigDecimal precio;

    @Column(name = "valor_compra", precision = 15, scale = 6)
    private BigDecimal valorCompra;

    @Column(name = "moneda", length = 10)
    private String moneda;

    @Column(name = "pais_origen", length = 50)
    private String paisOrigen;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido_compra")
    private PedidoCompra pedidoCompra;

}