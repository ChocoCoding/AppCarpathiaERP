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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public List<DatosBarcoDTO> listarDatosBarco() {
        return datosBarcoRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
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
