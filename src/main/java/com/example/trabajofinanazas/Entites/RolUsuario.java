package com.example.trabajofinanazas.Entites;

public enum RolUsuario {
    EMISOR("Emisor"),
    BONISTA("Bonista");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
