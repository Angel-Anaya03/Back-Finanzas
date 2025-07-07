package com.example.trabajofinanazas.Controller;

import com.example.trabajofinanazas.DTO.CrearUsuarioRequest;
import com.example.trabajofinanazas.DTO.LoginRequest;
import com.example.trabajofinanazas.DTO.LoginResponse;
import com.example.trabajofinanazas.Entites.RolUsuario;
import com.example.trabajofinanazas.Entites.Usuario;
import com.example.trabajofinanazas.Services.Susuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {
    
    @Autowired
    private Susuario susuario;
    
    // Crear usuario
    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request, BindingResult bindingResult) {
        try {
            // Verificar errores de validación
            if (bindingResult.hasErrors()) {
                StringBuilder errores = new StringBuilder("Errores de validación: ");
                for (FieldError error : bindingResult.getFieldErrors()) {
                    errores.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
                }
                return ResponseEntity.badRequest().body(errores.toString());
            }
            
            // Verificar si el email ya existe
            if (susuario.existeUsuarioPorEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body("El email ya está registrado");
            }
            
            // Validar y convertir el rol
            RolUsuario rolUsuario;
            try {
                rolUsuario = RolUsuario.valueOf(request.getRol().toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body("Rol inválido. Usa: EMISOR o BONISTA");
            }
            
            // Crear el usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setEmail(request.getEmail());
            usuario.setPassword(request.getPassword());
            usuario.setRol(rolUsuario);
            
            Usuario usuarioCreado = susuario.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
        } catch (Exception e) {
            e.printStackTrace(); // Para debug
            return ResponseEntity.badRequest()
                .body("Error al crear usuario: " + e.getMessage());
        }
    }
    
    // Login de usuario
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuario = susuario.validarUsuario(loginRequest.getEmail(), loginRequest.getPassword());
            if (usuario != null) {
                // Crear respuesta de login exitoso (sin la contraseña por seguridad)
                LoginResponse response = new LoginResponse();
                response.setIdUsuario(usuario.getIdUsuario());
                response.setNombre(usuario.getNombre());
                response.setApellido(usuario.getApellido());
                response.setEmail(usuario.getEmail());
                response.setRol(usuario.getRol().toString());
                response.setMensaje("Login exitoso");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error en el login: " + e.getMessage());
        }
    }
    
    // Obtener roles disponibles (endpoint útil para el frontend)
    @GetMapping("/roles")
    public ResponseEntity<?> obtenerRoles() {
        return ResponseEntity.ok(new String[]{"EMISOR", "BONISTA"});
    }
    
    // Obtener todos los usuarios
    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerTodosUsuarios() {
        List<Usuario> usuarios = susuario.obtenerTodosUsuarios();
        return ResponseEntity.ok(usuarios);
    }
    
    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<Usuario> usuario = susuario.obtenerUsuarioPorId(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @Valid @RequestBody Usuario usuario) {
        try {
            Optional<Usuario> usuarioExistente = susuario.obtenerUsuarioPorId(id);
            if (usuarioExistente.isPresent()) {
                usuario.setIdUsuario(id);
                usuario.setFechaCreacion(usuarioExistente.get().getFechaCreacion());
                Usuario usuarioActualizado = susuario.actualizarUsuario(usuario);
                return ResponseEntity.ok(usuarioActualizado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error al actualizar usuario: " + e.getMessage());
        }
    }
    
    // Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            Optional<Usuario> usuario = susuario.obtenerUsuarioPorId(id);
            if (usuario.isPresent()) {
                susuario.eliminarUsuario(id);
                return ResponseEntity.ok("Usuario eliminado correctamente");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Error al eliminar usuario: " + e.getMessage());
        }
    }
}
