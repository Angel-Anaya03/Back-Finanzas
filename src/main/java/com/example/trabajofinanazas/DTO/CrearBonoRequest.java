package com.example.trabajofinanazas.DTO;

import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrearBonoRequest {
    private Integer idUsuario;
    private String nombreBono;
    private BigDecimal valorNominal;
    private BigDecimal valorComercial;
    private BigDecimal tasaCupon;
    private String tiempoTasa;
    private String frecuenciaPago;
    private Integer plazo;
    private BigDecimal porcentajeTasa;
    private BigDecimal primaRedencion;
    private BigDecimal estructuracion;
    private BigDecimal colocacion;
    private BigDecimal cavali;
    private String tipoGracia;
    @Max(value = 2, message = "El período de gracia debe ser máximo 2")
    private Integer periodoGracia;
    private Integer idConfiguracion; // Si necesitas asignar una configuración específica
}
