package com.example.trabajofinanazas.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RoleSecurityInterceptor roleSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(roleSecurityInterceptor)
                .addPathPatterns("/api/**") // Aplica a todas las rutas de la API
                // **CORREGIDO: Excluir las rutas públicas correctamente**
                .excludePathPatterns(
                        "/api/usuarios/login", // Excluir el login
                        "/api/usuarios"        // Excluir la creación de usuarios (POST a /api/usuarios)
                );
    }
}