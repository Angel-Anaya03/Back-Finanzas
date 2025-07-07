package com.example.trabajofinanazas.Services;

import com.example.trabajofinanazas.Entites.Usuario;
import com.example.trabajofinanazas.Repositorio.Rusuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class Susuario {
    
    @Autowired
    private Rusuario rusuario;
    
    // Crear usuario
    public Usuario crearUsuario(Usuario usuario) {
        usuario.setFechaCreacion(new Date());
        return rusuario.save(usuario);
    }
    
    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosUsuarios() {
        return rusuario.findAll();
    }
    
    // Obtener usuario por ID
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return rusuario.findById(id);
    }
    
    // Actualizar usuario
    public Usuario actualizarUsuario(Usuario usuario) {
        return rusuario.save(usuario);
    }
    
    // Eliminar usuario
    public void eliminarUsuario(Integer id) {
        rusuario.deleteById(id);
    }
    
    // Verificar si existe usuario por email
    public boolean existeUsuarioPorEmail(String email) {
        return rusuario.existsByEmail(email);
    }
    
    // Buscar usuario por email
    public Optional<Usuario> buscarPorEmail(String email) {
        return rusuario.findByEmail(email);
    }
    
    // Validar usuario (para login)
    public Usuario validarUsuario(String email, String password) {
        Optional<Usuario> usuarioOpt = rusuario.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Aquí comparamos directamente las contraseñas (en producción deberías usar encriptación)
            if (usuario.getPassword().equals(password)) {
                return usuario;
            }
        }
        return null;
    }
}
