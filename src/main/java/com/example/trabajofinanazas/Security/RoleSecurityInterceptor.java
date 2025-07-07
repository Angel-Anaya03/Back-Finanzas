// Archivo: RoleSecurityInterceptor.java (CORREGIDO)
package com.example.trabajofinanazas.Security;

import com.example.trabajofinanazas.Entites.RolUsuario;
import com.example.trabajofinanazas.Entites.Usuario;
import com.example.trabajofinanazas.Services.UsuarioService; // Asegúrate que el import sea el correcto
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class RoleSecurityInterceptor implements HandlerInterceptor {

    @Autowired
    private UsuarioService usuarioService; // O Susuario, dependiendo de tu implementación

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Ignorar peticiones OPTIONS (pre-flight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        if (requireRole == null) {
            return true; // No hay restricción de rol, permitir acceso.
        }

        String userIdStr = request.getHeader("userId");
        if (userIdStr == null) {
            userIdStr = request.getParameter("userId");
        }

        if (userIdStr == null) {
            sendErrorResponse(response, "Usuario no autenticado. Falta el header 'userId'.", 401);
            return false;
        }

        try {
            Integer userId = Integer.parseInt(userIdStr);
            // **CORREGIDO**: Se eliminó ".orElse(null)" para que coincida con el tipo de retorno esperado (Usuario, no Optional<Usuario>).
            Usuario usuario = usuarioService.obtenerUsuarioPorId(userId);

            if (usuario == null) {
                sendErrorResponse(response, "Usuario no encontrado", 404);
                return false;
            }

            RolUsuario[] rolesPermitidos = requireRole.value();
            if (Arrays.stream(rolesPermitidos).noneMatch(rol -> rol.equals(usuario.getRol()))) {
                sendErrorResponse(response, "No tienes permisos para acceder a este recurso", 403);
                return false;
            }

            request.setAttribute("currentUser", usuario);
            return true;

        } catch (NumberFormatException e) {
            sendErrorResponse(response, "ID de usuario inválido", 400);
            return false;
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("status", status);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
