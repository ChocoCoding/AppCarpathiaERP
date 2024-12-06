package com.app.microservicio.compras.controllers;

import com.app.microservicio.compras.exceptions.OperacionExistenteException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        if (ex.getMessage().contains("Duplicate entry")) {
            if (ex.getMessage().contains("pedidos_compra_det")) {
                response.put("error", "El detalle ya existe.");
            } else if (ex.getMessage().contains("costes_compra")) {
                response.put("error", "El coste ya existe.");
            } else if (ex.getMessage().contains("datos_barco")) {
                response.put("error", "Los datos del barco para ese numero de pedido ya existen.");
            }
            else {
                response.put("error", "El registro ya existe.");
            }
        } else {
            response.put("error", "Error de integridad en la base de datos.");
        }
        return ResponseEntity.badRequest().body(response);
    }

    // Manejar la excepción personalizada
    @ExceptionHandler(OperacionExistenteException.class)
    public ResponseEntity<Map<String, String>> handleOperacionExistenteException(OperacionExistenteException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409 Conflict
    }
}