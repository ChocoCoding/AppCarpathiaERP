package com.app.microservicio.compras.services;
import com.app.microservicio.compras.entities.LineaPedidoCompra;
import com.app.microservicio.compras.repository.LineaPedidoCompraRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.PedidoCompraDetRepository;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoCompraDetService {

    @Autowired
    private PedidoCompraDetRepository pedidoCompraDetRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private LineaPedidoCompraRepository lineaPedidoCompraRepository;

    @Autowired
    private CalculoService calculoService;

    @CacheEvict(value = "pedidosCompraDet", allEntries = true)
    public Optional<PedidoCompraDetDTO> obtenerPedidoCompraDet(Long idPedidoCompra) {
        return pedidoCompraDetRepository.findById(idPedidoCompra)
                .map(this::convertirADTO);
    }

    @CacheEvict(value = "pedidosCompraDet", allEntries = true)
    public PedidoCompraDetDTO actualizarPedidoCompraDet(Long id, PedidoCompraDetDTO pedidoCompraDetDTO) {
        // Verificar si ya existe un registro con el mismo ID
        Optional<PedidoCompraDet> existente = pedidoCompraDetRepository.findById(id);

        PedidoCompraDet pedidoCompraDet;
        if (existente.isPresent()) {
            // Si el registro existe, actualizarlo
            pedidoCompraDet = existente.get();
        } else {
            // Si no existe, crear uno nuevo
            pedidoCompraDet = new PedidoCompraDet();
        }

        // Actualizar campos
        pedidoCompraDet.setNOperacion(pedidoCompraDetDTO.getN_operacion());
        pedidoCompraDet.setContratoCompra(pedidoCompraDetDTO.getContratoCompra());
        pedidoCompraDet.setTerminado(pedidoCompraDetDTO.getTerminado());
        pedidoCompraDet.setFactProveedor(pedidoCompraDetDTO.getFactProveedor());
        pedidoCompraDet.setNFactFlete(pedidoCompraDetDTO.getN_fact_flete());
        pedidoCompraDet.setFechaPagoFlete(pedidoCompraDetDTO.getFecha_pago_flete());
        pedidoCompraDet.setNBl(pedidoCompraDetDTO.getN_bl());
        pedidoCompraDet.setPesoNetoTotal(pedidoCompraDetDTO.getPesoNetoTotal());
        pedidoCompraDet.setTotalBultos(pedidoCompraDetDTO.getTotalBultos());
        pedidoCompraDet.setPromedio(pedidoCompraDetDTO.getPromedio());
        pedidoCompraDet.setValorCompraTotal(pedidoCompraDetDTO.getValorCompraTotal());
        pedidoCompraDet.setObservaciones(pedidoCompraDetDTO.getObservaciones());
        pedidoCompraDet.setPedidoCompra(pedidoCompraRepository.findById(pedidoCompraDetDTO.getIdPedidoCompra()).orElse(null));
        pedidoCompraDet.setStatus(pedidoCompraDetDTO.getStatus());
        //Calculamos el promedio
        calculoService.recalcularPromedio(pedidoCompraDet.getIdPedidoCompraDet());
        return convertirADTO(pedidoCompraDetRepository.save(pedidoCompraDet));
    }



    private PedidoCompraDetDTO convertirADTO(PedidoCompraDet pedidoCompraDet) {
        PedidoCompraDetDTO pedidoCompraDetDTO = new PedidoCompraDetDTO();
        pedidoCompraDetDTO.setIdPedidoCompraDet(pedidoCompraDet.getIdPedidoCompraDet());
        pedidoCompraDetDTO.setIdPedidoCompra(pedidoCompraDet.getPedidoCompra().getIdPedidoCompra());
        pedidoCompraDetDTO.setN_operacion(pedidoCompraDet.getNOperacion());
        pedidoCompraDetDTO.setContratoCompra(pedidoCompraDet.getContratoCompra());
        pedidoCompraDetDTO.setTerminado(pedidoCompraDet.getTerminado());
        pedidoCompraDetDTO.setFactProveedor(pedidoCompraDet.getFactProveedor());
        pedidoCompraDetDTO.setN_fact_flete(pedidoCompraDet.getNFactFlete());
        pedidoCompraDetDTO.setFecha_pago_flete(pedidoCompraDet.getFechaPagoFlete());
        pedidoCompraDetDTO.setN_bl(pedidoCompraDet.getNBl());
        pedidoCompraDetDTO.setPesoNetoTotal(pedidoCompraDet.getPesoNetoTotal());
        pedidoCompraDetDTO.setTotalBultos(pedidoCompraDet.getTotalBultos());
        pedidoCompraDetDTO.setPromedio(pedidoCompraDet.getPromedio());
        pedidoCompraDetDTO.setValorCompraTotal(pedidoCompraDet.getValorCompraTotal());
        pedidoCompraDetDTO.setObservaciones(pedidoCompraDet.getObservaciones());
        pedidoCompraDetDTO.setStatus(pedidoCompraDet.getStatus());
        return pedidoCompraDetDTO;
    }

    private PedidoCompraDet convertirAEntidad(PedidoCompraDetDTO pedidoCompraDetDTO) {

        PedidoCompraDet pedidoCompraDet = new PedidoCompraDet();
        pedidoCompraDet.setIdPedidoCompraDet(pedidoCompraDetDTO.getIdPedidoCompraDet());
        pedidoCompraDet.setPedidoCompra(pedidoCompraRepository.findById(pedidoCompraDetDTO.getIdPedidoCompra()).orElse(null));
        pedidoCompraDet.setNOperacion(pedidoCompraDetDTO.getN_operacion());
        pedidoCompraDet.setContratoCompra(pedidoCompraDetDTO.getContratoCompra());
        pedidoCompraDet.setTerminado(pedidoCompraDetDTO.getTerminado());
        pedidoCompraDet.setFactProveedor(pedidoCompraDetDTO.getFactProveedor());
        pedidoCompraDet.setNFactFlete(pedidoCompraDetDTO.getN_fact_flete());
        pedidoCompraDet.setFechaPagoFlete(pedidoCompraDetDTO.getFecha_pago_flete());
        pedidoCompraDet.setNBl(pedidoCompraDetDTO.getN_bl());
        pedidoCompraDet.setPesoNetoTotal(pedidoCompraDetDTO.getPesoNetoTotal());
        pedidoCompraDet.setTotalBultos(pedidoCompraDetDTO.getTotalBultos());
        pedidoCompraDet.setPromedio(pedidoCompraDetDTO.getPromedio());
        pedidoCompraDet.setValorCompraTotal(pedidoCompraDetDTO.getValorCompraTotal());
        pedidoCompraDet.setObservaciones(pedidoCompraDetDTO.getObservaciones());
        pedidoCompraDet.setStatus(pedidoCompraDetDTO.getStatus());
        return pedidoCompraDet;
    }


    public Page<PedidoCompraDetDTO> listarPedidosCompraDet(Pageable pageable, String search, List<String> searchFields) {
        Specification<PedidoCompraDet> spec = Specification.where(null);

        // Lógica de búsqueda
        if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
            Specification<PedidoCompraDet> searchSpec = Specification.where(null);
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
                } else if (field.equals("idPedidoCompraDet") || field.equals("nOperacion") || field.equals("totalBultos") ) {
                    // Campos numéricos
                    try {
                        Long value = Long.parseLong(search);
                        searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get(field), value)
                        );
                    } catch (NumberFormatException e) {
                        // Ignorar si no es numérico
                    }
                }else if (field.equals("pesoNetoTotal") || field.equals("promedio") || field.equals("valorCompraTotal")){
                    try {
                        Double value = Double.parseDouble(search);
                        searchSpec = searchSpec.or(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(field),value)));
                    }catch (NumberFormatException e) {
                        // Ignorar si no es numérico
                    }
                }else if (field.equals("fechaPagoFlete")) {
                    String[] partes = search.split("/");
                    if (partes.length == 3) {
                        // Formato dd/MM/yyyy exacto
                        LocalDate dateValue = LocalDate.parse(search, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        searchSpec = searchSpec.or((root, query, cb) -> cb.equal(root.get(field), dateValue));
                    } else if (partes.length == 2) {
                        // Formato MM/yyyy (mes y año)
                        int mes = Integer.parseInt(partes[0]);
                        int anio = Integer.parseInt(partes[1]);
                        LocalDate start = LocalDate.of(anio, mes, 1);
                        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
                        searchSpec = searchSpec.or((root, query, cb) ->
                                cb.between(root.get(field), start, end));
                    } else if (partes.length == 1) {
                        // Sólo año
                        int anio = Integer.parseInt(partes[0]);
                        LocalDate start = LocalDate.of(anio, 1, 1);
                        LocalDate end = LocalDate.of(anio, 12, 31);
                        searchSpec = searchSpec.or((root, query, cb) ->
                                cb.between(root.get(field), start, end));
                    }
                }else {
                    // Campos de texto
                    searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                            criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), "%" + search.toLowerCase() + "%")
                    );
                }
            }
            spec = spec.and(searchSpec);
        }

        return pedidoCompraDetRepository.findAll(spec, pageable).map(this::convertirADTO);
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
    @CacheEvict(value = "pedidosCompraDet", allEntries = true)
    public void eliminarPedidoCompraDet(Long idPedidoCompraDet) {
        pedidoCompraDetRepository.deleteById(idPedidoCompraDet);
    }

    @CacheEvict(value = "pedidosCompraDet", allEntries = true)
    public PedidoCompraDetDTO crearPedidoCompraDet(PedidoCompraDetDTO pedidoCompraDetDTO) {
         Long idPedidoCompra = pedidoCompraDetDTO.getIdPedidoCompra();
         BigDecimal pesoNetoTotal = lineaPedidoCompraRepository.sumPesoNetoByPedidoCompraId(idPedidoCompra);
         Long totalBultos = lineaPedidoCompraRepository.sumBultosByPedidoCompraId(idPedidoCompra);
         pedidoCompraDetDTO.setPesoNetoTotal(pesoNetoTotal);
         pedidoCompraDetDTO.setTotalBultos(totalBultos);
         pedidoCompraDetDTO.setStatus('P');
        //Calculamos el promedio
        pedidoCompraDetDTO.setValorCompraTotal(calculoService.calcularValoresCompra(pedidoCompraDetDTO.getIdPedidoCompra()).getBody());
        pedidoCompraDetDTO.setPromedio(calculoService.calcularPromedio(pedidoCompraDetDTO.getIdPedidoCompra()).getBody());
        PedidoCompraDet pedidoCompraDet = convertirAEntidad(pedidoCompraDetDTO);
        pedidoCompraDetRepository.save(pedidoCompraDet);
        calculoService.actualizarCamposPedidoCompraDet(pedidoCompraDetDTO.getIdPedidoCompra());

        PedidoCompraDet pedidoAGuardar = pedidoCompraDetRepository.findById(pedidoCompraDet.getIdPedidoCompraDet()).get();
    return convertirADTO(pedidoAGuardar);
    }
}

