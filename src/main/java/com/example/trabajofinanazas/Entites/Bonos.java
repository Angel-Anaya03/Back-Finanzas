package com.example.trabajofinanazas.Entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bonos")
public class Bonos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idBono")
    private Integer idBono;

    @Column(name = "nombreBono", length = 20)
    private String nombreBono;

    @Column(name = "valorNominal", precision = 10, scale = 2)
    @DecimalMin(value = "1000.00", message = "El valor nominal debe ser mínimo 1,000")
    @DecimalMax(value = "6000.00", message = "El valor nominal debe ser máximo 6,000")
    @NotNull(message = "El valor nominal es obligatorio")
    private BigDecimal valorNominal;

    @Column(name = "valorComercial", precision = 10, scale = 2)
    private BigDecimal valorComercial;

    @Column(name = "tasaCupon", precision = 5, scale = 2)
    private BigDecimal tasaCupon;

    @Column(name = "tiempoTasa", length = 20)
    private String tiempoTasa;

    @Column(name = "frecuenciaPago", length = 20)
    private String frecuenciaPago;

    @Column(name = "plazo")
    @Min(value = 1, message = "El plazo debe ser mínimo 1 año")
    @Max(value = 20, message = "El plazo debe ser máximo 20 años")
    @NotNull(message = "El plazo es obligatorio")
    private Integer plazo;

    @Column(name = "porcentajeTasa", precision = 5, scale = 2)
    @DecimalMin(value = "0.00", message = "El porcentaje de tasa debe ser mínimo 0%")
    @DecimalMax(value = "9.00", message = "El porcentaje de tasa debe ser máximo 9%")
    @NotNull(message = "El porcentaje de tasa es obligatorio")
    private BigDecimal porcentajeTasa;

    @Column(name = "primaRedencion", precision = 5, scale = 2)
    private BigDecimal primaRedencion;

    @Column(name = "estructuracion", precision = 5, scale = 2)
    private BigDecimal estructuracion;

    @Column(name = "colocacion", precision = 5, scale = 2)
    private BigDecimal colocacion;

    @Column(name = "cavali", precision = 5, scale = 2)
    private BigDecimal cavali;

    @Column(name = "tipoGracia", length = 20)
    private String tipoGracia;

    @Column(name = "periodoGracia")
    @Max(value = 2, message = "El período de gracia debe ser máximo 2")
    private Integer periodoGracia;

    @Column(name = "fechaCreacion")
    @Temporal(TemporalType.DATE)
    private Date fechaCreacion;



    @ManyToOne
    @JoinColumn(name = "Configuracion_idConfiguracion", referencedColumnName = "idConfiguracion")
    private Configuracion configuracion;

    @OneToMany(mappedBy = "bono", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlujoCaja> flujosCaja;

    @OneToMany(mappedBy = "bono", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ResultadosFinancieros> resultadosFinancieros;
}
