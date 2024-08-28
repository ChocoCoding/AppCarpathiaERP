package com.app.microservicio.compras.services;

import com.app.microservicio.compras.DTO.EliminarPorLineaDTO;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.app.microservicio.compras.entities.LineaPedidoCompra;
import com.app.microservicio.compras.repository.LineaPedidoCompraRepository;
import com.app.microservicio.compras.DTO.LineaPedidoCompraDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineaPedidoCompraService {

    @Autowired
    private LineaPedidoCompraRepository lineaPedidoCompraRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    public List<LineaPedidoCompraDTO> listarLineasPedidosCompra() {
        return lineaPedidoCompraRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<LineaPedidoCompraDTO> listarLineasPedidoCompra(Long idPedidoCompra) {
        return lineaPedidoCompraRepository.findLineasPedidoCompra(idPedidoCompra).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public Optional<LineaPedidoCompraDTO> obtenerLineaPedidoCompra(Long idPedidoCompra, Long nLinea) {
        return lineaPedidoCompraRepository.findByNLineaAndIdPedidoCompra(nLinea,idPedidoCompra)
                .map(this::convertirADTO);
    }

    public LineaPedidoCompraDTO guardarLineaPedidoCompra(LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        LineaPedidoCompra lineaPedidoCompra = convertirAEntidad(lineaPedidoCompraDTO);
        LineaPedidoCompra nuevaLineaPedidoCompra = lineaPedidoCompraRepository.save(lineaPedidoCompra);
        return convertirADTO(nuevaLineaPedidoCompra);
    }

    @Transactional
    public void eliminarLineaPedidoCompra(Long idPedidoCompra, Long nLinea) {
        lineaPedidoCompraRepository.deleteByNLineaAndIdPedidoCompra(nLinea,idPedidoCompra);
    }

    @Transactional
    public LineaPedidoCompraDTO actualizarLineaPedidoCompra(Long idPedidoCompra, Long nLinea, LineaPedidoCompraDTO lineaPedidoCompraDTO) {
        Optional<LineaPedidoCompra> optionalLinea = lineaPedidoCompraRepository.findByNLineaAndIdPedidoCompra(nLinea, idPedidoCompra);

        if (optionalLinea.isPresent()) {
            LineaPedidoCompra lineaPedidoCompra = optionalLinea.get();

            // Actualizar los campos con los valores del DTO
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
            return convertirADTO(lineaActualizada);
        } else {
            throw new EntityNotFoundException("LineaPedidoCompra no encontrada para el ID de pedido y número de línea especificados.");
        }
    }



    private LineaPedidoCompraDTO convertirADTO(LineaPedidoCompra lineaPedidoCompra) {
        LineaPedidoCompraDTO lineaPedidoCompraDTO = new LineaPedidoCompraDTO();
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
