package com.app.microservicio.compras.services;
import com.app.microservicio.compras.repository.PedidoCompraDetRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.app.microservicio.compras.entities.LineaPedidoCompra;
import com.app.microservicio.compras.repository.LineaPedidoCompraRepository;
import com.app.microservicio.compras.DTO.LineaPedidoCompraDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineaPedidoCompraService {

    @Autowired
    private LineaPedidoCompraRepository lineaPedidoCompraRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private PedidoCompraDetRepository pedidoCompraDetRepository;

    @Autowired
    private CalculoService calculoService;

    @Cacheable(
            value = "lineasPedidoCompra",
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString() + '-' + #proveedor + '-' + #cliente + '-' + #search + '-' + #searchFields"
    )
    public Page<LineaPedidoCompraDTO> listarLineasPedidoCompra(Pageable pageable, String proveedor, String cliente, String search, List<String> searchFields) {
        Specification<LineaPedidoCompra> spec = Specification.where(null);

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
            Specification<LineaPedidoCompra> searchSpec = Specification.where(null);
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
                } else if (field.equals("n_operacion") || field.equals("n_linea") || field.equals("bultos") ||
                        field.equals("p_neto") || field.equals("precio") || field.equals("valor_compra")) {
                    // Campos numéricos (Long y BigDecimal)
                    try {
                        if (field.equals("p_neto") || field.equals("precio") || field.equals("valor_compra")) {
                            BigDecimal value = new BigDecimal(search);
                            searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(root.get(field), value)
                            );
                        } else {
                            Long value = Long.parseLong(search);
                            searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                    criteriaBuilder.equal(root.get(field), value)
                            );
                        }
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

        return lineaPedidoCompraRepository.findAll(spec, pageable).map(this::convertirADTO);
    }




    @CacheEvict(value = "lineasPedidoCompra", allEntries = true)
    public LineaPedidoCompraDTO crearLineaPedido(LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        if (lineaPedidoCompraDTO.getIdPedidoCompra() == null) {
            throw new IllegalArgumentException("El ID de Pedido de Compra no puede ser nulo.");
        }
        lineaPedidoCompraDTO.setN_operacion(pedidoCompraRepository.findById(lineaPedidoCompraDTO.getIdPedidoCompra()).get().getNOperacion());
        lineaPedidoCompraDTO.setN_contenedor(pedidoCompraRepository.findById(lineaPedidoCompraDTO.getIdPedidoCompra()).get().getNContenedor());
        return convertirADTO(lineaPedidoCompraRepository.save(convertirAEntidad(lineaPedidoCompraDTO)));
    }

    @Transactional
    @CacheEvict(value = "lineasPedidoCompra", allEntries = true)
    public void eliminarLineaPedido(Long idNumeroLinea) {
        LineaPedidoCompra lineaExistente = lineaPedidoCompraRepository.findById(idNumeroLinea)
                .orElseThrow(() -> new RuntimeException("Línea de pedido no encontrada"));

        Long idPedidoCompra = lineaExistente.getPedidoCompra().getIdPedidoCompra();
        lineaExistente.setPNeto(new BigDecimal(0));
        lineaExistente.setBultos(0L);
        lineaExistente.setPrecio(new BigDecimal(0));
        lineaExistente.setValorCompra(new BigDecimal("0.0"));
        calculoService.recalcularPesoNetoTotal(idPedidoCompra);
        calculoService.recalcularTotalBultos(idPedidoCompra);
        calculoService.recalcularValoresCompra(idPedidoCompra);
        calculoService.recalcularPromedio(idPedidoCompra);
        lineaPedidoCompraRepository.save(lineaExistente);
        lineaPedidoCompraRepository.deleteById(idNumeroLinea);

    }

    @Transactional
    @CacheEvict(value = "lineasPedidoCompra", allEntries = true)
    public LineaPedidoCompraDTO actualizarLineaPedido(Long idNumeroLinea, LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        Optional<LineaPedidoCompra> optionalLinea = lineaPedidoCompraRepository.findById(idNumeroLinea);

        if (optionalLinea.isPresent()) {
            LineaPedidoCompra lineaPedidoCompra = optionalLinea.get();

            // Actualizar los campos con los valores del DTO
            lineaPedidoCompra.setNLinea(lineaPedidoCompraDTO.getN_linea());
            lineaPedidoCompra.setNOperacion(pedidoCompraRepository.findById(lineaPedidoCompraDTO.getIdPedidoCompra()).get().getNOperacion());
            lineaPedidoCompra.setProveedor(lineaPedidoCompraDTO.getProveedor());
            lineaPedidoCompra.setCliente(lineaPedidoCompraDTO.getCliente());
            lineaPedidoCompra.setNContenedor(pedidoCompraRepository.findById(lineaPedidoCompraDTO.getIdPedidoCompra()).get().getNContenedor());
            lineaPedidoCompra.setProducto(lineaPedidoCompraDTO.getProducto());
            lineaPedidoCompra.setTalla(lineaPedidoCompraDTO.getTalla());
            lineaPedidoCompra.setPNeto(lineaPedidoCompraDTO.getP_neto());
            lineaPedidoCompra.setUnidad(lineaPedidoCompraDTO.getUnidad());
            lineaPedidoCompra.setBultos(lineaPedidoCompraDTO.getBultos());
            lineaPedidoCompra.setPrecio(lineaPedidoCompraDTO.getPrecio());
            lineaPedidoCompra.setValorCompra(lineaPedidoCompraDTO.getValor_compra());
            lineaPedidoCompra.setMoneda(lineaPedidoCompraDTO.getMoneda());
            lineaPedidoCompra.setPaisOrigen(lineaPedidoCompraDTO.getPaisOrigen());

            // Guardar la línea de pedido actualizada
            LineaPedidoCompra lineaActualizada = lineaPedidoCompraRepository.save(lineaPedidoCompra);
            calculoService.recalcularPesoNetoTotal(lineaActualizada.getPedidoCompra().getIdPedidoCompra());
            calculoService.recalcularTotalBultos(lineaActualizada.getPedidoCompra().getIdPedidoCompra());
            calculoService.recalcularValoresCompra(lineaActualizada.getPedidoCompra().getIdPedidoCompra());
            calculoService.recalcularPromedio(lineaActualizada.getPedidoCompra().getIdPedidoCompra());
            return convertirADTO(lineaActualizada);
        } else {
            throw new EntityNotFoundException("LineaPedidoCompra no encontrada para el ID de pedido y número de línea especificados.");
        }
    }

    @CacheEvict(value = "lineasPedidoCompra", allEntries = true)
    public List<LineaPedidoCompraDTO> getLineasByPedidoCompra(Long idPedidoCompra) {
        return lineaPedidoCompraRepository.findByIdPedidoCompra(idPedidoCompra)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }





    private LineaPedidoCompraDTO convertirADTO(LineaPedidoCompra lineaPedidoCompra) {
        LineaPedidoCompraDTO lineaPedidoCompraDTO = new LineaPedidoCompraDTO();
        lineaPedidoCompraDTO.setIdNumeroLinea(lineaPedidoCompra.getIdNumeroLinea());
        lineaPedidoCompraDTO.setIdPedidoCompra(lineaPedidoCompra.getPedidoCompra().getIdPedidoCompra());
        lineaPedidoCompraDTO.setN_linea(lineaPedidoCompra.getNLinea());
        lineaPedidoCompraDTO.setN_operacion(lineaPedidoCompra.getNOperacion());
        lineaPedidoCompraDTO.setProveedor(lineaPedidoCompra.getProveedor());
        lineaPedidoCompraDTO.setCliente(lineaPedidoCompra.getCliente());
        lineaPedidoCompraDTO.setN_contenedor(lineaPedidoCompra.getNContenedor());
        lineaPedidoCompraDTO.setProducto(lineaPedidoCompra.getProducto());
        lineaPedidoCompraDTO.setTalla(lineaPedidoCompra.getTalla());
        lineaPedidoCompraDTO.setP_neto(lineaPedidoCompra.getPNeto());
        lineaPedidoCompraDTO.setUnidad(lineaPedidoCompra.getUnidad());
        lineaPedidoCompraDTO.setBultos(lineaPedidoCompra.getBultos());
        lineaPedidoCompraDTO.setPrecio(lineaPedidoCompra.getPrecio());
        lineaPedidoCompraDTO.setValor_compra(lineaPedidoCompra.getValorCompra());
        lineaPedidoCompraDTO.setMoneda(lineaPedidoCompra.getMoneda());
        lineaPedidoCompraDTO.setPaisOrigen(lineaPedidoCompra.getPaisOrigen());

        return lineaPedidoCompraDTO;
    }

    private LineaPedidoCompra convertirAEntidad(LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        LineaPedidoCompra lineaPedidoCompra = new LineaPedidoCompra();
        lineaPedidoCompra.setPedidoCompra(pedidoCompraRepository.findById(lineaPedidoCompraDTO.getIdPedidoCompra()).orElse(null));
        lineaPedidoCompra.setNLinea(lineaPedidoCompraDTO.getN_linea());
        lineaPedidoCompra.setNOperacion(lineaPedidoCompraDTO.getN_operacion());
        lineaPedidoCompra.setProveedor(lineaPedidoCompraDTO.getProveedor());
        lineaPedidoCompra.setCliente(lineaPedidoCompraDTO.getCliente());
        lineaPedidoCompra.setNContenedor(lineaPedidoCompraDTO.getN_contenedor());
        lineaPedidoCompra.setProducto(lineaPedidoCompraDTO.getProducto());
        lineaPedidoCompra.setTalla(lineaPedidoCompraDTO.getTalla());
        lineaPedidoCompra.setPNeto(lineaPedidoCompraDTO.getP_neto());
        lineaPedidoCompra.setUnidad(lineaPedidoCompraDTO.getUnidad());
        lineaPedidoCompra.setBultos(lineaPedidoCompraDTO.getBultos());
        lineaPedidoCompra.setPrecio(lineaPedidoCompraDTO.getPrecio());
        lineaPedidoCompra.setValorCompra(lineaPedidoCompraDTO.getValor_compra());
        lineaPedidoCompra.setMoneda(lineaPedidoCompraDTO.getMoneda());
        lineaPedidoCompra.setPaisOrigen(lineaPedidoCompraDTO.getPaisOrigen());
        return lineaPedidoCompra;
    }


}
