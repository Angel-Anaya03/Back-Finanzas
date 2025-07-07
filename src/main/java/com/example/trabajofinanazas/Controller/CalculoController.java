package com.example.trabajofinanazas.Controller;

import com.example.trabajofinanazas.Services.Sbono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calculos")
@CrossOrigin(origins = "*")
public class CalculoController {

    private static final Logger logger = LoggerFactory.getLogger(CalculoController.class);

    @Autowired
    private Sbono sbono;

    // Método auxiliar para convertir Objects a BigDecimal de manera segura
    private BigDecimal convertirABigDecimal(Object valor) {
        if (valor == null) {
            return null;
        }
        if (valor instanceof BigDecimal) {
            return (BigDecimal) valor;
        } else if (valor instanceof Double) {
            return BigDecimal.valueOf((Double) valor);
        } else if (valor instanceof Number) {
            return BigDecimal.valueOf(((Number) valor).doubleValue());
        } else if (valor instanceof String) {
            try {
                return new BigDecimal((String) valor);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    @GetMapping("/bonos-tes")
    public ResponseEntity<Map<String, Object>> obtenerBonosConTES() {
        try {
            logger.info("Obteniendo bonos con TES");
            List<Map<String, Object>> bonosConTES = sbono.obtenerBonosConTES();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bonosConTES);
            response.put("total", bonosConTES.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener bonos con TES: ", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/bono/{idBono}/datos-calculo")
    public ResponseEntity<Map<String, Object>> obtenerDatosCalculoBono(@PathVariable Integer idBono) {
        try {
            logger.info("Obteniendo datos de cálculo para bono ID: {}", idBono);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Map<String, Object> datosCalculo = sbono.obtenerDatosCalculoBono(idBono);
            if (datosCalculo.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", datosCalculo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener datos de cálculo para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/bono/{idBono}/numero-periodos")
    public ResponseEntity<Map<String, Object>> calcularNumeroPeriodos(@PathVariable Integer idBono) {
        try {
            logger.info("Calculando número de períodos para bono ID: {}", idBono);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Map<String, Object> datosCalculo = sbono.obtenerDatosCalculoBono(idBono);
            if (datosCalculo.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idBono", idBono);
            response.put("frecuenciaPago", datosCalculo.get("frecuenciaPago"));
            response.put("plazoAnios", datosCalculo.get("plazoAnios"));
            response.put("totalPeriodos", datosCalculo.get("totalPeriodos"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al calcular número de períodos para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/bono/{idBono}/interes")
    public ResponseEntity<Map<String, Object>> calcularInteres(@PathVariable Integer idBono) {
        try {
            logger.info("Calculando interés para bono ID: {}", idBono);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Map<String, Object> datosCalculo = sbono.obtenerDatosCalculoBono(idBono);
            if (datosCalculo.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idBono", idBono);
            response.put("valorNominal", datosCalculo.get("valorNominal"));
            response.put("tes", datosCalculo.get("tes"));
            response.put("interesPorPeriodo", datosCalculo.get("interesPorPeriodo"));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al calcular interés para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/bono/{idBono}/cuota/{periodo}")
    public ResponseEntity<Map<String, Object>> calcularCuota(
            @PathVariable Integer idBono, 
            @PathVariable Integer periodo) {
        try {
            logger.info("Calculando cuota para bono ID: {}, período: {}", idBono, periodo);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (periodo == null || periodo <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Período inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Map<String, Object> datosCalculo = sbono.obtenerDatosCalculoBono(idBono);
            if (datosCalculo.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }

            BigDecimal valorNominal = convertirABigDecimal(datosCalculo.get("valorNominal"));
            BigDecimal interes = convertirABigDecimal(datosCalculo.get("interesPorPeriodo"));
            Integer totalPeriodos = (Integer) datosCalculo.get("totalPeriodos");
            
            if (valorNominal == null || interes == null || totalPeriodos == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Datos incompletos para el cálculo");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (periodo > totalPeriodos) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Período excede el total de períodos del bono");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            BigDecimal cuota = sbono.calcularCuota(interes, valorNominal, periodo, totalPeriodos);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idBono", idBono);
            response.put("periodo", periodo);
            response.put("totalPeriodos", totalPeriodos);
            response.put("interes", interes);
            response.put("cuota", cuota);
            response.put("esUltimoPeriodo", periodo.equals(totalPeriodos));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al calcular cuota para bono {} período {}: ", idBono, periodo, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/bono/{idBono}/amortizacion/{periodo}")
    public ResponseEntity<Map<String, Object>> calcularAmortizacion(
            @PathVariable Integer idBono, 
            @PathVariable Integer periodo) {
        try {
            logger.info("Calculando amortización para bono ID: {}, período: {}", idBono, periodo);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (periodo == null || periodo <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Período inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Map<String, Object> datosCalculo = sbono.obtenerDatosCalculoBono(idBono);
            if (datosCalculo.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }

            BigDecimal valorNominal = convertirABigDecimal(datosCalculo.get("valorNominal"));
            Integer totalPeriodos = (Integer) datosCalculo.get("totalPeriodos");
            
            if (valorNominal == null || totalPeriodos == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Datos incompletos para el cálculo");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (periodo > totalPeriodos) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Período excede el total de períodos del bono");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            BigDecimal amortizacion = sbono.calcularAmortizacion(periodo, totalPeriodos, valorNominal);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idBono", idBono);
            response.put("periodo", periodo);
            response.put("totalPeriodos", totalPeriodos);
            response.put("valorNominal", valorNominal);
            response.put("amortizacion", amortizacion);
            response.put("esUltimoPeriodo", periodo.equals(totalPeriodos));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al calcular amortización para bono {} período {}: ", idBono, periodo, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/bono/{idBono}/saldo-final/{periodo}")
    public ResponseEntity<Map<String, Object>> calcularSaldoFinal(
            @PathVariable Integer idBono, 
            @PathVariable Integer periodo) {
        try {
            logger.info("Calculando saldo final para bono ID: {}, período: {}", idBono, periodo);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (periodo == null || periodo <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Período inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            Map<String, Object> datosCalculo = sbono.obtenerDatosCalculoBono(idBono);
            if (datosCalculo.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }

            BigDecimal valorNominal = convertirABigDecimal(datosCalculo.get("valorNominal"));
            Integer totalPeriodos = (Integer) datosCalculo.get("totalPeriodos");
            
            if (valorNominal == null || totalPeriodos == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Datos incompletos para el cálculo");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (periodo > totalPeriodos) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Período excede el total de períodos del bono");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            BigDecimal saldoFinal = sbono.calcularSaldoFinal(periodo, totalPeriodos, valorNominal);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("idBono", idBono);
            response.put("periodo", periodo);
            response.put("totalPeriodos", totalPeriodos);
            response.put("valorNominal", valorNominal);
            response.put("saldoFinal", saldoFinal);
            response.put("esUltimoPeriodo", periodo.equals(totalPeriodos));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al calcular saldo final para bono {} período {}: ", idBono, periodo, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/bono/{idBono}/flujo-completo")
    public ResponseEntity<Map<String, Object>> generarFlujoCompleto(@PathVariable Integer idBono) {
        try {
            logger.info("Generando flujo completo para bono ID: {}", idBono);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Obtener datos de cálculo
            Map<String, Object> datosCalculo = sbono.obtenerDatosCalculoBono(idBono);
            if (datosCalculo.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }

            // Generar flujo de caja
            List<Map<String, Object>> flujos = sbono.generarFlujoCaja(idBono);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("datosGenerales", datosCalculo);
            response.put("flujos", flujos);
            response.put("totalFlujos", flujos.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al generar flujo completo para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Actualizar la TEA de un bono específico
     * @param idBono ID del bono a modificar
     * @param nuevaTea Nueva TEA a aplicar
     * @return Respuesta con el bono actualizado y confirmación de regeneración de flujos
     */
    @PutMapping("/bono/{idBono}/actualizar-tea")
    public ResponseEntity<Map<String, Object>> actualizarTEA(
            @PathVariable Integer idBono,
            @RequestParam BigDecimal nuevaTea) {
        try {
            logger.info("Actualizando TEA para bono ID: {} con nueva TEA: {}", idBono, nuevaTea);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (nuevaTea == null || nuevaTea.compareTo(BigDecimal.ZERO) < 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "TEA inválida. Debe ser un valor positivo");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Actualizar la TEA y regenerar flujos automáticamente
            sbono.actualizarTEA(idBono, nuevaTea);
            
            // Obtener los datos actualizados para confirmación
            Map<String, Object> datosActualizados = sbono.obtenerDatosCalculoBono(idBono);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "TEA actualizada correctamente y flujos regenerados");
            response.put("idBono", idBono);
            response.put("teaAnterior", "N/A"); // No tenemos el valor anterior
            response.put("teaNueva", nuevaTea);
            response.put("datosActualizados", datosActualizados);
            response.put("flujosRegenerados", true);
            
            logger.info("TEA actualizada exitosamente para bono ID: {}", idBono);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error de negocio al actualizar TEA para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error interno al actualizar TEA para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Actualizar el Valor Nominal de un bono específico
     * @param idBono ID del bono a modificar
     * @param nuevoValorNominal Nuevo valor nominal a aplicar
     * @return Respuesta con el bono actualizado y confirmación de regeneración de flujos
     */
    @PutMapping("/bono/{idBono}/actualizar-valor-nominal")
    public ResponseEntity<Map<String, Object>> actualizarValorNominal(
            @PathVariable Integer idBono,
            @RequestParam BigDecimal nuevoValorNominal) {
        try {
            logger.info("Actualizando Valor Nominal para bono ID: {} con nuevo valor: {}", idBono, nuevoValorNominal);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (nuevoValorNominal == null || nuevoValorNominal.compareTo(BigDecimal.ZERO) <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Valor Nominal inválido. Debe ser un valor positivo mayor a cero");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Actualizar el valor nominal y regenerar flujos automáticamente
            sbono.actualizarValorNominal(idBono, nuevoValorNominal);
            
            // Obtener los datos actualizados para confirmación
            Map<String, Object> datosActualizados = sbono.obtenerDatosCalculoBono(idBono);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Valor Nominal actualizado correctamente y flujos regenerados");
            response.put("idBono", idBono);
            response.put("valorNominalAnterior", "N/A"); // No tenemos el valor anterior
            response.put("valorNominalNuevo", nuevoValorNominal);
            response.put("datosActualizados", datosActualizados);
            response.put("flujosRegenerados", true);
            
            logger.info("Valor Nominal actualizado exitosamente para bono ID: {}", idBono);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.error("Error de negocio al actualizar Valor Nominal para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Error interno al actualizar Valor Nominal para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtener el flujo completo actualizado después de modificaciones
     * Este endpoint es útil para verificar los cambios después de actualizar TEA o Valor Nominal
     * @param idBono ID del bono
     * @return Flujo de caja completo actualizado con los nuevos valores
     */
    @GetMapping("/bono/{idBono}/flujo-actualizado")
    public ResponseEntity<Map<String, Object>> obtenerFlujoActualizado(@PathVariable Integer idBono) {
        try {
            logger.info("Obteniendo flujo actualizado para bono ID: {}", idBono);
            
            if (idBono == null || idBono <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "ID de bono inválido");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Obtener datos generales del bono
            Map<String, Object> datosGenerales = sbono.obtenerDatosCalculoBono(idBono);
            if (datosGenerales.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Bono no encontrado");
                return ResponseEntity.notFound().build();
            }
            
            // Obtener flujos de caja actualizados
            List<Map<String, Object>> flujos = sbono.generarFlujoCaja(idBono);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Flujo de caja actualizado obtenido correctamente");
            response.put("idBono", idBono);
            response.put("datosGenerales", datosGenerales);
            response.put("flujos", flujos);
            response.put("totalFlujos", flujos.size());
            response.put("fechaConsulta", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener flujo actualizado para bono {}: ", idBono, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error interno del servidor");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", "healthy");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
