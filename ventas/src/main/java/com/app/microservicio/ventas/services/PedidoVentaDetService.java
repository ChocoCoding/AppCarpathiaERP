package com.app.microservicio.ventas.services;

import com.app.microservicio.ventas.dto.PedidoVentaDetDTO;
import com.app.microservicio.ventas.entities.PedidoVentaDet;
import com.app.microservicio.ventas.repository.LineaPedidoVentaRepository;
import com.app.microservicio.ventas.repository.PedidoVentaDetRepository;
import com.app.microservicio.ventas.repository.PedidoVentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoVentaDetService {
    @Autowired
    private PedidoVentaDetRepository pedidoVentaDetRepository;

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    @Autowired
    private LineaPedidoVentaRepository lineaPedidoVentaRepository;

    @Autowired
    private CalculoService calculoService;

    @CacheEvict(value = "pedidosVentaDet", allEntries = true)
    public Optional<PedidoVentaDetDTO> obtenerPedidoVentaDet(Long idPedidoVenta) {
        return pedidoVentaDetRepository.findById(idPedidoVenta)
                .map(this::convertirADTO);
    }

    @CacheEvict(value = "pedidosVentaDet", allEntries = true)
    public PedidoVentaDetDTO actualizarPedidoVentaDet(Long id, PedidoVentaDetDTO pedidoVentaDetDTO) {
        // Verificar si ya existe un registro con el mismo ID
        Optional<PedidoVentaDet> existente = pedidoVentaDetRepository.findById(id);

        PedidoVentaDet pedidoVentaDet;
        if (existente.isPresent()) {
            // Si el registro existe, actualizarlo
            pedidoVentaDet = existente.get();
        } else {
            // Si no existe, crear uno nuevo
            pedidoVentaDet = new PedidoVentaDet();
        }

        // Actualizar campos
        pedidoVentaDet.setPesoNetoTotal(pedidoVentaDetDTO.getPesoNetoTotal());
        pedidoVentaDet.setTotalBultos(pedidoVentaDetDTO.getTotalBultos());

        pedidoVentaDet.setPrecioTotal(pedidoVentaDetDTO.getPrecioTotal());
        pedidoVentaDet.setPromedio(pedidoVentaDetDTO.getPromedio());
        pedidoVentaDet.setValorVentaTotal(pedidoVentaDetDTO.getValorVentaTotal());
        pedidoVentaDet.setImportador(pedidoVentaDetDTO.getImportador());
        pedidoVentaDet.setPedidoVenta(pedidoVentaRepository.findById(pedidoVentaDetDTO.getIdPedidoVenta()).orElse(null));

        //Calculamos el promedio
        calculoService.recalcularPrecioTotalVenta(pedidoVentaDet.getIdPedidoVentaDet());
        calculoService.recalcularPromedio(pedidoVentaDet.getIdPedidoVentaDet());

        return convertirADTO(pedidoVentaDetRepository.save(pedidoVentaDet));
    }



    private PedidoVentaDetDTO convertirADTO(PedidoVentaDet pedidoVentaDet) {
        PedidoVentaDetDTO pedidoVentaDetDTO = new PedidoVentaDetDTO();
        pedidoVentaDetDTO.setIdPedidoVentaDet(pedidoVentaDet.getIdPedidoVentaDet());
        pedidoVentaDetDTO.setIdPedidoVenta(pedidoVentaDet.getPedidoVenta().getIdPedidoVenta());
        pedidoVentaDetDTO.setPesoNetoTotal(pedidoVentaDet.getPesoNetoTotal());
        pedidoVentaDetDTO.setTotalBultos(pedidoVentaDet.getTotalBultos());
        pedidoVentaDetDTO.setPrecioTotal(pedidoVentaDet.getPrecioTotal());
        pedidoVentaDetDTO.setPromedio(pedidoVentaDet.getPromedio());
        pedidoVentaDetDTO.setValorVentaTotal(pedidoVentaDet.getValorVentaTotal());
        pedidoVentaDetDTO.setImportador(pedidoVentaDet.getImportador());
        return pedidoVentaDetDTO;
    }

    private PedidoVentaDet convertirAEntidad(PedidoVentaDetDTO pedidoVentaDetDTO) {

        PedidoVentaDet pedidoVentaDet = new PedidoVentaDet();
        pedidoVentaDet.setIdPedidoVentaDet(pedidoVentaDetDTO.getIdPedidoVentaDet());
        pedidoVentaDet.setPedidoVenta(pedidoVentaRepository.findById(pedidoVentaDetDTO.getIdPedidoVenta()).orElse(null));
        pedidoVentaDet.setPesoNetoTotal(pedidoVentaDetDTO.getPesoNetoTotal());
        pedidoVentaDet.setTotalBultos(pedidoVentaDetDTO.getTotalBultos());
        pedidoVentaDet.setPrecioTotal(pedidoVentaDetDTO.getPrecioTotal());
        pedidoVentaDet.setPromedio(pedidoVentaDetDTO.getPromedio());
        pedidoVentaDet.setValorVentaTotal(pedidoVentaDetDTO.getValorVentaTotal());
        pedidoVentaDet.setImportador(pedidoVentaDetDTO.getImportador());
        return pedidoVentaDet;
    }


    public Page<PedidoVentaDetDTO> listarPedidosVentaDet(Pageable pageable, String search, List<String> searchFields) {
        Specification<PedidoVentaDet> spec = Specification.where(null);

        // Lógica de búsqueda
        if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
            Specification<PedidoVentaDet> searchSpec = Specification.where(null);
            for (String field : searchFields) {
                if (field.contains(".")) {
                    // Handle nested fields
                    String[] parts = field.split("\\.");
                    String parentField = parts[0];
                    String childField = parts[1];

                    searchSpec = searchSpec.or((root, query, criteriaBuilder) -> {
                        if (isNumeric(search)) {
                            return criteriaBuilder.equal(
                                    root.get(parentField).get(childField),
                                    Long.parseLong(search)
                            );
                        } else {
                            return criteriaBuilder.like(
                                    criteriaBuilder.lower(root.get(parentField).get(childField).as(String.class)),
                                    "%" + search.toLowerCase() + "%"
                            );
                        }
                    });
                } else if (field.equals("idPedidoVentaDet") || field.equals("nOperacion") || field.equals("totalBultos") ) {
                    // Campos numéricos
                    try {
                        Long value = Long.parseLong(search);
                        searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get(field), value)
                        );
                    } catch (NumberFormatException e) {
                        // Ignorar si no es numérico
                    }
                }else if (field.equals("pesoNetoTotal") || field.equals("promedio") || field.equals("valorVentaTotal") ||field.equals("precioTotal")){
                    try {
                        Double value = Double.parseDouble(search);
                        searchSpec = searchSpec.or(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field),value)));
                    }catch (NumberFormatException e) {
                        // Ignorar si no es numérico
                    }
                }else if (field.equals("fechaPagoFlete")) {
                    // Campo de fecha
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate dateValue = LocalDate.parse(search, formatter);
                        searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get(field), dateValue)
                        );
                    } catch (Exception e) {
                        // Ignorar si no es una fecha válida
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

        return pedidoVentaDetRepository.findAll(spec, pageable).map(this::convertirADTO);
    }

    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Transactional
    @CacheEvict(value = "pedidosVentaDet", allEntries = true)
    public void eliminarPedidoVentaDet(Long idPedidoVentaDet) {
        pedidoVentaDetRepository.deleteById(idPedidoVentaDet);
    }

    @CacheEvict(value = "pedidosVentaDet", allEntries = true)
    public PedidoVentaDetDTO crearPedidoVentaDet(PedidoVentaDetDTO pedidoVentaDetDTO) {
        Long idPedidoVenta = pedidoVentaDetDTO.getIdPedidoVenta();
        BigDecimal pesoNetoTotal = lineaPedidoVentaRepository.sumPesoNetoByPedidoVentaId(idPedidoVenta);
        Long totalBultos = lineaPedidoVentaRepository.sumBultosByPedidoVentaId(idPedidoVenta);
        pedidoVentaDetDTO.setPesoNetoTotal(pesoNetoTotal);
        pedidoVentaDetDTO.setTotalBultos(totalBultos);
        //Calculamos el promedio
        pedidoVentaDetDTO.setValorVentaTotal(calculoService.calcularValoresVenta(pedidoVentaDetDTO.getIdPedidoVenta()).getBody());
        pedidoVentaDetDTO.setPromedio(calculoService.calcularPromedio(pedidoVentaDetDTO.getIdPedidoVenta()).getBody());
        pedidoVentaDetDTO.setPrecioTotal(calculoService.calcularPrecioVenta(pedidoVentaDetDTO.getIdPedidoVenta()).getBody());
        return convertirADTO(pedidoVentaDetRepository.save(convertirAEntidad(pedidoVentaDetDTO)));
    }
}
