package com.app.microservicio.compras.services;

import com.app.microservicio.compras.entities.LineaPedidoCompra;
import com.app.microservicio.compras.entities.PedidoCompra;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.PedidoCompraDetRepository;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;

import java.sql.Date;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoCompraDetService {

    @Autowired
    private PedidoCompraDetRepository pedidoCompraDetRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    public Optional<PedidoCompraDetDTO> obtenerPedidoCompraDet(Long idPedidoCompra) {
        return pedidoCompraDetRepository.findById(idPedidoCompra)
                .map(this::convertirADTO);
    }

    public PedidoCompraDetDTO guardarPedidoCompraDet(PedidoCompraDetDTO pedidoCompraDetDTO) {
        return convertirADTO(pedidoCompraDetRepository.save(convertirAEntidad(pedidoCompraDetDTO)));
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
    return convertirADTO(pedidoCompraDetRepository.save(convertirAEntidad(pedidoCompraDetDTO)));
    }
}

