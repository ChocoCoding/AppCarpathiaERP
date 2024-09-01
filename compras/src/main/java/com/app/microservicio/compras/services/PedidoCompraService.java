package com.app.microservicio.compras.services;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.microservicio.compras.entities.PedidoCompra;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import com.app.microservicio.compras.DTO.PedidoCompraDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoCompraService {

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    EntityManager entityManager;

    public List<PedidoCompraDTO> listarPedidosCompra() {
        return pedidoCompraRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<PedidoCompraDTO> obtenerPedidoCompra(Long id) {
        return pedidoCompraRepository.findById(id)
                .map(this::convertirADTO);
    }

    public PedidoCompraDTO guardarPedidoCompra(PedidoCompraDTO pedidoCompraDTO) {
        PedidoCompra pedidoCompra = convertirAEntidad(pedidoCompraDTO);
        PedidoCompra nuevoPedidoCompra = pedidoCompraRepository.save(pedidoCompra);
        return convertirADTO(nuevoPedidoCompra);
    }

    @Transactional
    public boolean eliminarPedidoCompra(Long id) {
        if (pedidoCompraRepository.existsById(id)) {
            // Eliminar las relaciones en la tabla usuario_roles
            entityManager.createNativeQuery("DELETE FROM lineas_pedidos_compra WHERE id_pedido_compra = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            entityManager.createNativeQuery("DELETE FROM pedidos_compra_det WHERE id_pedido_compra = :id")
                    .setParameter("id", id)
                    .executeUpdate();

            pedidoCompraRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public PedidoCompraDTO actualizarPedidoCompra(Long id, PedidoCompraDTO pedidoCompraDTO) {
        Optional<PedidoCompra> pedidoCompraOpt = pedidoCompraRepository.findById(id);

        if (pedidoCompraOpt.isPresent()) {
            PedidoCompra pedidoCompra = pedidoCompraOpt.get();
            // Actualizar los campos
            pedidoCompra.setNOperacion(pedidoCompraDTO.getN_operacion());
            pedidoCompra.setNContenedor(pedidoCompraDTO.getN_contenedor());
            pedidoCompra.setProforma(pedidoCompraDTO.getProforma());
            pedidoCompra.setProveedor(pedidoCompraDTO.getProveedor());
            pedidoCompra.setCliente(pedidoCompraDTO.getCliente());
            pedidoCompra.setIncoterm(pedidoCompraDTO.getIncoterm());
            pedidoCompra.setReferenciaProveedor(pedidoCompraDTO.getReferenciaProveedor());

            PedidoCompra actualizado = pedidoCompraRepository.save(pedidoCompra);
            return convertirADTO(actualizado);
        } else {
            return null;
        }
    }

    public boolean existePedidoCompra(Long idPedidoCompra) {
        return pedidoCompraRepository.existsById(idPedidoCompra);
    }


    private PedidoCompraDTO convertirADTO(PedidoCompra pedidoCompra) {
        PedidoCompraDTO pedidoCompraDTO = new PedidoCompraDTO();
        pedidoCompraDTO.setIdPedidoCompra(pedidoCompra.getIdPedidoCompra());
        pedidoCompraDTO.setN_operacion(pedidoCompra.getNOperacion());
        pedidoCompraDTO.setN_contenedor(pedidoCompra.getNContenedor());
        pedidoCompraDTO.setProforma(pedidoCompra.getProforma());
        pedidoCompraDTO.setProveedor(pedidoCompra.getProveedor());
        pedidoCompraDTO.setCliente(pedidoCompra.getCliente());
        pedidoCompraDTO.setIncoterm(pedidoCompra.getIncoterm());
        pedidoCompraDTO.setReferenciaProveedor(pedidoCompra.getReferenciaProveedor());
        return pedidoCompraDTO;
    }

    private PedidoCompra convertirAEntidad(PedidoCompraDTO pedidoCompraDTO) {
        PedidoCompra pedidoCompra = new PedidoCompra();
        pedidoCompra.setIdPedidoCompra(pedidoCompraDTO.getIdPedidoCompra());
        pedidoCompra.setNOperacion(pedidoCompraDTO.getN_operacion());
        pedidoCompra.setNContenedor(pedidoCompraDTO.getN_contenedor());
        pedidoCompra.setProforma(pedidoCompraDTO.getProforma());
        pedidoCompra.setProveedor(pedidoCompraDTO.getProveedor());
        pedidoCompra.setCliente(pedidoCompraDTO.getCliente());
        pedidoCompra.setIncoterm(pedidoCompraDTO.getIncoterm());
        pedidoCompra.setReferenciaProveedor(pedidoCompraDTO.getReferenciaProveedor());
        return pedidoCompra;
    }
}
