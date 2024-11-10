package com.app.microservicio.ventas.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pedidos_venta")
public class PedidoVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_venta")
    private Long idPedidoVenta;

    @Column(name = "n_operacion")
    private Long nOperacion;

    @Column(name = "proforma", length = 200)
    private String proforma;

    @Column(name = "proveedor", length = 200)
    private String proveedor;

    @Column(name = "incoterm", length = 200)
    private String incoterm;

    @Column(name = "referencia_proveedor", length = 200)
    private String referenciaProveedor;

    @OneToMany(mappedBy = "pedidoVenta")
    private Set<LineaPedidoVenta> lineasPedidoVenta;

    @OneToOne(mappedBy = "pedidoVenta")
    private PedidoVentaDet pedidoVentaDet;

}
