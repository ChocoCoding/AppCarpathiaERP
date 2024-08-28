package com.app.microservicio.compras.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedidos_compra")
public class PedidoCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_compra")
    private Long idPedidoCompra;

    @Column(name = "n_operacion")
    private Long nOperacion;

    @Column(name = "n_contenedor", length = 20)
    private String nContenedor;

    @Column(name = "proforma", length = 20)
    private String proforma;

    @Column(name = "proveedor", length = 50)
    private String proveedor;

    @Column(name = "cliente", length = 50)
    private String cliente;

    @Column(name = "incoterm", length = 20)
    private String incoterm;

    @Column(name = "referencia_proveedor", length = 50)
    private String referenciaProveedor;

    @OneToMany(mappedBy = "pedidoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LineaPedidoCompra> lineasPedidoCompra;

    @OneToOne(mappedBy = "pedidoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private PedidoCompraDet pedidoCompraDet;

}
