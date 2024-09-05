package com.app.frontend.controllers;

import com.app.frontend.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

    @Autowired
    private AuthenticationService authenticationService;

    @Value("${modulo.login.view.login}")
    private String loginView;

    @Value("${modulo.login.view.home}")
    private String homeView;

    // Inyección de propiedades para las cadenas de texto
    @Value("${modulo.login.error.invalid-credentials}")
    private String invalidCredentialsError;

    @Value("${modulo.login.session.authenticated}")
    private String authenticatedSessionAttr;

    @Value("${modulo.login.error.key}")
    private String errorKey;

    // Inyección de propiedades para los endpoints
    @Value("${modulo.login.url.login}")
    private String loginUrl;

    @Value("${modulo.login.url.home}")
    private String homeUrl;

    @Value("${modulo.login.url.authenticate}")
    private String authenticateUrl;

    @Value("${modulo.login.url.root}")
    private String rootUrl;

    @Value("${modulo.login.url.logout}")
    private String logoutUrl;

    // Inyección de propiedades para los redirect
    @Value("${modulo.login.redirect.login}")
    private String loginRedirect;

    @Value("${modulo.login.redirect.home}")
    private String homeRedirect;

    @GetMapping({ "${modulo.login.url.login}", "${modulo.login.url.root}" })
    public String login() {
        return loginView;
    }

    @PostMapping("${modulo.login.url.authenticate}")
    public String authenticate(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        boolean isAuthenticated = authenticationService.authenticateWithBackend(username, password);

        if (isAuthenticated) {
            session.setAttribute(authenticatedSessionAttr, true);  // Marca al usuario como autenticado
            return homeRedirect;
        } else {
            model.addAttribute(errorKey, invalidCredentialsError);
            return loginView;
        }
    }

    @GetMapping("${modulo.login.url.home}")
    public String home(HttpSession session) {
        if (session.getAttribute(authenticatedSessionAttr) == null) {
            return loginRedirect;  // Redirige a la página de login si no está autenticado
        }
        return homeView;
    }

    @GetMapping("${modulo.login.url.logout}")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();  // Invalida la sesión
        return loginRedirect;  // Redirige a la página de login
    }
}
