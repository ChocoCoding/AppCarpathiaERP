package com.app.microservicio.compras.services;

import com.app.microservicio.compras.exceptions.OperacionExistenteException;
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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoCompraService {

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private CalculoService calculoService;

    @Autowired
    EntityManager entityManager;


    public Page<PedidoCompraDTO> listarPedidosCompra(Pageable pageable, String search, List<String> searchFields) {
        Specification<PedidoCompra> spec = Specification.where(null);

        if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
            Specification<PedidoCompra> searchSpec = Specification.where(null);
            String searchLower = search.toLowerCase();

            for (String field : searchFields) {
                if (field.equals("idPedidoCompra") || field.equals("nOperacion")) {
                    // Campos numéricos
                    try {
                        Long value = Long.parseLong(search);
                        // Si la conversión tiene éxito, filtrar por igualdad exacta
                        searchSpec = searchSpec.or((root, query, cb) ->
                                cb.equal(root.get(field), value)
                        );
                    } catch (NumberFormatException e) {
                        // Si no es número, ignorar este campo
                    }
                } else {
                    // Campos de texto: aplicar coincidencia parcial
                    // Ejemplo: LIKE '%searchLower%'
                    searchSpec = searchSpec.or((root, query, cb) ->
                            cb.like(cb.lower(root.get(field)), "%" + searchLower + "%")
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

    public Optional<PedidoCompraDTO> obtenerPedidoCompraPorOperacion(Long nOperacion){
        return pedidoCompraRepository.findPedidoCompraByNoperacion(nOperacion)
                .map(this::convertirADTO);
    }

    public PedidoCompraDTO guardarPedidoCompra(PedidoCompraDTO pedidoCompraDTO) {
        Optional<PedidoCompra> pedidoCompraOperacion = pedidoCompraRepository.findPedidoCompraByNoperacion(pedidoCompraDTO.getN_operacion());
        if (pedidoCompraOperacion.isPresent() && !Objects.equals(pedidoCompraOperacion.get().getIdPedidoCompra(), pedidoCompraDTO.getIdPedidoCompra())){
            throw new OperacionExistenteException("El número de operación introducido ya está asignado a un pedido de compra.");
        }
        PedidoCompra pedidoCompra = convertirAEntidad(pedidoCompraDTO);
        PedidoCompra nuevoPedidoCompra = pedidoCompraRepository.save(pedidoCompra);
        calculoService.actualizarCamposLineaPedido(pedidoCompra.getIdPedidoCompra());
        calculoService.actualizarCamposPedidoCompraDet(pedidoCompra.getIdPedidoCompra());
        calculoService.actualizarCamposDatosBarco(pedidoCompra.getIdPedidoCompra());
        calculoService.actualizarCostePedidoCompra(pedidoCompra.getIdPedidoCompra());
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

                entityManager.createNativeQuery("DELETE FROM compras_ventas WHERE id_pedido_compra = :id")
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
            Optional<PedidoCompra> pedidoCompraOperacion = pedidoCompraRepository.findById(id);
            if (pedidoCompraOperacion.isPresent() && !Objects.equals(pedidoCompraOperacion.get().getIdPedidoCompra(), pedidoCompraOpt.get().getIdPedidoCompra())){
                throw new OperacionExistenteException("El número de operación introducido ya está asignado a un pedido de compra.");
            }
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
            calculoService.actualizarCamposLineaPedido(actualizado.getIdPedidoCompra());
            calculoService.actualizarCamposPedidoCompraDet(actualizado.getIdPedidoCompra());
            calculoService.actualizarCamposDatosBarco(actualizado.getIdPedidoCompra());
            calculoService.actualizarCostePedidoCompra(actualizado.getIdPedidoCompra());
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
