package com.app.microservicio.compras.services;

import com.app.microservicio.compras.DTO.CostesDTO;
import com.app.microservicio.compras.entities.CostePedidoCompra;
import com.app.microservicio.compras.repository.CostePedidoRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CostePedidoService {

    @Autowired
    private CostePedidoRepository costePedidoRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private CalculoService calculoService;

    // Convertir Entidad a DTO
    private CostesDTO convertirADTO(CostePedidoCompra costePedidoCompra) {
        CostesDTO costePedidoCompraDTO = new CostesDTO();
        costePedidoCompraDTO.setIdCosteCompra(costePedidoCompra.getIdCosteCompra());
        costePedidoCompraDTO.setIdPedidoCompra(costePedidoCompra.getPedidoCompra().getIdPedidoCompra());
        costePedidoCompraDTO.setN_operacion(costePedidoCompra.getNOperacion());
        costePedidoCompraDTO.setN_contenedor(costePedidoCompra.getNContenedor());
        costePedidoCompraDTO.setArancel(costePedidoCompra.getArancel());
        costePedidoCompraDTO.setSanidad(costePedidoCompra.getSanidad());
        costePedidoCompraDTO.setPlastico(costePedidoCompra.getPlastico());
        costePedidoCompraDTO.setCarga(costePedidoCompra.getCarga());
        costePedidoCompraDTO.setInland(costePedidoCompra.getInland());
        costePedidoCompraDTO.setMuellaje(costePedidoCompra.getMuellaje());
        costePedidoCompraDTO.setPif(costePedidoCompra.getPif());
        costePedidoCompraDTO.setDespacho(costePedidoCompra.getDespacho());
        costePedidoCompraDTO.setConexiones(costePedidoCompra.getConexiones());
        costePedidoCompraDTO.setIva(costePedidoCompra.getIva());
        costePedidoCompraDTO.setDec_iva(costePedidoCompra.getDec_iva());
        costePedidoCompraDTO.setTasa_sanitaria(costePedidoCompra.getTasa_sanitaria());
        costePedidoCompraDTO.setSuma_costes(costePedidoCompra.getSuma_costes());
        costePedidoCompraDTO.setGasto_total(costePedidoCompra.getGasto_total());

        return costePedidoCompraDTO;
    }

    // Convertir DTO a Entidad
    private CostePedidoCompra convertirAEntidad(CostesDTO costePedidoCompraDTO) {
        CostePedidoCompra costePedidoCompra = new CostePedidoCompra();
        costePedidoCompra.setIdCosteCompra(costePedidoCompraDTO.getIdCosteCompra());
        costePedidoCompra.setPedidoCompra(pedidoCompraRepository.findById(costePedidoCompraDTO.getIdPedidoCompra()).orElse(null));
        costePedidoCompra.setNOperacion(costePedidoCompraDTO.getN_operacion());
        costePedidoCompra.setNContenedor(costePedidoCompraDTO.getN_contenedor());
        costePedidoCompra.setArancel(costePedidoCompraDTO.getArancel());
        costePedidoCompra.setSanidad(costePedidoCompraDTO.getSanidad());
        costePedidoCompra.setPlastico(costePedidoCompraDTO.getPlastico());
        costePedidoCompra.setCarga(costePedidoCompraDTO.getCarga());
        costePedidoCompra.setInland(costePedidoCompraDTO.getInland());
        costePedidoCompra.setMuellaje(costePedidoCompraDTO.getMuellaje());
        costePedidoCompra.setPif(costePedidoCompraDTO.getPif());
        costePedidoCompra.setDespacho(costePedidoCompraDTO.getDespacho());
        costePedidoCompra.setConexiones(costePedidoCompraDTO.getConexiones());
        costePedidoCompra.setIva(costePedidoCompraDTO.getIva());
        costePedidoCompra.setDec_iva(costePedidoCompraDTO.getDec_iva());
        costePedidoCompra.setTasa_sanitaria(costePedidoCompraDTO.getTasa_sanitaria());
        costePedidoCompra.setSuma_costes(costePedidoCompraDTO.getSuma_costes());
        costePedidoCompra.setGasto_total(costePedidoCompraDTO.getGasto_total());

        return costePedidoCompra;
    }

    // Crear un nuevo coste
    public CostesDTO crearCoste(CostesDTO costesDTO) {
        CostePedidoCompra costePedidoCompra = convertirAEntidad(costesDTO);
        costePedidoRepository.save(costePedidoCompra);
        costePedidoCompra.setTasa_sanitaria(calculoService.calcularTasaSanitaria(costePedidoCompra.getPedidoCompra().getIdPedidoCompra()).getBody());
        costePedidoCompra.setGasto_total(calculoService.calcularGastoTotal(costePedidoCompra.getPedidoCompra().getIdPedidoCompra()).getBody());
        costePedidoCompra.setSuma_costes(calculoService.calcularSumaCostes(costePedidoCompra.getPedidoCompra().getIdPedidoCompra()).getBody());
        System.out.println(costePedidoCompra.getTasa_sanitaria());
        return convertirADTO(costePedidoCompra);
    }

    // Obtener un coste por ID
    public CostesDTO obtenerCostePorId(Long id) {
        Optional<CostePedidoCompra> costePedidoCompra = costePedidoRepository.findById(id);
        return costePedidoCompra.map(this::convertirADTO).orElse(null);
    }

    public Page<CostesDTO> listarCostes(Pageable pageable, String search, List<String> searchFields) {
        Specification<CostePedidoCompra> spec = Specification.where(null);

        // Lógica de búsqueda
        if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
            Specification<CostePedidoCompra> searchSpec = Specification.where(null);
            for (String field : searchFields) {
                if (field.contains(".")) {
                    // Campo anidado (ej. pedidoCompra.idPedidoCompra)
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
                } else if (isNumericField(field)) {
                    // Campos numéricos
                    try {
                        BigDecimal value = new BigDecimal(search);
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

        return costePedidoRepository.findAll(spec, pageable).map(this::convertirADTO);
    }

    // Método helper para verificar si una cadena es numérica
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método helper para determinar si un campo es numérico
    private boolean isNumericField(String fieldName) {
        // Lista de campos numéricos en CostePedidoCompra
        List<String> numericFields = Arrays.asList(
                "idCosteCompra", "nOperacion", "arancel", "sanidad", "plastico",
                "carga", "inland", "muellaje", "pif", "despacho", "conexiones",
                "iva", "tasa_sanitaria", "suma_costes", "gasto_total"
        );
        return numericFields.contains(fieldName);
    }

    // Actualizar un coste
    public CostesDTO actualizarCoste(Long id, CostesDTO costesDTO) {
        Optional<CostePedidoCompra> costePedidoExistente = costePedidoRepository.findById(id);

        CostePedidoCompra costePedidoCompra;

       if (costePedidoExistente.isPresent()){
           costePedidoCompra = costePedidoExistente.get();
       }else costePedidoCompra = new CostePedidoCompra();

       costePedidoCompra.setPedidoCompra(pedidoCompraRepository.findById(costesDTO.getIdPedidoCompra()).orElse(null));
       costePedidoCompra.setNOperacion(costesDTO.getN_operacion());
       costePedidoCompra.setNContenedor(costesDTO.getN_contenedor());
       costePedidoCompra.setArancel(costesDTO.getArancel());
       costePedidoCompra.setSanidad(costesDTO.getSanidad());
       costePedidoCompra.setPlastico(costesDTO.getPlastico());
       costePedidoCompra.setCarga(costesDTO.getCarga());
       costePedidoCompra.setInland(costesDTO.getInland());
       costePedidoCompra.setMuellaje(costesDTO.getMuellaje());
       costePedidoCompra.setPif(costesDTO.getPif());
       costePedidoCompra.setDespacho(costesDTO.getDespacho());
       costePedidoCompra.setConexiones(costesDTO.getConexiones());
       costePedidoCompra.setIva(costesDTO.getIva());
       costePedidoCompra.setDec_iva(costesDTO.getDec_iva());
       costePedidoCompra.setTasa_sanitaria(costesDTO.getTasa_sanitaria());
       costePedidoCompra.setGasto_total(costesDTO.getGasto_total());
       costePedidoRepository.save(costePedidoCompra);
       //Calculamos los costes
       calculoService.calcularSumaCostes(costesDTO.getIdPedidoCompra()).getBody();
       calculoService.calcularTasaSanitaria(costesDTO.getIdPedidoCompra());
       calculoService.calcularGastoTotal(costesDTO.getIdPedidoCompra());
       return convertirADTO(costePedidoCompra);
    }

    @Transactional
    public void eliminarCoste(Long id) {
        costePedidoRepository.deleteById(id);
    }

    public Optional<CostesDTO> obtenerCostePedidoCompra(Long idPedidoCompra) {
        return costePedidoRepository.findById(idPedidoCompra)
                .map(this::convertirADTO);
    }
}
