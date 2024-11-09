package com.app.microservicio.compras.services;

import com.app.microservicio.compras.DTO.CostesDTO;
import com.app.microservicio.compras.DTO.DatosBarcoDTO;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;
import com.app.microservicio.compras.entities.CostePedidoCompra;
import com.app.microservicio.compras.entities.DatosBarcoPedidoCompra;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.DatosBarcoRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DatosBarcoPedidoCompraService {

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private DatosBarcoRepository datosBarcoRepository;

    public Optional<DatosBarcoDTO> obtenerDatoBarco(Long idPedidoCompra) {
        return datosBarcoRepository.findById(idPedidoCompra)
                .map(this::convertirADTO);
    }

    public Page<DatosBarcoDTO> listarDatosBarco(Pageable pageable, String search, List<String> searchFields) {
        Specification<DatosBarcoPedidoCompra> spec = Specification.where(null);

        // Lógica de búsqueda
        if (search != null && !search.isEmpty() && searchFields != null && !searchFields.isEmpty()) {
            Specification<DatosBarcoPedidoCompra> searchSpec = Specification.where(null);
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
                } else if (field.equals("idDatosBarco") || field.equals("nOperacion") || field.equals("flete")) {
                    // Campos numéricos
                    try {
                        Long value = Long.parseLong(search);
                        searchSpec = searchSpec.or((root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get(field), value)
                        );
                    } catch (NumberFormatException e) {
                        // Ignorar si no es numérico
                    }
                } else if (field.equals("fechaEmbarque") || field.equals("fechaLlegada") || field.equals("fechaPagoFlete")) {
                    // Campos de fecha
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

        return datosBarcoRepository.findAll(spec, pageable).map(this::convertirADTO);
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

    @Transactional
    public void eliminarDatosBarco(Long idDatosBarco) {
        datosBarcoRepository.deleteById(idDatosBarco);
    }

    public DatosBarcoDTO crearDatosBarco(DatosBarcoDTO datosBarcoDTO) {
        return convertirADTO(datosBarcoRepository.save(convertirAEntidad(datosBarcoDTO)));
    }


    public DatosBarcoDTO actualizarDatosBarco(Long id, DatosBarcoDTO datosBarcoDTO) {
        // Verificar si ya existe un registro con el mismo ID
        Optional<DatosBarcoPedidoCompra> existente = datosBarcoRepository.findById(id);

        DatosBarcoPedidoCompra datosBarcoPedidoCompra;
        if (existente.isPresent()) {
            // Si el registro existe, actualizarlo
            datosBarcoPedidoCompra = existente.get();
        } else {
            // Si no existe, crear uno nuevo
            datosBarcoPedidoCompra = new DatosBarcoPedidoCompra();
        }

        // Actualizar campos
        datosBarcoPedidoCompra.setPedidoCompra(pedidoCompraRepository.findById(datosBarcoDTO.getIdPedidoCompra()).orElse(null));
        datosBarcoPedidoCompra.setN_operacion(datosBarcoDTO.getN_operacion());
        datosBarcoPedidoCompra.setNombreBarco(datosBarcoDTO.getNombreBarco());
        datosBarcoPedidoCompra.setViaje(datosBarcoDTO.getViaje());
        datosBarcoPedidoCompra.setNaviera(datosBarcoDTO.getNaviera());
        datosBarcoPedidoCompra.setPuertoEmbarque(datosBarcoDTO.getPuertoEmbarque());
        datosBarcoPedidoCompra.setPuertoLlegada(datosBarcoDTO.getPuertoLlegada());
        datosBarcoPedidoCompra.setFecha_embarque(datosBarcoDTO.getFecha_embarque());
        datosBarcoPedidoCompra.setFecha_llegada(datosBarcoDTO.getFecha_llegada());
        datosBarcoPedidoCompra.setFlete(datosBarcoDTO.getFlete());
        datosBarcoPedidoCompra.setFecha_pago_flete(datosBarcoDTO.getFecha_pago_flete());
        datosBarcoPedidoCompra.setFactura_flete(datosBarcoDTO.getFacturaFlete());

        return convertirADTO(datosBarcoRepository.save(datosBarcoPedidoCompra));
    }



    // Convertir Entidad a DTO
    private DatosBarcoDTO convertirADTO(DatosBarcoPedidoCompra datosBarcoPedidoCompra) {
        DatosBarcoDTO datosBarcoDTO = new DatosBarcoDTO();
        datosBarcoDTO.setIdDatosBarco(datosBarcoPedidoCompra.getIdDatosBarco());
        datosBarcoDTO.setIdPedidoCompra(datosBarcoPedidoCompra.getPedidoCompra().getIdPedidoCompra());
        datosBarcoDTO.setN_operacion(datosBarcoPedidoCompra.getN_operacion());
        datosBarcoDTO.setNombreBarco(datosBarcoPedidoCompra.getNombreBarco());
        datosBarcoDTO.setViaje(datosBarcoPedidoCompra.getViaje());
        datosBarcoDTO.setNaviera(datosBarcoPedidoCompra.getNaviera());
        datosBarcoDTO.setPuertoEmbarque(datosBarcoPedidoCompra.getPuertoEmbarque());
        datosBarcoDTO.setPuertoLlegada(datosBarcoPedidoCompra.getPuertoLlegada());
        datosBarcoDTO.setFecha_embarque(datosBarcoPedidoCompra.getFecha_embarque());
        datosBarcoDTO.setFecha_llegada(datosBarcoPedidoCompra.getFecha_llegada());
        datosBarcoDTO.setFlete(datosBarcoPedidoCompra.getFlete());
        datosBarcoDTO.setFecha_pago_flete(datosBarcoPedidoCompra.getFecha_pago_flete());
        datosBarcoDTO.setFacturaFlete(datosBarcoPedidoCompra.getFactura_flete());

        return datosBarcoDTO;
    }

    // Convertir DTO a Entidad
    private DatosBarcoPedidoCompra convertirAEntidad(DatosBarcoDTO datosBarcoDTO) {
        DatosBarcoPedidoCompra datosBarcoPedidoCompra = new DatosBarcoPedidoCompra();

        datosBarcoPedidoCompra.setIdDatosBarco(datosBarcoDTO.getIdDatosBarco());
        datosBarcoPedidoCompra.setPedidoCompra(pedidoCompraRepository.findById(datosBarcoDTO.getIdPedidoCompra()).orElse(null));
        datosBarcoPedidoCompra.setN_operacion(datosBarcoDTO.getN_operacion());
        datosBarcoPedidoCompra.setNombreBarco(datosBarcoDTO.getNombreBarco());
        datosBarcoPedidoCompra.setViaje(datosBarcoDTO.getViaje());
        datosBarcoPedidoCompra.setNaviera(datosBarcoDTO.getNaviera());
        datosBarcoPedidoCompra.setPuertoEmbarque(datosBarcoDTO.getPuertoEmbarque());
        datosBarcoPedidoCompra.setPuertoLlegada(datosBarcoDTO.getPuertoLlegada());
        datosBarcoPedidoCompra.setFecha_embarque(datosBarcoDTO.getFecha_embarque());
        datosBarcoPedidoCompra.setFecha_llegada(datosBarcoDTO.getFecha_llegada());
        datosBarcoPedidoCompra.setFlete(datosBarcoDTO.getFlete());
        datosBarcoPedidoCompra.setFecha_pago_flete(datosBarcoDTO.getFecha_pago_flete());
        datosBarcoPedidoCompra.setFactura_flete(datosBarcoDTO.getFacturaFlete());

        return datosBarcoPedidoCompra;
    }
}
