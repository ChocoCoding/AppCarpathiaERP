package com.app.microservicio.ventas.services;

import com.app.microservicio.ventas.dto.PedidoVentaDTO;
import com.app.microservicio.ventas.entities.PedidoVenta;
import com.app.microservicio.ventas.repository.PedidoVentaRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoVentaService {

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    EntityManager entityManager;

    @Cacheable(
            value = "pedidosVenta",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString() + '-' + #proveedor + '-' + #search + '-' + #searchFields"
    )
    public Page<PedidoVentaDTO> listarPedidosVenta(Pageable pageable, String proveedor, String search, List<String> searchFields) {
        Specification<PedidoVenta> spec = Specification.where(null);

        // Filtro por proveedor
        if (proveedor != null && !proveedor.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("proveedor")), "%" + proveedor.toLowerCase() + "%")
            );
        }

        // Lógica de búsqueda
        if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
            Specification<PedidoVenta> searchSpec = Specification.where(null);
            for (String field : searchFields) {
                if (field.equals("idPedidoVenta") || field.equals("nOperacion")) { // Campos numéricos
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

        return pedidoVentaRepository.findAll(spec, pageable).map(this::convertirADTO);
    }

    public Optional<PedidoVentaDTO> obtenerPedidoVenta(Long id) {
        return pedidoVentaRepository.findById(id)
                .map(this::convertirADTO);
    }

    public PedidoVentaDTO guardarPedidoVenta(PedidoVentaDTO pedidoVentaDTO) {
        PedidoVenta pedidoVenta = convertirAEntidad(pedidoVentaDTO);
        PedidoVenta nuevoPedidoVenta = pedidoVentaRepository.save(pedidoVenta);
        return convertirADTO(nuevoPedidoVenta);
    }

    @Transactional
    public boolean eliminarPedidoVenta(Long id) {
        try {
            if (pedidoVentaRepository.existsById(id)) {
                // Eliminar las relaciones en las tablas relacionadas
                entityManager.createNativeQuery("DELETE FROM lineas_pedidos_venta WHERE id_pedido_venta = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                entityManager.createNativeQuery("DELETE FROM pedidos_venta_det WHERE id_pedido_venta = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                entityManager.createNativeQuery("DELETE FROM compras_ventas WHERE id_pedido_venta = :id")
                        .setParameter("id", id)
                        .executeUpdate();

                // Eliminar el pedido en pedidos_venta
                pedidoVentaRepository.deleteById(id);

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
    public PedidoVentaDTO actualizarPedidoVenta(Long id, PedidoVentaDTO pedidoVentaDTO) {
        Optional<PedidoVenta> pedidoVentaOpt = pedidoVentaRepository.findById(id);

        if (pedidoVentaOpt.isPresent()) {
            PedidoVenta pedidoVenta = pedidoVentaOpt.get();
            // Actualizar los campos
            pedidoVenta.setNOperacion(pedidoVentaDTO.getN_operacion());
            pedidoVenta.setProforma(pedidoVentaDTO.getProforma());
            pedidoVenta.setProveedor(pedidoVentaDTO.getProveedor());
            pedidoVenta.setIncoterm(pedidoVentaDTO.getIncoterm());
            pedidoVenta.setReferenciaProveedor(pedidoVentaDTO.getReferenciaProveedor());

            PedidoVenta actualizado = pedidoVentaRepository.save(pedidoVenta);
            return convertirADTO(actualizado);
        } else {
            return null;
        }
    }

    public boolean existePedidoVenta(Long idPedidoVenta) {
        return pedidoVentaRepository.existsById(idPedidoVenta);
    }

    private PedidoVentaDTO convertirADTO(PedidoVenta pedidoVenta) {
        PedidoVentaDTO pedidoVentaDTO = new PedidoVentaDTO();
        pedidoVentaDTO.setIdPedidoVenta(pedidoVenta.getIdPedidoVenta());
        pedidoVentaDTO.setN_operacion(pedidoVenta.getNOperacion());
        pedidoVentaDTO.setProforma(pedidoVenta.getProforma());
        pedidoVentaDTO.setProveedor(pedidoVenta.getProveedor());
        pedidoVentaDTO.setIncoterm(pedidoVenta.getIncoterm());
        pedidoVentaDTO.setReferenciaProveedor(pedidoVenta.getReferenciaProveedor());
        return pedidoVentaDTO;
    }

    private PedidoVenta convertirAEntidad(PedidoVentaDTO pedidoVentaDTO) {
        PedidoVenta pedidoVenta = new PedidoVenta();
        pedidoVenta.setIdPedidoVenta(pedidoVentaDTO.getIdPedidoVenta());
        pedidoVenta.setNOperacion(pedidoVentaDTO.getN_operacion());
        pedidoVenta.setProforma(pedidoVentaDTO.getProforma());
        pedidoVenta.setProveedor(pedidoVentaDTO.getProveedor());
        pedidoVenta.setIncoterm(pedidoVentaDTO.getIncoterm());
        pedidoVenta.setReferenciaProveedor(pedidoVentaDTO.getReferenciaProveedor());
        return pedidoVenta;
    }
}
