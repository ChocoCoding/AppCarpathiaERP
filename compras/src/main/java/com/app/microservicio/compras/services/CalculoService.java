package com.app.microservicio.compras.services;

import com.app.microservicio.compras.entities.CostePedidoCompra;
import com.app.microservicio.compras.entities.DatosBarcoPedidoCompra;
import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        CostePedidoCompra costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        BigDecimal pesoNetoTotal = BigDecimal.valueOf(0.0);
        if (pedidoCompraDet !=null){
            pesoNetoTotal = lineaPedidoCompraRepository.sumPesoNetoByPedidoCompraId(pedidoCompraId);
            pedidoCompraDet.setPesoNetoTotal(pesoNetoTotal);
            pedidoCompraDetRepository.save(pedidoCompraDet);
        }if (costePedidoCompra!=null){
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
            // Actualizar el total de bultos en PedidoCompraDet
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
        BigDecimal promedio = new BigDecimal(0);
            BigDecimal valorCompraTotal = lineaPedidoCompraRepository.sumValorCompraByPedidoCompraId(idPedidoCompra);
            if (valorCompraTotal != null){
                BigDecimal pesoNetoTotal = lineaPedidoCompraRepository.sumPesoNetoByPedidoCompraId(idPedidoCompra);
                promedio = valorCompraTotal.divide(pesoNetoTotal,4, RoundingMode.HALF_UP);

            }
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
            tasaSanitaria = pesoNetoTotal.multiply(BigDecimal.valueOf(0.005962));
            costePedidoCompra.get().setTasa_sanitaria(tasaSanitaria);
            costePedidoRepository.save(costePedidoCompra.get());
        }
        return ResponseEntity.ok(tasaSanitaria);
    }

    public ResponseEntity<BigDecimal> calcularGastoTotal(Long idPedidoCompra){
        Optional<CostePedidoCompra> costePedidoCompra = costePedidoRepository.findByIdPedidoCompra(idPedidoCompra);
        BigDecimal gastoCompraTotal = new BigDecimal("0.0");
        if(costePedidoCompra.isPresent()){
            BigDecimal valorCompraTotal = lineaPedidoCompraRepository.sumValorCompraByPedidoCompraId(idPedidoCompra);
            BigDecimal sumaCostes = costePedidoRepository.sumaCostes(idPedidoCompra);
            gastoCompraTotal = valorCompraTotal.subtract(sumaCostes);
            costePedidoCompra.get().setGasto_total(gastoCompraTotal);
            costePedidoRepository.save(costePedidoCompra.get());
        }
        return ResponseEntity.ok(gastoCompraTotal);
    }
}
