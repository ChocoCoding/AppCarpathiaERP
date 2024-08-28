package com.app.frontend.controllers;

import com.app.frontend.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
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
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model,HttpSession session) {
        System.out.println("Received username: " + username);
        System.out.println("Received password: " + password);
        boolean isAuthenticated = authenticationService.authenticateWithBackend(username, password);

        if (isAuthenticated) {
            session.setAttribute("authenticated", true); // Marca al usuario como autenticado
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Usuario o contraseña no válidos");
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute("authenticated") == null) {
            return "redirect:/login"; // Redirige a la página de login si no está autenticado
        }
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();  // Invalida la sesión
        return "redirect:/login";  // Redirige a la página de login
    }
}
