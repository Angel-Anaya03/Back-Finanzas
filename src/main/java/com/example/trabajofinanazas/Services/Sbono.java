package com.example.trabajofinanazas.Services;

import com.example.trabajofinanazas.Entites.Bonos;
import com.example.trabajofinanazas.Entites.FlujoCaja;
import com.example.trabajofinanazas.Entites.Usuario;
import com.example.trabajofinanazas.Repositorio.Rbono;
import com.example.trabajofinanazas.Repositorio.RflujoCaja;
import com.example.trabajofinanazas.Repositorio.Rusuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class Sbono {

    @Autowired
    private Rbono rbono;
    
    @Autowired
    private Rusuario rusuario;
    
    @Autowired
    private RflujoCaja rflujoCaja;

    /**
     * Método para agregar un nuevo bono a un usuario específico
     * @param bono El bono a agregar
     * @return El bono guardado con su ID asignado
     * @throws RuntimeException Si el usuario no existe
     */
    public Bonos agregarBono(Bonos bono) {
        // Verificar que el usuario existe

        

        // Asignar el usuario al bono

        
        // Establecer la fecha de creación si no está establecida
        if (bono.getFechaCreacion() == null) {
            bono.setFechaCreacion(new Date());
        }
        
        // Guardar el bono
        return rbono.save(bono);
    }

    /**
     * Método para obtener todos los bonos de un usuario específico
     * @param idUsuario El ID del usuario
     * @return Lista de bonos del usuario
     */


    /**
     * Método para obtener un bono por su ID
     * @param idBono El ID del bono
     * @return El bono encontrado
     * @throws RuntimeException Si el bono no existe
     */
    public Bonos obtenerBonoPorId(Integer idBono) {
        return rbono.findById(idBono)
                .orElseThrow(() -> new RuntimeException("Bono con ID " + idBono + " no encontrado"));
    }

    /**
     * Método para actualizar un bono existente
     * @param bono El bono con los datos actualizados
     * @return El bono actualizado
     */
    public Bonos actualizarBono(Bonos bono) {
        if (bono.getIdBono() == null) {
            throw new RuntimeException("El ID del bono es requerido para la actualización");
        }
        
        // Verificar que el bono existe
        obtenerBonoPorId(bono.getIdBono());
        
        return rbono.save(bono);
    }

    /**
     * Método para eliminar un bono
     * @param idBono El ID del bono a eliminar
     */
    public void eliminarBono(Integer idBono) {
        // Verificar que el bono existe antes de eliminarlo
        obtenerBonoPorId(idBono);
        rbono.deleteById(idBono);
    }

    public List<Map<String, Object>> obtenerBonosConTES() {
        return rbono.calcularTES();
    }

    public List<Map<String, Object>> generarFlujoCaja(Integer idBono) {
        return rbono.generarFlujoCaja(idBono);
    }

    public Map<String, Object> obtenerDatosCalculoBono(Integer idBono) {
        return rbono.obtenerDatosCalculoBono(idBono);
    }

    // Métodos de conveniencia para el controlador
    public Integer calcularNumeroPeriodos(String frecuenciaPago, Integer plazoAnios) {
        return rbono.calcularNumeroPeriodos(frecuenciaPago, plazoAnios);
    }

    public BigDecimal calcularInteres(BigDecimal valorNominal, BigDecimal tes) {
        return rbono.calcularInteres(valorNominal, tes);
    }

    public BigDecimal calcularCuota(BigDecimal interes, BigDecimal valorNominal, Integer periodo, Integer totalPeriodos) {
        return rbono.calcularCuota(interes, valorNominal, periodo, totalPeriodos);
    }

    public BigDecimal calcularAmortizacion(Integer periodo, Integer totalPeriodos, BigDecimal valorNominal) {
        return rbono.calcularAmortizacion(periodo, totalPeriodos, valorNominal);
    }

    public BigDecimal calcularSaldoFinal(Integer periodo, Integer totalPeriodos, BigDecimal valorNominal) {
        return rbono.calcularSaldoFinal(periodo, totalPeriodos, valorNominal);
    }

    /**
     * Método principal para generar automáticamente los flujos de caja de un bono
     * y guardarlos en la base de datos
     * @param idBono El ID del bono para el cual generar los flujos
     * @return Lista de flujos de caja generados y guardados
     */
    public List<FlujoCaja> generarYGuardarFlujosCaja(Integer idBono) {
        // Obtener el bono
        Bonos bono = obtenerBonoPorId(idBono);
        
        // Limpiar flujos existentes si los hay
        rflujoCaja.deleteByBonoIdBono(idBono);
        
        // Obtener los datos calculados del flujo
        List<Map<String, Object>> datosCalculados = rbono.generarFlujoCaja(idBono);
        
        List<FlujoCaja> flujosCaja = new ArrayList<>();
        
        // Convertir cada cálculo en un registro de FlujoCaja
        for (Map<String, Object> datosFlujo : datosCalculados) {
            FlujoCaja flujo = new FlujoCaja();
            
            // Asignar valores básicos
            flujo.setBono(bono);
            flujo.setNroPeriodo((Integer) datosFlujo.get("periodo"));
            
            // Tasa cupón fija (como pediste, 50 por defecto)
            flujo.setTasaCupon(new BigDecimal("50.0000000"));
            
            // Amortización según el cálculo
            BigDecimal amortizacion = convertirABigDecimal(datosFlujo.get("amortizacion"));
            flujo.setAmortizacion(amortizacion != null ? amortizacion : BigDecimal.ZERO);
            
            // Cavali (valor fijo, puedes ajustarlo según tus necesidades)
            flujo.setCavali(bono.getCavali() != null ? bono.getCavali() : new BigDecimal("5.00"));


            flujo.setTcea(rbono.calcularTCEAManual(idBono));
            // Valor comercial (valor fijo del bono)
            flujo.setValorComercial(bono.getValorComercial() != null ? bono.getValorComercial() : bono.getValorNominal());
            
            // Prima de redención (valor fijo del bono)
            flujo.setPrimaRedencion(bono.getPrimaRedencion() != null ? bono.getPrimaRedencion() : new BigDecimal("0.00"));
            
            flujosCaja.add(flujo);
        }
        
        // Guardar todos los flujos en la base de datos
        List<FlujoCaja> flujoGuardados = rflujoCaja.saveAll(flujosCaja);
        
        return flujoGuardados;
    }

    /**
     * Método para obtener los flujos de caja de un bono desde la base de datos
     * @param idBono El ID del bono
     * @return Lista de flujos de caja del bono
     */
    public List<FlujoCaja> obtenerFlujosCajaPorBono(Integer idBono) {
        return rflujoCaja.findByBonoIdBonoOrderByNroPeriodo(idBono);
    }

    /**
     * Método auxiliar para convertir Object a BigDecimal de manera segura
     */
    private BigDecimal convertirABigDecimal(Object valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        if (valor instanceof BigDecimal) {
            return (BigDecimal) valor;
        } else if (valor instanceof Double) {
            return BigDecimal.valueOf((Double) valor).setScale(2, RoundingMode.HALF_UP);
        } else if (valor instanceof Number) {
            return BigDecimal.valueOf(((Number) valor).doubleValue()).setScale(2, RoundingMode.HALF_UP);
        } else if (valor instanceof String) {
            try {
                return new BigDecimal((String) valor).setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Método para regenerar flujos de caja cuando se actualiza un bono
     * @param bono El bono actualizado
     * @return El bono actualizado con sus nuevos flujos
     */
    public Bonos actualizarBonoYRegenerarFlujos(Bonos bono) {
        // Actualizar el bono
        Bonos bonoActualizado = actualizarBono(bono);
        
        // Regenerar los flujos de caja
        generarYGuardarFlujosCaja(bonoActualizado.getIdBono());
        
        return bonoActualizado;
    }

    /**
     * Método específico para actualizar solo la TEA (Tasa Efectiva Anual) de un bono
     * y regenerar automáticamente sus flujos de caja
     * @param idBono El ID del bono a actualizar
     * @param nuevaTea La nueva TEA a establecer
     * @return El bono actualizado
     */
    public Bonos actualizarTEA(Integer idBono, BigDecimal nuevaTea) {
        // Obtener el bono actual
        Bonos bono = obtenerBonoPorId(idBono);
        
        // Actualizar solo la TEA (porcentajeTasa)
        bono.setPorcentajeTasa(nuevaTea);
        
        // Guardar el bono actualizado
        Bonos bonoActualizado = rbono.save(bono);
        
        // Regenerar los flujos de caja con la nueva TEA
        generarYGuardarFlujosCaja(idBono);
        
        return bonoActualizado;
    }

    /**
     * Método específico para actualizar solo el Valor Nominal de un bono
     * y regenerar automáticamente sus flujos de caja
     * @param idBono El ID del bono a actualizar
     * @param nuevoValorNominal El nuevo valor nominal a establecer
     * @return El bono actualizado
     */
    public Bonos actualizarValorNominal(Integer idBono, BigDecimal nuevoValorNominal) {
        // Obtener el bono actual
        Bonos bono = obtenerBonoPorId(idBono);
        
        // Actualizar solo el valor nominal
        bono.setValorNominal(nuevoValorNominal);
        
        // Opcional: actualizar también el valor comercial si es igual al nominal
        if (bono.getValorComercial() == null || bono.getValorComercial().equals(bono.getValorNominal())) {
            bono.setValorComercial(nuevoValorNominal);
        }
        
        // Guardar el bono actualizado
        Bonos bonoActualizado = rbono.save(bono);
        
        // Regenerar los flujos de caja con el nuevo valor nominal
        generarYGuardarFlujosCaja(idBono);
        
        return bonoActualizado;
    }
}
