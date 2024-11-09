package com.app.microservicio.compras.services;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    @Cacheable(
            value = "pedidosCompra",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString() + '-' + #proveedor + '-' + #cliente + '-' + #search + '-' + #searchFields"
    )
    public Page<PedidoCompraDTO> listarPedidosCompra(Pageable pageable, String proveedor, String cliente, String search, List<String> searchFields) {
        Specification<PedidoCompra> spec = Specification.where(null);

        // Filtro por proveedor
        if (proveedor != null && !proveedor.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("proveedor")), "%" + proveedor.toLowerCase() + "%")
            );
        }

        // Filtro por cliente
        if (cliente != null && !cliente.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("cliente")), "%" + cliente.toLowerCase() + "%")
            );
        }

        // Lógica de búsqueda
        if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
            Specification<PedidoCompra> searchSpec = Specification.where(null);
            for (String field : searchFields) {
                if (field.equals("idPedidoCompra") || field.equals("nOperacion")) { // Campos numéricos
                    try {
                        Long value = Long.parseLong(search);
                        searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get(field), value)
                        );
                    } catch (NumberFormatException e) {
                        // Manejar si el input no es numérico
                        // Opcional: Puedes optar por ignorar este campo o lanzar una excepción
                        System.err.println("Valor de búsqueda no es numérico para el campo: " + field);
                    }
                } else { // Campos de texto
                    searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + search.toLowerCase() + "%")
                    );
                }
            }
            spec = spec.and(searchSpec);
        }

        return pedidoCompraRepository.findAll(spec, pageable).map(this::convertirADTO);
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
        try {
            if (pedidoCompraRepository.existsById(id)) {
                // Eliminar las relaciones en las tablas relacionadas
                entityManager.createNativeQuery("DELETE FROM lineas_pedidos_compra WHERE id_pedido_compra = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                entityManager.createNativeQuery("DELETE FROM pedidos_compra_det WHERE id_pedido_compra = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                entityManager.createNativeQuery("DELETE FROM costes_compra WHERE id_pedido_compra = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                entityManager.createNativeQuery("DELETE FROM datos_barco WHERE id_pedido_compra = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                // Eliminar el pedido en pedidos_compra
                pedidoCompraRepository.deleteById(id);

                return true;
            } else {
                return false; // El pedido no existe
            }
        } catch (Exception e) {
            // Manejo de cualquier error que pueda ocurrir durante el proceso
            System.err.println("Error al eliminar el pedido con id " + id + ": " + e.getMessage());
            return false; // Error durante la eliminación
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
