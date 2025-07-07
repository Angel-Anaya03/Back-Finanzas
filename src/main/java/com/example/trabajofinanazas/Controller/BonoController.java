package com.example.trabajofinanazas.Controller;

import com.example.trabajofinanazas.DTO.CrearBonoRequest;
import com.example.trabajofinanazas.Entites.Bonos;
import com.example.trabajofinanazas.Entites.FlujoCaja;
import com.example.trabajofinanazas.Entites.Usuario;
import com.example.trabajofinanazas.Entites.RolUsuario;
import com.example.trabajofinanazas.Repositorio.Rbono;
import com.example.trabajofinanazas.Services.Sbono;
import com.example.trabajofinanazas.Services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/bonos")
@CrossOrigin(origins = "*")
public class BonoController {

    @Autowired
    private Sbono sbono;

@Autowired
private com.example.trabajofinanazas.Repositorio.Rbono rbono;


    @Autowired
    private UsuarioService usuarioService;

    /**
     * Endpoint para agregar un nuevo bono a un usuario
     *
     * @param bono      Los datos del bono

     * @return El bono creado o mensaje de error
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarBono(@RequestBody Bonos bono) {
        try {
            Bonos bonoGuardado = sbono.agregarBono(bono);
            return ResponseEntity.ok(bonoGuardado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint alternativo para agregar un bono usando DTO
     *
     * @param request Los datos del bono y el ID del usuario
     * @return El bono creado o mensaje de error
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearBono(@Valid @RequestBody CrearBonoRequest request) {
        try {
            // Crear objeto Bono a partir del DTO
            Bonos bono = new Bonos();
            bono.setNombreBono(request.getNombreBono());
            bono.setValorNominal(request.getValorNominal());
            bono.setValorComercial(request.getValorComercial());
            bono.setTasaCupon(request.getTasaCupon());
            bono.setTiempoTasa(request.getTiempoTasa());
            bono.setFrecuenciaPago(request.getFrecuenciaPago());
            bono.setPlazo(request.getPlazo());
            bono.setPorcentajeTasa(request.getPorcentajeTasa());
            bono.setPrimaRedencion(request.getPrimaRedencion());
            bono.setEstructuracion(request.getEstructuracion());
            bono.setColocacion(request.getColocacion());
            bono.setCavali(request.getCavali());
            bono.setTipoGracia(request.getTipoGracia());
            bono.setPeriodoGracia(request.getPeriodoGracia());

            // Agregar el bono al usuario
            Bonos bonoGuardado = sbono.agregarBono(bono);
            return ResponseEntity.ok(bonoGuardado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }



    /**
     * Endpoint para obtener todos los bonos con TES calculado
     *
     * @return Lista de bonos con TES
     */
    @GetMapping("/con-tes")
    public ResponseEntity<List<Map<String, Object>>> obtenerBonosConTES() {
        try {
            List<Map<String, Object>> bonos = sbono.obtenerBonosConTES();
            return ResponseEntity.ok(bonos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para generar flujo de caja de un bono
     *
     * @param idBono El ID del bono
     * @return Flujo de caja del bono
     */
    @GetMapping("/{idBono}/flujo-caja")
    public ResponseEntity<List<Map<String, Object>>> generarFlujoCaja(@PathVariable Integer idBono) {
        try {
            List<Map<String, Object>> flujoCaja = sbono.generarFlujoCaja(idBono);
            return ResponseEntity.ok(flujoCaja);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener datos de cálculo de un bono
     *
     * @param idBono El ID del bono
     * @return Datos de cálculo del bono
     */
    @GetMapping("/{idBono}/datos-calculo")
    public ResponseEntity<Map<String, Object>> obtenerDatosCalculoBono(@PathVariable Integer idBono) {
        try {
            Map<String, Object> datos = sbono.obtenerDatosCalculoBono(idBono);
            return ResponseEntity.ok(datos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint para obtener un bono específico por ID
     *
     * @param idBono El ID del bono
     * @return El bono encontrado
     */
    @GetMapping("/{idBono}")
    public ResponseEntity<?> obtenerBonoPorId(@PathVariable Integer idBono) {
        try {
            Bonos bono = sbono.obtenerBonoPorId(idBono);
            return ResponseEntity.ok(bono);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para actualizar un bono existente
     *
     * @param idBono El ID del bono a actualizar
     * @param bono   Los nuevos datos del bono
     * @return El bono actualizado
     */
    @PutMapping("/{idBono}")
    public ResponseEntity<?> actualizarBono(@PathVariable Integer idBono, @RequestBody Bonos bono) {
        try {
            bono.setIdBono(idBono);
            Bonos bonoActualizado = sbono.actualizarBono(bono);
            return ResponseEntity.ok(bonoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para eliminar un bono
     *
     * @param idBono El ID del bono a eliminar
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/{idBono}")
    public ResponseEntity<?> eliminarBono(@PathVariable Integer idBono) {
        try {
            sbono.eliminarBono(idBono);
            return ResponseEntity.ok("Bono eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // ========== ENDPOINTS PARA FLUJOS DE CAJA ==========

    /**
     * Endpoint para generar automáticamente los flujos de caja de un bono y guardarlos en la BD
     *
     * @param idBono El ID del bono
     * @return Lista de flujos de caja generados y guardados
     */
    @PostMapping("/{idBono}/generar-flujos-caja")
    public ResponseEntity<?> generarFlujosCaja(@PathVariable Integer idBono) {
        try {
            List<FlujoCaja> flujos = sbono.generarYGuardarFlujosCaja(idBono);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Flujos de caja generados exitosamente",
                    "totalPeriodos", flujos.size(),
                    "flujos", flujos
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para obtener los flujos de caja guardados en la BD de un bono
     *
     * @param idBono El ID del bono
     * @return Lista de flujos de caja del bono con la TEA incluida
     */
    @GetMapping("/{idBono}/flujos-caja")
    public ResponseEntity<?> obtenerFlujosCajaBono(@PathVariable Integer idBono) {
        try {
            List<FlujoCaja> flujos = sbono.obtenerFlujosCajaPorBono(idBono);
            Bonos bono = sbono.obtenerBonoPorId(idBono);
            BigDecimal tea = bono.getPorcentajeTasa(); // TEA del bono

            if (flujos.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "mensaje", "No se encontraron flujos de caja. Genere los flujos primero.",
                        "tea", tea,
                        "flujos", flujos
                ));
            }
            return ResponseEntity.ok(Map.of(
                    "totalPeriodos", flujos.size(),
                    "tea", tea,
                    "flujos", flujos
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para crear un bono y generar automáticamente sus flujos de caja
     *
     * @param request Los datos del bono y el ID del usuario
     * @return El bono creado con sus flujos de caja generados
     */
    @PostMapping("/crear-con-flujos")
    public ResponseEntity<?> crearBonoConFlujos(@Valid @RequestBody CrearBonoRequest request) {
        try {
            // Crear objeto Bono a partir del DTO
            Bonos bono = new Bonos();
            bono.setNombreBono(request.getNombreBono());
            bono.setValorNominal(request.getValorNominal());
            bono.setValorComercial(request.getValorComercial());
            bono.setTasaCupon(request.getTasaCupon());
            bono.setTiempoTasa(request.getTiempoTasa());
            bono.setFrecuenciaPago(request.getFrecuenciaPago());
            bono.setPlazo(request.getPlazo());
            bono.setPorcentajeTasa(request.getPorcentajeTasa());
            bono.setPrimaRedencion(request.getPrimaRedencion());
            bono.setEstructuracion(request.getEstructuracion());
            bono.setColocacion(request.getColocacion());
            bono.setCavali(request.getCavali());
            bono.setTipoGracia(request.getTipoGracia());
            bono.setPeriodoGracia(request.getPeriodoGracia());

            // Agregar el bono al usuario
            Bonos bonoGuardado = sbono.agregarBono(bono);

            // Generar automáticamente los flujos de caja
            List<FlujoCaja> flujos = sbono.generarYGuardarFlujosCaja(bonoGuardado.getIdBono());

            return ResponseEntity.ok(Map.of("mensaje", "Bono creado exitosamente"));




        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "mensaje", "Error: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "mensaje", "Error interno del servidor: " + e.getMessage()
            ));

        }
    }

    /**
     * Endpoint para actualizar un bono y regenerar automáticamente sus flujos de caja
     *
     * @param bono   Los nuevos datos del bono
     * @return El bono actualizado con sus nuevos flujos de caja
     */
    @PutMapping("/{idBono}/actualizar-con-flujos")
    public ResponseEntity<?> actualizarBonoConFlujos( @RequestBody Bonos bono) {
        try {
            Bonos bonoActualizado = sbono.actualizarBonoYRegenerarFlujos(bono);

            return ResponseEntity.ok(Map.of(

            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // ========== NUEVOS ENDPOINTS PARA MODIFICAR TEA Y VALOR NOMINAL ==========

    /**
     * Endpoint para actualizar solo la TEA (Tasa Efectiva Anual) de un bono
     * y regenerar automáticamente sus flujos de caja
     *
     * @param idBono El ID del bono
     * @return El bono actualizado con sus nuevos flujos de caja
     */
    @PutMapping("/{idBono}/actualizar-tea")
    public ResponseEntity<?> actualizarTEA(@PathVariable Integer idBono, @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal nuevaTea = request.get("tea");
            if (nuevaTea == null) {
                return ResponseEntity.badRequest().body("Error: El campo 'tea' es requerido");
            }

            // Validar rango de TEA
            if (nuevaTea.compareTo(BigDecimal.ZERO) < 0 || nuevaTea.compareTo(new BigDecimal("9.00")) > 0) {
                return ResponseEntity.badRequest().body("Error: La TEA debe estar entre 0% y 9%");
            }

            Bonos bonoActualizado = sbono.actualizarTEA(idBono, nuevaTea);
            List<FlujoCaja> flujos = sbono.obtenerFlujosCajaPorBono(idBono);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "TEA actualizada exitosamente y flujos de caja regenerados",
                    "teaAnterior", request.get("teaAnterior"),
                    "teaNueva", nuevaTea,
                    "bono", bonoActualizado,
                    "totalPeriodos", flujos.size(),
                    "flujos", flujos
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para actualizar solo el Valor Nominal de un bono
     * y regenerar automáticamente sus flujos de caja
     *
     * @param idBono El ID del bono
     * @return El bono actualizado con sus nuevos flujos de caja
     */
    @PutMapping("/{idBono}/actualizar-valor-nominal")
    public ResponseEntity<?> actualizarValorNominal(@PathVariable Integer idBono, @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal nuevoValor = request.get("valorNominal");
            if (nuevoValor == null) {
                return ResponseEntity.badRequest().body("Error: El campo 'valorNominal' es requerido");
            }

            // Validar rango de Valor Nominal
            if (nuevoValor.compareTo(new BigDecimal("1000.00")) < 0 || nuevoValor.compareTo(new BigDecimal("6000.00")) > 0) {
                return ResponseEntity.badRequest().body("Error: El valor nominal debe estar entre 1,000 y 6,000");
            }

            Bonos bonoActualizado = sbono.actualizarValorNominal(idBono, nuevoValor);
            List<FlujoCaja> flujos = sbono.obtenerFlujosCajaPorBono(idBono);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Valor nominal actualizado exitosamente y flujos de caja regenerados",
                    "valorAnterior", request.get("valorAnterior"),
                    "valorNuevo", nuevoValor,
                    "bono", bonoActualizado,
                    "totalPeriodos", flujos.size(),
                    "flujos", flujos
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint simplificado para obtener el flujo completo actualizado de un bono
     * Útil para verificar los cambios después de modificar TEA o Valor Nominal
     * Solo el usuario con rol EMISOR puede ver la TCEA
     *
     * @param idBono El ID del bono
     * @param idUsuario El ID del usuario que solicita la información
     * @return El flujo completo actualizado del bono
     */
    @GetMapping("/{idBono}/flujo-completo")
    public ResponseEntity<?> obtenerFlujoCompleto(@PathVariable Integer idBono, @RequestParam Integer idUsuario) {
        try {
            // Obtener el bono
            Bonos bono = sbono.obtenerBonoPorId(idBono);
            
            // Obtener el usuario para verificar su rol
            Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Error: Usuario no encontrado");
            }
            
            // Obtener flujos de caja
            List<FlujoCaja> flujos = sbono.obtenerFlujosCajaPorBono(idBono);
            
            // Si no hay flujos, los generamos automáticamente
            if (flujos.isEmpty()) {
                flujos = sbono.generarYGuardarFlujosCaja(idBono);
            }

            // Preparar información del bono
            Map<String, Object> infoBono = new HashMap<>();
            infoBono.put("id", bono.getIdBono());
            infoBono.put("nombre", bono.getNombreBono());
            infoBono.put("valorNominal", bono.getValorNominal());
            infoBono.put("plazo", bono.getPlazo());
            infoBono.put("frecuenciaPago", bono.getFrecuenciaPago());

            BigDecimal tcea = rbono.calcularTCEAManual(idBono);
            System.out.println("TCEA calculada: " + tcea); // Log para depurar


            // Solo mostrar TCEA si el usuario es EMISOR
            if (usuario.getRol() == RolUsuario.EMISOR) {
                infoBono.put("tea", bono.getPorcentajeTasa());
                infoBono.put("mensaje", "TCEA visible - Usuario Emisor");
                infoBono.put("tcea", tcea);
            } else {
                infoBono.put("mensaje", "TCEA restringida - Solo visible para Emisores");
            }

            return ResponseEntity.ok(Map.of(
                    "bono", infoBono,
                    "resumen", Map.of(
                            "totalPeriodos", flujos.size(),
                            "fechaUltimaActualizacion", new Date(),
                            "rolUsuario", usuario.getRol().getDescripcion()
                    ),
                    "flujosCaja", flujos
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }
}
