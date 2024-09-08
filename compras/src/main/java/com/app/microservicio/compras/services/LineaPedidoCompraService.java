package com.app.microservicio.compras.services;

import com.app.microservicio.compras.DTO.EliminarPorLineaDTO;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.PedidoCompraDetRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public List<LineaPedidoCompraDTO> obtenerTodasLasLineasPedidoCompra() {
        // Supongamos que tienes un repositorio que devuelve la lista de LineaPedidoCompraDTO
        return lineaPedidoCompraRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }


    public List<LineaPedidoCompraDTO> getLineasByPedidoCompra(Long idPedidoCompra) {
        return lineaPedidoCompraRepository.findByIdPedidoCompra(idPedidoCompra)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public LineaPedidoCompraDTO crearLineaPedido(LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        return convertirADTO(lineaPedidoCompraRepository.save(convertirAEntidad(lineaPedidoCompraDTO)));
    }

    @Transactional
    public LineaPedidoCompraDTO actualizarLineaPedido(Long idNumeroLinea, LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        Optional<LineaPedidoCompra> optionalLinea = lineaPedidoCompraRepository.findById(idNumeroLinea);

        if (optionalLinea.isPresent()) {
            LineaPedidoCompra lineaPedidoCompra = optionalLinea.get();

            // Actualizar los campos con los valores del DTO
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

    @Transactional
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
