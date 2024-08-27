package com.app.frontend.controllers;

import com.app.frontend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model) {
        // Lógica para hacer una petición POST al backend con las credenciales
        // y gestionar la respuesta
        // Si la autenticación es exitosa, redirigir a la página principal

        // Aquí se hace la petición al backend
        boolean isAuthenticated = authenticateWithBackend(username, password);

        if (isAuthenticated) {
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    private boolean authenticateWithBackend(String username, String password) {
        // Implementar la lógica para hacer la petición al backend y verificar las credenciales
        // Puedes usar RestTemplate o WebClient para hacer la llamada HTTP al backend
        return true; // Simulación de autenticación exitosa
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
