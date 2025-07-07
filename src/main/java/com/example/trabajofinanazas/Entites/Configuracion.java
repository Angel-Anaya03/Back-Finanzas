package com.example.trabajofinanazas.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "configuracion")
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConfiguracion")
    private Integer idConfiguracion;

    @Column(name = "moneda", length = 10)
    private String moneda;

    @Column(name = "capitalizacion", length = 10)
    private String capitalizacion;
}
