package com.app.frontend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModuloController {


    // Inyectar las URLs desde el archivo application.properties
    @Value("${modulo.url.compras}")
    private String comprasUrl;

    @Value("${modulo.url.ventas}")
    private String ventasUrl;

    @Value("${modulo.url.usuarios}")
    private String usuariosUrl;

    @Value("${modulo.view.modulo-compras}")
    private String moduloComprasView;

    @Value("${modulo.view.modulo-ventas}")
    private String moduloVentasView;

    @Value("${modulo.view.modulo-usuarios}")
    private String moduloUsuariosView;

    // Usar las rutas inyectadas en los métodos del controlador
    @GetMapping("${modulo.url.compras}")
    public String gestionarCompras() {
        return moduloComprasView;
    }

    @GetMapping("${modulo.url.ventas}")
    public String gestionarVentas() {
        return moduloVentasView;
    }

    @GetMapping("${modulo.url.usuarios}")
    public String gestionarUsuarios() {
        return moduloUsuariosView;
    }
}
