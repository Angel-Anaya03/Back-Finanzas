package com.example.trabajofinanazas.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Integer idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private String mensaje;
}
