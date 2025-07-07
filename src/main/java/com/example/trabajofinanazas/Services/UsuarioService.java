package com.example.trabajofinanazas.Services;

import com.example.trabajofinanazas.Entites.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private Susuario susuario;

    public Usuario obtenerUsuarioPorId(Integer id) {
        return susuario.obtenerUsuarioPorId(id).orElse(null);
    }
}
