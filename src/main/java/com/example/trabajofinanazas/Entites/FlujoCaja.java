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
@Table(name = "flujo_caja")
public class FlujoCaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idFlujo")
    private Integer idFlujo;

    @Column(name = "nroPeriodo")
    private Integer nroPeriodo;

    @Column(name = "tasaCupon", precision = 15, scale = 7)
    private BigDecimal tasaCupon;

    @Column(name = "amortizacion", precision = 15, scale = 2)
    private BigDecimal amortizacion;

    @Column(name = "cavali", precision = 15, scale = 2)
    private BigDecimal cavali;

    @Column(name = "valorComercial", precision = 15, scale = 2)
    private BigDecimal valorComercial;

    @Column(name = "primaRedencion", precision = 15, scale = 2)
    private BigDecimal primaRedencion;

    @Column(name = "tcea", precision = 15, scale = 2)
    private BigDecimal tcea;

    @ManyToOne
    @JoinColumn(name = "Bonos_idBono", referencedColumnName = "idBono")
    private Bonos bono;
}
