package com.app.microservicio.ventas.services;

import com.app.microservicio.ventas.entities.LineaPedidoVenta;
import com.app.microservicio.ventas.entities.PedidoVenta;
import com.app.microservicio.ventas.entities.PedidoVentaDet;
import com.app.microservicio.ventas.repository.LineaPedidoVentaRepository;
import com.app.microservicio.ventas.repository.PedidoVentaDetRepository;
import com.app.microservicio.ventas.repository.PedidoVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CalculoService {
    @Autowired
    private LineaPedidoVentaRepository lineaPedidoVentaRepository;

    @Autowired
    private PedidoVentaDetRepository pedidoVentaDetRepository;

    @Autowired
    private PedidoVentaRepository pedidoVentaRepository;

    public ResponseEntity<BigDecimal> recalcularPesoNetoTotal(Long pedidoVentaId) {
        BigDecimal pesoNetoTotal = BigDecimal.ZERO;

        PedidoVentaDet pedidoVentaDet = pedidoVentaDetRepository.findByIdPedidoVenta(pedidoVentaId).orElse(null);


        if (pedidoVentaDet != null) {
            pesoNetoTotal = lineaPedidoVentaRepository.sumPesoNetoByPedidoVentaId(pedidoVentaId);
            pedidoVentaDet.setPesoNetoTotal(pesoNetoTotal);
            pedidoVentaDetRepository.save(pedidoVentaDet);
        }

        return ResponseEntity.ok(pesoNetoTotal);
    }


    public ResponseEntity<Integer> recalcularTotalBultos(Long pedidoVentaId) {
        int totalBultos = 0;
        PedidoVentaDet pedidoVentaDet = pedidoVentaDetRepository.findByIdPedidoVenta(pedidoVentaId).orElse(null);
        try {
            totalBultos =  Math.toIntExact(lineaPedidoVentaRepository.sumBultosByPedidoVentaId(pedidoVentaId));
        }catch (NullPointerException n){
            if (pedidoVentaDet!= null){
                pedidoVentaDet.setTotalBultos((long) totalBultos);
                pedidoVentaDetRepository.save(pedidoVentaDet);
                return ResponseEntity.ok(totalBultos);
            }
        }
        if (pedidoVentaDet!= null){
            pedidoVentaDet.setTotalBultos((long) totalBultos);
            pedidoVentaDetRepository.save(pedidoVentaDet);
        }
        return ResponseEntity.ok(totalBultos);
    }

    public ResponseEntity<BigDecimal> recalcularValoresVenta(Long pedidoVentaId) {
        // Sumar los bultos
        BigDecimal totalValoresVenta = lineaPedidoVentaRepository.sumValorVentaByPedidoVentaId(pedidoVentaId);
        System.out.println("Estoy calculando la venta " + totalValoresVenta);
        // Actualizar el total de bultos en PedidoVentaDet
        PedidoVentaDet pedidoVentaDet = pedidoVentaDetRepository.findByIdPedidoVenta(pedidoVentaId).orElse(null);
        if (pedidoVentaDet!= null){
            pedidoVentaDet.setValorVentaTotal(totalValoresVenta);
            pedidoVentaDetRepository.save(pedidoVentaDet);
                calcularGastoTotal(pedidoVentaId);
        }
        return ResponseEntity.ok(totalValoresVenta);
    }

    public ResponseEntity<BigDecimal> calcularPrecioVenta(Long pedidoVentaId) {
        // Sumar los bultos
        BigDecimal totalPrecioVenta = lineaPedidoVentaRepository.sumPrecioVentaByPedidoVentaId(pedidoVentaId);
        return ResponseEntity.ok(totalPrecioVenta);
    }

    public ResponseEntity<BigDecimal> recalcularPrecioTotalVenta(Long pedidoVentaId) {
        // Sumar los bultos
        BigDecimal totalPrecioVenta = calcularPrecioVenta(pedidoVentaId).getBody();
        System.out.println("Estoy calculando la venta " + totalPrecioVenta);
        // Actualizar el total de bultos en PedidoVentaDet
        PedidoVentaDet pedidoVentaDet = pedidoVentaDetRepository.findByIdPedidoVenta(pedidoVentaId).orElse(null);
        if (pedidoVentaDet!= null){
            pedidoVentaDet.setPrecioTotal(totalPrecioVenta);
            pedidoVentaDetRepository.save(pedidoVentaDet);
        }
        return ResponseEntity.ok(totalPrecioVenta);
    }

    public ResponseEntity<BigDecimal> recalcularPromedio(Long idPedidoVenta) {
        PedidoVentaDet pedidoVentaDet = pedidoVentaDetRepository.findByIdPedidoVenta(idPedidoVenta).orElse(null);

        BigDecimal promedio = new BigDecimal(0);
        if (pedidoVentaDet!= null){
            BigDecimal valorVentaTotal = pedidoVentaDet.getValorVentaTotal();
            if (valorVentaTotal != null){
                BigDecimal pesoNetoTotal = pedidoVentaDet.getPesoNetoTotal();
                try{
                    promedio = valorVentaTotal.divide(pesoNetoTotal,4, RoundingMode.HALF_UP);
                }catch (ArithmeticException e){
                    pedidoVentaDet.setPromedio(new BigDecimal(0));
                    return ResponseEntity.ok(new BigDecimal(0));
                }
                pedidoVentaDet.setPromedio(promedio);
                pedidoVentaDetRepository.save(pedidoVentaDet);

            }
        }
        return ResponseEntity.ok(promedio);
    }

    public ResponseEntity<BigDecimal> calcularPromedio(Long idPedidoVenta) {
        BigDecimal promedio = BigDecimal.ZERO;

        // Obtener el valor total de la venta
        BigDecimal valorVentaTotal = lineaPedidoVentaRepository.sumValorVentaByPedidoVentaId(idPedidoVenta);
        if (valorVentaTotal == null) {
            valorVentaTotal = BigDecimal.ZERO; // Asignar 0 si el valor es null
        }

        // Obtener el peso neto total
        BigDecimal pesoNetoTotal = lineaPedidoVentaRepository.sumPesoNetoByPedidoVentaId(idPedidoVenta);
        if (pesoNetoTotal == null || pesoNetoTotal.compareTo(BigDecimal.ZERO) == 0) {
            // Si el peso neto es nulo o cero, no se puede dividir, retorna el promedio como 0
            return ResponseEntity.ok(BigDecimal.ZERO);
        }

        // Calcular el promedio
        promedio = valorVentaTotal.divide(pesoNetoTotal, 6, RoundingMode.HALF_UP);

        return ResponseEntity.ok(promedio);
    }


    public ResponseEntity<BigDecimal> calcularValoresVenta(Long pedidoVentaId) {
        // Sumar los bultos
        BigDecimal totalValoresVenta = lineaPedidoVentaRepository.sumValorVentaByPedidoVentaId(pedidoVentaId);
        return ResponseEntity.ok(totalValoresVenta);
    }

    public ResponseEntity<BigDecimal> calcularGastoTotal(Long idPedidoVenta){

            BigDecimal valorVentaTotal = Optional.ofNullable(lineaPedidoVentaRepository.sumValorVentaByPedidoVentaId(idPedidoVenta))
                    .orElse(BigDecimal.ZERO);

        return ResponseEntity.ok(valorVentaTotal);
    }




    public ResponseEntity<BigDecimal> calcularValorVentaTotalLinea(Long idLineaPedidoVenta) {
        Optional<LineaPedidoVenta> lineaPedidoVentaOpt = lineaPedidoVentaRepository.findById(idLineaPedidoVenta);
        BigDecimal valorPedidoVenta = BigDecimal.ZERO;

        if (lineaPedidoVentaOpt.isPresent()) {
            LineaPedidoVenta lineaPedidoVenta = lineaPedidoVentaOpt.get();

            // Manejo de nulos: si alguno de los valores es null, se asigna BigDecimal.ZERO por defecto
            BigDecimal pesoNeto = lineaPedidoVenta.getPNeto() != null ? lineaPedidoVenta.getPNeto() : BigDecimal.ZERO;
            BigDecimal precio = lineaPedidoVenta.getPrecio() != null ? lineaPedidoVenta.getPrecio() : BigDecimal.ZERO;
            valorPedidoVenta = pesoNeto.multiply(precio);
            lineaPedidoVenta.setValorVenta(valorPedidoVenta);
            lineaPedidoVentaRepository.save(lineaPedidoVenta);
        }
        return ResponseEntity.ok(valorPedidoVenta);
    }

    public void actualizarCamposLineaPedido(Long id){
        System.out.println("Llego aqu´i");
        List<LineaPedidoVenta> lineaPedidoVentas = lineaPedidoVentaRepository.findByIdPedidoVenta(id);
        if (!lineaPedidoVentas.isEmpty()){
            Optional<PedidoVenta> pedidoVenta = pedidoVentaRepository.findById(id);
            for(LineaPedidoVenta l: lineaPedidoVentas){
                l.setNOperacion(pedidoVenta.get().getNOperacion());
                l.setNContenedor(pedidoVenta.get().getNContenedor());
                lineaPedidoVentaRepository.save(l);
            }
        }
    }

}
