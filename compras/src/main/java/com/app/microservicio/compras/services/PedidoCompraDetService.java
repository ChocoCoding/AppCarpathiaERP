package com.app.microservicio.compras.services;
import com.app.microservicio.compras.repository.LineaPedidoCompraRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.PedidoCompraDetRepository;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public Optional<PedidoCompraDetDTO> obtenerPedidoCompraDet(Long idPedidoCompra) {
        return pedidoCompraDetRepository.findById(idPedidoCompra)
                .map(this::convertirADTO);
    }

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
        return pedidoCompraDet;
    }

    public List<PedidoCompraDetDTO> listarPedidosCompraDet() {
        return pedidoCompraDetRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarPedidoCompraDet(Long idPedidoCompraDet) {
        pedidoCompraDetRepository.deleteById(idPedidoCompraDet);
    }

    public PedidoCompraDetDTO crearPedidoCompraDet(PedidoCompraDetDTO pedidoCompraDetDTO) {
         Long idPedidoCompra = pedidoCompraDetDTO.getIdPedidoCompra();
         BigDecimal pesoNetoTotal = lineaPedidoCompraRepository.sumPesoNetoByPedidoCompraId(idPedidoCompra);
         Long totalBultos = lineaPedidoCompraRepository.sumBultosByPedidoCompraId(idPedidoCompra);
         pedidoCompraDetDTO.setPesoNetoTotal(pesoNetoTotal);
         pedidoCompraDetDTO.setTotalBultos(totalBultos);
        //Calculamos el promedio
        pedidoCompraDetDTO.setValorCompraTotal(calculoService.calcularValoresCompra(pedidoCompraDetDTO.getIdPedidoCompra()).getBody());
        pedidoCompraDetDTO.setPromedio(calculoService.calcularPromedio(pedidoCompraDetDTO.getIdPedidoCompra()).getBody());
    return convertirADTO(pedidoCompraDetRepository.save(convertirAEntidad(pedidoCompraDetDTO)));
    }
}

