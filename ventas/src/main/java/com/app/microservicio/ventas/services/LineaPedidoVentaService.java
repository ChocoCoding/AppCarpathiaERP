package com.app.microservicio.ventas.services;

import com.app.microservicio.ventas.dto.LineaPedidoVentaDTO;
import com.app.microservicio.ventas.entities.LineaPedidoVenta;
import com.app.microservicio.ventas.repository.LineaPedidoVentaRepository;
import com.app.microservicio.ventas.repository.PedidoVentaDetRepository;
import com.app.microservicio.ventas.repository.PedidoVentaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineaPedidoVentaService {

    @Autowired
    private LineaPedidoVentaRepository lineaPedidoVentaRepository;

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    private PedidoVentaDetRepository pedidoVentaDetRepository;

    @Autowired
    private CalculoService calculoService;

    @Cacheable(
            value = "lineasPedidoVenta",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString() + '-' + #proveedor + '-' + #cliente + '-' + #search + '-' + #searchFields"
    )
    public Page<LineaPedidoVentaDTO> listarLineasPedidoVenta(Pageable pageable, String proveedor, String cliente, String search, List<String> searchFields) {
        Specification<LineaPedidoVenta> spec = Specification.where(null);

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
            Specification<LineaPedidoVenta> searchSpec = Specification.where(null);
            for (String field : searchFields) {
                if (field.contains(".")) {
                    // Campo anidado (ej. pedidoCompra.idPedidoCompra)
                    String[] parts = field.split("\\.");
                    searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get(parts[0]).get(parts[1]).as(String.class)),
                                    "%" + search.toLowerCase() + "%"
                            )
                    );
                } else if (field.equals("nOperacion") || field.equals("nLinea") || field.equals("bultos")) {
                    // Campos numéricos
                    try {
                        Long value = Long.parseLong(search);
                        searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get(field), value)
                        );
                    } catch (NumberFormatException e) {
                        // Ignorar si no es numérico
                    }
                } else {
                    // Campos de texto
                    searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + search.toLowerCase() + "%")
                    );
                }
            }
            spec = spec.and(searchSpec);
        }

        return lineaPedidoVentaRepository.findAll(spec, pageable).map(this::convertirADTO);
    }

    private LineaPedidoVentaDTO convertirADTO(LineaPedidoVenta lineaPedidoVenta) {
        LineaPedidoVentaDTO lineaPedidoVentaDTO = new LineaPedidoVentaDTO();
        lineaPedidoVentaDTO.setIdLineaPedidoVenta(lineaPedidoVenta.getIdLineaPedidoVenta());
        lineaPedidoVentaDTO.setIdPedidoVenta(lineaPedidoVenta.getPedidoVenta().getIdPedidoVenta());
        lineaPedidoVentaDTO.setN_linea(lineaPedidoVenta.getNLinea());
        lineaPedidoVentaDTO.setN_operacion(lineaPedidoVenta.getNOperacion());
        lineaPedidoVentaDTO.setProveedor(lineaPedidoVenta.getProveedor());
        lineaPedidoVentaDTO.setCliente(lineaPedidoVenta.getCliente());
        lineaPedidoVentaDTO.setContratoVenta(lineaPedidoVenta.getContratoVenta());
        lineaPedidoVentaDTO.setFacturaVenta(lineaPedidoVenta.getFacturaVenta());
        lineaPedidoVentaDTO.setN_contenedor(lineaPedidoVenta.getNContenedor());
        lineaPedidoVentaDTO.setProducto(lineaPedidoVenta.getProducto());
        lineaPedidoVentaDTO.setTalla(lineaPedidoVenta.getTalla());
        lineaPedidoVentaDTO.setPaisOrigen(lineaPedidoVenta.getPaisOrigen());
        lineaPedidoVentaDTO.setP_neto(lineaPedidoVenta.getPNeto());
        lineaPedidoVentaDTO.setUnidad(lineaPedidoVenta.getUnidad());
        lineaPedidoVentaDTO.setBultos(lineaPedidoVenta.getBultos());
        lineaPedidoVentaDTO.setPrecio(lineaPedidoVenta.getPrecio());
        lineaPedidoVentaDTO.setValor_venta(lineaPedidoVenta.getValorVenta());
        lineaPedidoVentaDTO.setIncoterm(lineaPedidoVenta.getIncoterm());
        lineaPedidoVentaDTO.setMoneda(lineaPedidoVenta.getMoneda());
        lineaPedidoVentaDTO.setComerciales(lineaPedidoVenta.getComerciales());
        lineaPedidoVentaDTO.setTransporte(lineaPedidoVenta.getTransporte());

        return lineaPedidoVentaDTO;
    }

    private LineaPedidoVenta convertirAEntidad(LineaPedidoVentaDTO lineaPedidoVentaDTO) {
        LineaPedidoVenta lineaPedidoVenta = new LineaPedidoVenta();
        lineaPedidoVenta.setPedidoVenta(pedidoVentaRepository.findById(lineaPedidoVentaDTO.getIdPedidoVenta()).orElse(null));
        lineaPedidoVenta.setNLinea(lineaPedidoVentaDTO.getN_linea());
        lineaPedidoVenta.setNOperacion(lineaPedidoVentaDTO.getN_operacion());
        lineaPedidoVenta.setProveedor(lineaPedidoVentaDTO.getProveedor());
        lineaPedidoVenta.setCliente(lineaPedidoVentaDTO.getCliente());
        lineaPedidoVenta.setContratoVenta(lineaPedidoVentaDTO.getContratoVenta());
        lineaPedidoVenta.setFacturaVenta(lineaPedidoVentaDTO.getFacturaVenta());
        lineaPedidoVenta.setNContenedor(lineaPedidoVentaDTO.getN_contenedor());
        lineaPedidoVenta.setProducto(lineaPedidoVentaDTO.getProducto());
        lineaPedidoVenta.setTalla(lineaPedidoVentaDTO.getTalla());
        lineaPedidoVenta.setPaisOrigen(lineaPedidoVentaDTO.getPaisOrigen());
        lineaPedidoVenta.setPNeto(lineaPedidoVentaDTO.getP_neto());
        lineaPedidoVenta.setUnidad(lineaPedidoVentaDTO.getUnidad());
        lineaPedidoVenta.setBultos(lineaPedidoVentaDTO.getBultos());
        lineaPedidoVenta.setPrecio(lineaPedidoVentaDTO.getPrecio());
        lineaPedidoVenta.setValorVenta(lineaPedidoVentaDTO.getValor_venta());
        lineaPedidoVenta.setIncoterm(lineaPedidoVentaDTO.getIncoterm());
        lineaPedidoVenta.setMoneda(lineaPedidoVentaDTO.getMoneda());
        lineaPedidoVenta.setComerciales(lineaPedidoVentaDTO.getComerciales());
        lineaPedidoVenta.setTransporte(lineaPedidoVentaDTO.getTransporte());


        return lineaPedidoVenta;
    }

    @CacheEvict(value = "lineasPedidoVenta", allEntries = true)
    public LineaPedidoVentaDTO crearLineaPedido(LineaPedidoVentaDTO lineaPedidoVentaDTO) {
        if (lineaPedidoVentaDTO.getIdPedidoVenta() == null) {
            throw new IllegalArgumentException("El ID de Pedido de Venta no puede ser nulo.");
        }
        return convertirADTO(lineaPedidoVentaRepository.save(convertirAEntidad(lineaPedidoVentaDTO)));
    }

    @Transactional
    @CacheEvict(value = "lineasPedidoVenta", allEntries = true)
    public void eliminarLineaPedido(Long idNumeroLinea) {
        LineaPedidoVenta lineaExistente = lineaPedidoVentaRepository.findById(idNumeroLinea)
                .orElseThrow(() -> new RuntimeException("Línea de pedido no encontrada"));

        Long idPedidoVenta = lineaExistente.getPedidoVenta().getIdPedidoVenta();
        lineaExistente.setPNeto(new BigDecimal(0));
        lineaExistente.setBultos(0L);
        lineaExistente.setPrecio(new BigDecimal(0));
        lineaExistente.setValorVenta(new BigDecimal("0.0"));
        calculoService.recalcularPesoNetoTotal(idPedidoVenta);
        calculoService.recalcularTotalBultos(idPedidoVenta);
        calculoService.recalcularValoresVenta(idPedidoVenta);
        calculoService.recalcularPromedio(idPedidoVenta);
        calculoService.recalcularPrecioTotalVenta(idPedidoVenta);
        lineaPedidoVentaRepository.save(lineaExistente);
        lineaPedidoVentaRepository.deleteById(idNumeroLinea);
    }

    @Transactional
    @CacheEvict(value = "lineasPedidoVenta", allEntries = true)
    public LineaPedidoVentaDTO actualizarLineaPedido(Long idNumeroLinea, LineaPedidoVentaDTO lineaPedidoVentaDTO) {
        Optional<LineaPedidoVenta> optionalLinea = lineaPedidoVentaRepository.findById(idNumeroLinea);

        if (optionalLinea.isPresent()) {
            LineaPedidoVenta lineaPedidoVenta = optionalLinea.get();

            // Actualizar los campos con los valores del DTO
            lineaPedidoVenta.setNLinea(lineaPedidoVentaDTO.getN_linea());
            lineaPedidoVenta.setNOperacion(lineaPedidoVentaDTO.getN_operacion());
            lineaPedidoVenta.setProveedor(lineaPedidoVentaDTO.getProveedor());
            lineaPedidoVenta.setCliente(lineaPedidoVentaDTO.getCliente());
            lineaPedidoVenta.setContratoVenta(lineaPedidoVentaDTO.getContratoVenta());
            lineaPedidoVenta.setFacturaVenta(lineaPedidoVentaDTO.getFacturaVenta());
            lineaPedidoVenta.setNContenedor(lineaPedidoVentaDTO.getN_contenedor());
            lineaPedidoVenta.setProducto(lineaPedidoVentaDTO.getProducto());
            lineaPedidoVenta.setTalla(lineaPedidoVentaDTO.getTalla());
            lineaPedidoVenta.setPaisOrigen(lineaPedidoVentaDTO.getPaisOrigen());
            lineaPedidoVenta.setPNeto(lineaPedidoVentaDTO.getP_neto());
            lineaPedidoVenta.setUnidad(lineaPedidoVentaDTO.getUnidad());
            lineaPedidoVenta.setBultos(lineaPedidoVentaDTO.getBultos());
            lineaPedidoVenta.setPrecio(lineaPedidoVentaDTO.getPrecio());
            lineaPedidoVenta.setValorVenta(lineaPedidoVentaDTO.getValor_venta());
            lineaPedidoVenta.setIncoterm(lineaPedidoVentaDTO.getIncoterm());
            lineaPedidoVenta.setMoneda(lineaPedidoVentaDTO.getMoneda());
            lineaPedidoVenta.setComerciales(lineaPedidoVentaDTO.getComerciales());
            lineaPedidoVenta.setTransporte(lineaPedidoVentaDTO.getTransporte());

            // Guardar la línea de pedido actualizada
            LineaPedidoVenta lineaActualizada = lineaPedidoVentaRepository.save(lineaPedidoVenta);
            calculoService.recalcularPesoNetoTotal(lineaActualizada.getPedidoVenta().getIdPedidoVenta());
            calculoService.recalcularTotalBultos(lineaActualizada.getPedidoVenta().getIdPedidoVenta());
            calculoService.recalcularValoresVenta(lineaActualizada.getPedidoVenta().getIdPedidoVenta());
            calculoService.recalcularPromedio(lineaActualizada.getPedidoVenta().getIdPedidoVenta());
            calculoService.recalcularPrecioTotalVenta(lineaActualizada.getPedidoVenta().getIdPedidoVenta());
            return convertirADTO(lineaActualizada);
        } else {
            throw new EntityNotFoundException("LineaPedidoVenta no encontrada para el ID de pedido y número de línea especificados.");
        }
    }

    @CacheEvict(value = "lineasPedidoVenta", allEntries = true)
    public List<LineaPedidoVentaDTO> getLineasByPedidoVenta(Long idPedidoVenta) {
        return lineaPedidoVentaRepository.findByIdPedidoVenta(idPedidoVenta)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
}
