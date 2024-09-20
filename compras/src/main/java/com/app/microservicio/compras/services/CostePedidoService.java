package com.app.microservicio.compras.services;

import com.app.microservicio.compras.DTO.CostesDTO;
import com.app.microservicio.compras.entities.CostePedidoCompra;
import com.app.microservicio.compras.repository.CostePedidoRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CostePedidoService {

    @Autowired
    private CostePedidoRepository costePedidoRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

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
        costePedidoCompraDTO.setGasto_total(costePedidoCompra.getGasto_total());

        return costePedidoCompraDTO;
    }

    // Convertir DTO a Entidad
    private CostePedidoCompra convertirAEntidad(CostesDTO costePedidoCompraDTO) {
        CostePedidoCompra costePedidoCompra = new CostePedidoCompra();

        costePedidoCompra.setPedidoCompra(pedidoCompraRepository.findById(costePedidoCompraDTO.getIdPedidoCompra()).orElse(null));
        costePedidoCompra.setIdCosteCompra(costePedidoCompraDTO.getIdCosteCompra());
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
        costePedidoCompra.setGasto_total(costePedidoCompraDTO.getGasto_total());

        return costePedidoCompra;
    }

    // Crear un nuevo coste
    public CostesDTO crearCoste(CostesDTO costesDTO) {
        CostePedidoCompra costePedidoCompra = convertirAEntidad(costesDTO);
        CostePedidoCompra nuevoCoste = costePedidoRepository.save(costePedidoCompra);
        return convertirADTO(nuevoCoste);
    }

    // Obtener un coste por ID
    public CostesDTO obtenerCostePorId(Long id) {
        Optional<CostePedidoCompra> costePedidoCompra = costePedidoRepository.findById(id);
        return costePedidoCompra.map(this::convertirADTO).orElse(null);
    }

    // Obtener todos los costes
    public List<CostesDTO> obtenerTodosLosCostes() {
        List<CostePedidoCompra> listaCostes = costePedidoRepository.findAll();
        return listaCostes.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    // Actualizar un coste
    public CostesDTO actualizarCoste(Long id, CostesDTO costesDTO) {
        Optional<CostePedidoCompra> costePedidoExistente = costePedidoRepository.findById(id);

        if (costePedidoExistente.isPresent()) {
            CostePedidoCompra costePedidoActualizado = convertirAEntidad(costesDTO);
            costePedidoActualizado.setIdCosteCompra(id);
            return convertirADTO(costePedidoRepository.save(costePedidoActualizado));
        }
        return null;
    }

    // Eliminar un coste
    public void eliminarCoste(Long id) {
        costePedidoRepository.deleteById(id);
    }
}
