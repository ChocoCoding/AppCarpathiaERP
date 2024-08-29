package com.app.microservicio.compras.services;

import com.app.microservicio.compras.entities.PedidoCompra;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.PedidoCompraDetRepository;
import com.app.microservicio.compras.DTO.PedidoCompraDetDTO;

import java.sql.Date;
import java.util.Optional;

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
        PedidoCompraDet pedidoCompraDet = convertirAEntidad(pedidoCompraDetDTO);
        PedidoCompraDet nuevoPedidoCompraDet = pedidoCompraDetRepository.save(pedidoCompraDet);
        return convertirADTO(nuevoPedidoCompraDet);
    }

    public PedidoCompraDet actualizarPedidoCompraDet(Long id, PedidoCompraDetDTO pedidoCompraDetDTO) {
        Optional<PedidoCompraDet> pedidoCompraDetOpt = pedidoCompraDetRepository.findById(id);

        if (pedidoCompraDetOpt.isPresent()) {
            PedidoCompraDet pedidoCompraDet = pedidoCompraDetOpt.get();

            // Actualizar los campos con los valores del DTO
            pedidoCompraDet.setContratoCompra(pedidoCompraDetDTO.getContratoCompra());
            pedidoCompraDet.setTerminado(pedidoCompraDetDTO.getTerminado());
            pedidoCompraDet.setFactProveedor(pedidoCompraDetDTO.getFactProveedor());
            pedidoCompraDet.setNFactFlete(pedidoCompraDetDTO.getN_fact_flete());

            if (pedidoCompraDetDTO.getFecha_pago_flete() != null) {
                pedidoCompraDet.setFechaPagoFlete(Date.valueOf(pedidoCompraDetDTO.getFecha_pago_flete()));
            }

            pedidoCompraDet.setNBl(pedidoCompraDetDTO.getN_bl());
            pedidoCompraDet.setPesoNetoTotal(pedidoCompraDetDTO.getPesoNetoTotal());
            pedidoCompraDet.setTotalBultos(pedidoCompraDetDTO.getTotalBultos());
            pedidoCompraDet.setPromedio(pedidoCompraDetDTO.getPromedio());
            pedidoCompraDet.setValorCompraTotal(pedidoCompraDetDTO.getValorCompraTotal());
            pedidoCompraDet.setObservaciones(pedidoCompraDetDTO.getObservaciones());

            // Guardar los cambios en la base de datos
            return pedidoCompraDetRepository.save(pedidoCompraDet);
        } else {
            throw new RuntimeException("PedidoCompraDet con ID " + id + " no encontrado.");
        }
    }



    private PedidoCompraDetDTO convertirADTO(PedidoCompraDet pedidoCompraDet) {
        PedidoCompraDetDTO pedidoCompraDetDTO = new PedidoCompraDetDTO();
        pedidoCompraDetDTO.setIdPedidoCompra(pedidoCompraDet.getPedidoCompra().getIdPedidoCompra());
        pedidoCompraDetDTO.setN_operacion(pedidoCompraDet.getNOperacion());
        pedidoCompraDetDTO.setContratoCompra(pedidoCompraDet.getContratoCompra());
        pedidoCompraDetDTO.setTerminado(pedidoCompraDet.getTerminado());
        pedidoCompraDetDTO.setFactProveedor(pedidoCompraDet.getFactProveedor());
        pedidoCompraDetDTO.setN_fact_flete(pedidoCompraDet.getNFactFlete());
        pedidoCompraDetDTO.setFecha_pago_flete(String.valueOf(pedidoCompraDet.getFechaPagoFlete()));
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
        pedidoCompraDet.setFechaPagoFlete(Date.valueOf(pedidoCompraDetDTO.getFecha_pago_flete()));
        pedidoCompraDet.setNBl(pedidoCompraDetDTO.getN_bl());
        pedidoCompraDet.setPesoNetoTotal(pedidoCompraDetDTO.getPesoNetoTotal());
        pedidoCompraDet.setTotalBultos(pedidoCompraDetDTO.getTotalBultos());
        pedidoCompraDet.setPromedio(pedidoCompraDetDTO.getPromedio());
        pedidoCompraDet.setValorCompraTotal(pedidoCompraDetDTO.getValorCompraTotal());
        pedidoCompraDet.setObservaciones(pedidoCompraDetDTO.getObservaciones());
        return pedidoCompraDet;
    }
}
