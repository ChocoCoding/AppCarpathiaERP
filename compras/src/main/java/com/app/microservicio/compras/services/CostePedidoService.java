package com.app.microservicio.compras.services;

import com.app.microservicio.compras.DTO.CostesDTO;
import com.app.microservicio.compras.entities.CostePedidoCompra;
import com.app.microservicio.compras.repository.CostePedidoRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
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

    // Obtener todos los costes
    public List<CostesDTO> obtenerTodosLosCostes() {
        List<CostePedidoCompra> listaCostes = costePedidoRepository.findAll();
        return listaCostes.stream().map(this::convertirADTO).collect(Collectors.toList());
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
