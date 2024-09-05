package com.app.microservicio.compras.services;

import com.app.microservicio.compras.entities.PedidoCompraDet;
import com.app.microservicio.compras.repository.LineaPedidoCompraRepository;
import com.app.microservicio.compras.repository.PedidoCompraDetRepository;
import com.app.microservicio.compras.repository.PedidoCompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculoService{

    @Autowired
    private LineaPedidoCompraRepository lineaPedidoCompraRepository;

    @Autowired
    private PedidoCompraDetRepository pedidoCompraDetRepository;

    @Autowired
    private PedidoCompraRepository pedidoCompraRepository;


    public ResponseEntity<Integer> recalcularTotalBultos(Long pedidoCompraId) {
        // Sumar los bultos
        int totalBultos = Math.toIntExact(lineaPedidoCompraRepository.sumBultosByPedidoCompraId(pedidoCompraId));

        // Actualizar el total de bultos en PedidoCompraDet
        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        if (pedidoCompraDet!= null){
            pedidoCompraDet.setTotalBultos((long) totalBultos);
            pedidoCompraDetRepository.save(pedidoCompraDet);
        }
        return ResponseEntity.ok(totalBultos);
    }

    public ResponseEntity<BigDecimal> recalcularValoresCompra(Long pedidoCompraId) {
        // Sumar los bultos
        BigDecimal totalValoresCompra = lineaPedidoCompraRepository.sumValorCompraByPedidoCompraId(pedidoCompraId);

        // Actualizar el total de bultos en PedidoCompraDet
        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(pedidoCompraId).orElse(null);
        if (pedidoCompraDet!= null){
            pedidoCompraDet.setValorCompraTotal(totalValoresCompra);
            pedidoCompraDetRepository.save(pedidoCompraDet);
        }
        return ResponseEntity.ok(totalValoresCompra);
    }

    public ResponseEntity<BigDecimal> recalcularPromedio(Long idPedidoCompra) {
        PedidoCompraDet pedidoCompraDet = pedidoCompraDetRepository.findByIdPedidoCompra(idPedidoCompra).orElse(null);
        BigDecimal promedio = new BigDecimal(0);
        if (pedidoCompraDet!= null){
            BigDecimal valorCompraTotal = pedidoCompraDetRepository.findValorCompraTotalByIdPedidoCompra(pedidoCompraDet.getIdPedidoCompraDet());
            if (valorCompraTotal != null){
                BigDecimal pesoNetoTotal = pedidoCompraDetRepository.findPesoNetoTotalByIdPedidoCompra(pedidoCompraDet.getIdPedidoCompraDet());

                promedio = valorCompraTotal.divide(pesoNetoTotal,4, RoundingMode.HALF_UP);

                pedidoCompraDet.setPromedio(promedio);
                pedidoCompraDetRepository.save(pedidoCompraDet);
            }
        }
        return ResponseEntity.ok(promedio);
    }


}
