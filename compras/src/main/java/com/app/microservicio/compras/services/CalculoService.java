package com.app.microservicio.compras.services;

import com.app.microservicio.compras.entities.*;
import com.app.microservicio.compras.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CalculoService{

    @Autowired
    private LineaPedidoCompraRepository lineaPedidoCompraRepository;

    @Autowired
    private PedidoCompraDetRepository pedidoCompraDetRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;

    @Autowired
    private CostePedidoRepository costePedidoRepository;

    @Autowired
    private DatosBarcoRepository datosBarcoRepository;


    public ResponseEntity<BigDecimal> recalcularPesoNetoTotal(Long pedidoCompraId) {
        BigDecimal pesoNetoTotal = BigDecimal.ZERO;

        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        CostePedidoCompra costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);

        if (pedidoCompraDet != null) {
            pesoNetoTotal = lineaPedidoCompraRepository.sumPesoNetoByPedidoCompraId(pedidoCompraId);
            pedidoCompraDet.setPesoNetoTotal(pesoNetoTotal);
            pedidoCompraDetRepository.save(pedidoCompraDet);
        }

        if (costePedidoCompra != null) {
            calcularTasaSanitaria(pedidoCompraId);
        }

        return ResponseEntity.ok(pesoNetoTotal);
    }


    public ResponseEntity<Integer> recalcularTotalBultos(Long pedidoCompraId) {
        int totalBultos = 0;
        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        try {
            totalBultos =  Math.toIntExact(lineaPedidoCompraRepository.sumBultosByPedidoCompraId(pedidoCompraId));
        }catch (NullPointerException n){
            if (pedidoCompraDet!= null){
                pedidoCompraDet.setTotalBultos((long) totalBultos);
                pedidoCompraDetRepository.save(pedidoCompraDet);
                return ResponseEntity.ok(totalBultos);
            }
        }
        if (pedidoCompraDet!= null){
            pedidoCompraDet.setTotalBultos((long) totalBultos);
            pedidoCompraDetRepository.save(pedidoCompraDet);
        }
        return ResponseEntity.ok(totalBultos);
    }

    public ResponseEntity<BigDecimal> recalcularValoresCompra(Long pedidoCompraId) {
        // Sumar los bultos
        BigDecimal totalValoresCompra = lineaPedidoCompraRepository.sumValorCompraByPedidoCompraId(pedidoCompraId);
        CostePedidoCompra costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        System.out.println("Estoy calculando la compra " + totalValoresCompra);
        // Actualizar el total de bultos en PedidoCompraDet
        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        if (pedidoCompraDet!= null){
            pedidoCompraDet.setValorCompraTotal(totalValoresCompra);
            pedidoCompraDetRepository.save(pedidoCompraDet);
            if (costePedidoCompra != null){
                calcularGastoTotal(pedidoCompraId);
            }
        }
        return ResponseEntity.ok(totalValoresCompra);
    }

    public ResponseEntity<BigDecimal> recalcularPromedio(Long idPedidoCompra) {
        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(idPedidoCompra).orElse(null);

        BigDecimal promedio = new BigDecimal(0);
        if (pedidoCompraDet!= null){
            BigDecimal valorCompraTotal = pedidoCompraDet.getValorCompraTotal();
            if (valorCompraTotal != null){
                BigDecimal pesoNetoTotal = pedidoCompraDet.getPesoNetoTotal();
                try{
                    promedio = valorCompraTotal.divide(pesoNetoTotal,4, RoundingMode.HALF_UP);
                }catch (ArithmeticException e){
                    pedidoCompraDet.setPromedio(new BigDecimal(0));
                    return ResponseEntity.ok(new BigDecimal(0));
                }
                pedidoCompraDet.setPromedio(promedio);
                pedidoCompraDetRepository.save(pedidoCompraDet);

            }
        }
        return ResponseEntity.ok(promedio);
    }

    public ResponseEntity<BigDecimal> calcularPromedio(Long idPedidoCompra) {
        BigDecimal promedio = BigDecimal.ZERO;

        // Obtener el valor total de la compra
        BigDecimal valorCompraTotal = lineaPedidoCompraRepository.sumValorCompraByPedidoCompraId(idPedidoCompra);
        if (valorCompraTotal == null) {
            valorCompraTotal = BigDecimal.ZERO; // Asignar 0 si el valor es null
        }

        // Obtener el peso neto total
        BigDecimal pesoNetoTotal = lineaPedidoCompraRepository.sumPesoNetoByPedidoCompraId(idPedidoCompra);
        if (pesoNetoTotal == null || pesoNetoTotal.compareTo(BigDecimal.ZERO) == 0) {
            // Si el peso neto es nulo o cero, no se puede dividir, retorna el promedio como 0
            return ResponseEntity.ok(BigDecimal.ZERO);
        }

        // Calcular el promedio
        promedio = valorCompraTotal.divide(pesoNetoTotal, 6, RoundingMode.HALF_UP);

        return ResponseEntity.ok(promedio);
    }


    public ResponseEntity<BigDecimal> calcularValoresCompra(Long pedidoCompraId) {
        // Sumar los bultos
        BigDecimal totalValoresCompra = lineaPedidoCompraRepository.sumValorCompraByPedidoCompraId(pedidoCompraId);
        return ResponseEntity.ok(totalValoresCompra);
    }

    public ResponseEntity<BigDecimal> calcularSumaCostes(Long idPedidoCompra) {
        BigDecimal sumaCostes = new BigDecimal("0.0");
        CostePedidoCompra costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(idPedidoCompra).orElse(null);
        if (costePedidoCompra != null){
            sumaCostes = costePedidoRepository.sumaCostes(idPedidoCompra);
            costePedidoCompra.setSuma_costes(sumaCostes);
            costePedidoRepository.save(costePedidoCompra);
        }

        return ResponseEntity.ok(sumaCostes);
    }

    public ResponseEntity<BigDecimal> calcularTasaSanitaria(Long idPedidoCompra){
        Optional<CostePedidoCompra> costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(idPedidoCompra);
        BigDecimal tasaSanitaria = new BigDecimal("0.0");
        if(costePedidoCompra.isPresent()){
            BigDecimal pesoNetoTotal = lineaPedidoCompraRepository.sumPesoNetoByPedidoCompraId(idPedidoCompra);
            if(pesoNetoTotal != null){
                tasaSanitaria = pesoNetoTotal.multiply(BigDecimal.valueOf(0.005962));
                costePedidoCompra.get().setTasa_sanitaria(tasaSanitaria);
                costePedidoRepository.save(costePedidoCompra.get());
            }
        }
        return ResponseEntity.ok(tasaSanitaria);
    }

    public ResponseEntity<BigDecimal> calcularGastoTotal(Long idPedidoCompra){
        Optional<CostePedidoCompra> costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(idPedidoCompra);


        BigDecimal gastoCompraTotal = BigDecimal.ZERO;
        if(costePedidoCompra.isPresent()){
            BigDecimal valorCompraTotal = Optional.ofNullable(lineaPedidoCompraRepository.sumValorCompraByPedidoCompraId(idPedidoCompra))
                    .orElse(BigDecimal.ZERO);
            BigDecimal sumaCostes = Optional.ofNullable(costePedidoRepository.sumaCostes(idPedidoCompra))
                    .orElse(BigDecimal.ZERO);

            gastoCompraTotal = valorCompraTotal.add(sumaCostes);
            costePedidoCompra.get().setGasto_total(gastoCompraTotal);
            costePedidoRepository.save(costePedidoCompra.get());

        }
        return ResponseEntity.ok(gastoCompraTotal);
    }

    public ResponseEntity<BigDecimal> calcularValorCompraTotalLinea(Long idLineaPedidoCompra) {
        Optional<LineaPedidoCompra> lineaPedidoCompraOpt = lineaPedidoCompraRepository.findById(idLineaPedidoCompra);
        BigDecimal valorPedidoCompra = BigDecimal.ZERO;

        if (lineaPedidoCompraOpt.isPresent()) {
            LineaPedidoCompra lineaPedidoCompra = lineaPedidoCompraOpt.get();

            // Manejo de nulos: si alguno de los valores es null, se asigna BigDecimal.ZERO por defecto
            BigDecimal pesoNeto = lineaPedidoCompra.getPNeto() != null ? lineaPedidoCompra.getPNeto() : BigDecimal.ZERO;
            BigDecimal precio = lineaPedidoCompra.getPrecio() != null ? lineaPedidoCompra.getPrecio() : BigDecimal.ZERO;
            valorPedidoCompra = pesoNeto.multiply(precio);
            lineaPedidoCompra.setValorCompra(valorPedidoCompra);
            lineaPedidoCompraRepository.save(lineaPedidoCompra);
        }

        return ResponseEntity.ok(valorPedidoCompra);
    }

    public void actualizarCamposLineaPedido(Long id){
        List<LineaPedidoCompra> lineaPedidoCompras = lineaPedidoCompraRepository.findByIdPedidoCompra(id);
        if (!lineaPedidoCompras.isEmpty()){
            Optional<PedidoCompra> pedidoCompra = pedidoCompraRepository.findById(id);
            for(LineaPedidoCompra l: lineaPedidoCompras){
                l.setNContenedor(pedidoCompra.get().getNContenedor());
                l.setNOperacion(pedidoCompra.get().getNOperacion());
                lineaPedidoCompraRepository.save(l);
            }
        }
    }
    public void actualizarCamposPedidoCompraDet(Long id) {
        Optional<PedidoCompraDet> pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(id);
        Optional<PedidoCompra> pedidoCompra = pedidoCompraRepository.findById(id);
        if (pedidoCompraDet.isPresent()){
            pedidoCompraDet.get().setNOperacion(pedidoCompra.get().getNOperacion());
            pedidoCompraDetRepository.save(pedidoCompraDet.get());
        }
    }

    public DatosBarcoPedidoCompra actualizarCamposDatosBarco(Long id) {
        Optional<DatosBarcoPedidoCompra> datosBarcoPedidoCompra = datosBarcoRepository.findByIdPedidoCompra(id);
        Optional<PedidoCompra> pedidoCompra = pedidoCompraRepository.findById(id);
        if (datosBarcoPedidoCompra.isPresent()){
            datosBarcoPedidoCompra.get().setNOperacion(pedidoCompra.get().getNOperacion());
            datosBarcoPedidoCompra.get().setNContenedor(pedidoCompra.get().getNContenedor());
           return datosBarcoRepository.save(datosBarcoPedidoCompra.get());
        }
        return null;
    }

    public CostePedidoCompra actualizarCostePedidoCompra(Long id) {
        Optional<CostePedidoCompra> costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(id);
        Optional<PedidoCompra> pedidoCompra = pedidoCompraRepository.findById(id);
        if (costePedidoCompra.isPresent()){
            costePedidoCompra.get().setNOperacion(pedidoCompra.get().getNOperacion());
            costePedidoCompra.get().setNContenedor(pedidoCompra.get().getNContenedor());
            return costePedidoRepository.save(costePedidoCompra.get());
        }
        return null;
    }

    public void  actualizarStatusLineasPedidoCompra(Long idPedidoCompra, char nuevoStatus){
        List<LineaPedidoCompra> lineas = lineaPedidoCompraRepository.findByIdPedidoCompra(idPedidoCompra);
        for (LineaPedidoCompra linea : lineas) {
            linea.setStatus(nuevoStatus);
            lineaPedidoCompraRepository.save(linea);
        }
    }

    public void actualizarStatusPedidoCompraDet(Long idPedidoCompra, char nuevoStatus){
        Optional<PedidoCompraDet> pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(idPedidoCompra);
        if (pedidoCompraDet.isPresent()){
            pedidoCompraDet.get().setStatus(nuevoStatus);
            pedidoCompraDetRepository.save(pedidoCompraDet.get());
        }
    }

    public void  actualizarStatusDatosBarco(Long idPedidoCompra, char nuevoStatus){
        Optional<DatosBarcoPedidoCompra> datosBarcoPedidoCompra = datosBarcoRepository.findByIdPedidoCompra(idPedidoCompra);
        if (datosBarcoPedidoCompra.isPresent()){
            datosBarcoPedidoCompra.get().setStatus(nuevoStatus);
            datosBarcoRepository.save(datosBarcoPedidoCompra.get());
        }
    }

    public void  actualizarStatusCosteCompra(Long idPedidoCompra, char nuevoStatus){
        Optional<CostePedidoCompra> costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(idPedidoCompra);
        if (costePedidoCompra.isPresent()){
            costePedidoCompra.get().setStatus(nuevoStatus);
            costePedidoRepository.save(costePedidoCompra.get());
        }
    }

}
