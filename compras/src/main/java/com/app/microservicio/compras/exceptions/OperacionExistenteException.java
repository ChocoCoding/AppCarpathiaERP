package com.app.microservicio.compras.exceptions;

public class OperacionExistenteException extends RuntimeException{
    public OperacionExistenteException(String message) {
        super(message);
}
}
