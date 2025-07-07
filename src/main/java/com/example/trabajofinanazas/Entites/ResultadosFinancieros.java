package com.example.trabajofinanazas.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resultados_financieros")
public class ResultadosFinancieros {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idResultado")
    private Integer idResultado;

    @Column(name = "tcea", precision = 15, scale = 2)
    private BigDecimal tcea;

    @Column(name = "trea", precision = 15, scale = 2)
    private BigDecimal trea;

    @ManyToOne
    @JoinColumn(name = "Bonos_idBono", referencedColumnName = "idBono")
    private Bonos bono;
}
