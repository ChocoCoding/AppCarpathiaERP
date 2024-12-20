package com.app.microservicio.ventas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8708")
                .allowedMethods("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS","HEAD")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}