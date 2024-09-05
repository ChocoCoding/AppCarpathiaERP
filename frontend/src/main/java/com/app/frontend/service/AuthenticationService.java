package com.app.frontend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthenticationService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${modulo.auth.backend.url}")
    private String backendAuthUrl;

    @Value("${modulo.auth.header.content-type}")
    private String contentTypeHeader;

    @Value("${modulo.auth.header.content-type.value}")
    private String contentTypeValue;

    @Value("${modulo.auth.header.authorization}")
    private String authorizationHeader;

    @Value("${modulo.auth.auth.basic}")
    private String basicAuthPrefix;

    // Inyección de la plantilla del cuerpo del JSON
    @Value("${modulo.auth.body.template}")
    private String authBodyTemplate;

    public boolean authenticateWithBackend(String username, String password) {

        // Crear encabezado de autenticación básica
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = basicAuthPrefix + " " + encodedAuth;

        // Reemplazar las variables dinámicas en la plantilla del cuerpo
        String requestBody = authBodyTemplate
                .replace("{username}", username)
                .replace("{password}", password);

        // Construir la solicitud HTTP
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(backendAuthUrl))
                .header(contentTypeHeader, contentTypeValue)
                .header(authorizationHeader, authHeader)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificar si la autenticación fue exitosa
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
