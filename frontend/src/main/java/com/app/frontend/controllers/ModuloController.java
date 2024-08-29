package com.app.frontend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModuloController {

    @GetMapping("/compras")
    public String gestionarCompras() {
        return "modulo_compras";
    }

    @GetMapping("/ventas")
    public String gestionarVentas() {
        return "modulo_ventas";
    }

    @GetMapping("/usuarios")
    public String gestionarUsuarios() {
        return "modulo_usuarios";
    }
}