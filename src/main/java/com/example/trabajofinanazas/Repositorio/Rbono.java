package com.example.trabajofinanazas.Repositorio;

import com.example.trabajofinanazas.Entites.Bonos;
import com.example.trabajofinanazas.Entites.RolUsuario;
import com.example.trabajofinanazas.Entites.FlujoCaja;
import com.example.trabajofinanazas.Entites.ResultadosFinancieros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Repository
public interface Rbono extends JpaRepository<Bonos, Integer> {

    // Método para obtener todos los bonos de un usuario específico

    @Query("SELECT b.idBono as idBono, b.nombreBono as nombreBono, b.porcentajeTasa as porcentajeTasa, " +
            "ROUND((POWER(1 + (b.porcentajeTasa / 100), 180.0/360.0) - 1) * 100, 7) as tes " +
            "FROM Bonos b")
    List<Map<String, Object>> calcularTES();

    @Query("SELECT b.idBono as idBono, b.nombreBono as nombreBono, b.porcentajeTasa as porcentajeTasa, " +
            "ROUND((POWER(1 + (b.porcentajeTasa / 100), 180.0/360.0) - 1) * 100, 7) as tes " +
            "FROM Bonos b WHERE b.idBono = :idBono")
    Map<String, Object> calcularTESPorId(@Param("idBono") Integer idBono);

    // Método para obtener TCEA desde ResultadosFinancieros
    @Query("SELECT rf.tcea as tcea " +
           "FROM ResultadosFinancieros rf WHERE rf.bono.idBono = :idBono")
    BigDecimal obtenerTCEADesdeResultados(@Param("idBono") Integer idBono);
    
    // Método para obtener TCEA desde FlujoCaja (primer registro)
    @Query("SELECT fc.tcea as tcea " +
           "FROM FlujoCaja fc WHERE fc.bono.idBono = :idBono " +
           "ORDER BY fc.nroPeriodo ASC")
    List<BigDecimal> obtenerTCEADesdeFlujoCaja(@Param("idBono") Integer idBono);

    // Método para calcular TCEA manualmente (backup si no está almacenada)
    // Esto asegura que Spring Data JPA devuelva el BigDecimal correcto y no el ID del bono.
    @Query("SELECT ROUND((b.porcentajeTasa + COALESCE(b.cavali, 0) + COALESCE(b.estructuracion, 0) + COALESCE(b.colocacion, 0)), 4) " +
            "FROM Bonos b WHERE b.idBono = :idBono")
    BigDecimal calcularTCEAManual(@Param("idBono") Integer idBono);

    // Método para calcular TREA manualmente
    @Query("SELECT ROUND((b.porcentajeTasa - COALESCE(b.cavali, 0) - COALESCE(b.estructuracion, 0) - COALESCE(b.colocacion, 0)), 4) " +
            "FROM Bonos b WHERE b.idBono = :idBono")
    BigDecimal calcularTreaManual(@Param("idBono") Integer idBono);

    // Métodos de cálculo movidos desde el servicio
    default Integer calcularNumeroPeriodos(String frecuenciaPago, Integer plazoAnios) {
        switch (frecuenciaPago.toLowerCase()) {
            case "semestral":
                return plazoAnios * 2;
            case "trimestral":
                return plazoAnios * 4;
            case "mensual":
                return plazoAnios * 12;
            case "anual":
                return plazoAnios;
            default:
                return plazoAnios; // Por defecto anual
        }
    }

    default BigDecimal calcularInteres(BigDecimal valorNominal, BigDecimal tes) {
        return valorNominal.multiply(tes.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    default BigDecimal calcularCuota(BigDecimal interes, BigDecimal valorNominal, Integer periodo, Integer totalPeriodos) {
        if (periodo.equals(totalPeriodos)) {
            // En el último período: cuota = valor nominal + interés
            return valorNominal.add(interes).setScale(2, RoundingMode.HALF_UP);
        } else {
            // En los demás períodos: cuota = interés
            return interes;
        }
    }

    default BigDecimal calcularAmortizacion(Integer periodo, Integer totalPeriodos, BigDecimal valorNominal) {
        if (periodo.equals(totalPeriodos)) {
            // Solo en el último período se amortiza el valor nominal
            return valorNominal;
        } else {
            // En los demás períodos no hay amortización
            return BigDecimal.ZERO;
        }
    }

    default BigDecimal calcularSaldoFinal(Integer periodo, Integer totalPeriodos, BigDecimal valorNominal) {
        if (periodo.equals(totalPeriodos)) {
            // En el último período el saldo final es 0
            return BigDecimal.ZERO;
        } else {
            // En los demás períodos el saldo final es el valor nominal
            return valorNominal;
        }
    }

    default BigDecimal calcularTESMercado(BigDecimal trea) {
        if (trea == null) {
            return BigDecimal.ZERO;
        }
        // Fórmula: (1 + TREA/100)^(1/2) - 1
        BigDecimal treaPorcentaje = trea.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        BigDecimal unoMasTrea = BigDecimal.ONE.add(treaPorcentaje);
        double resultado = Math.pow(unoMasTrea.doubleValue(), 0.5) - 1;
        return BigDecimal.valueOf(resultado * 100).setScale(4, RoundingMode.HALF_UP);
    }

    default BigDecimal calcularValorPresente(BigDecimal cuota, BigDecimal tesMercado, Integer periodo) {
        if (cuota == null || tesMercado == null || periodo == null || periodo <= 0) {
            return BigDecimal.ZERO;
        }
        // Fórmula: VP = CFt / (1 + r)^t
        // r = TES del mercado en decimal
        BigDecimal rDecimal = tesMercado.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        BigDecimal unoMasR = BigDecimal.ONE.add(rDecimal);
        double denominador = Math.pow(unoMasR.doubleValue(), periodo);
        BigDecimal valorPresente = cuota.divide(BigDecimal.valueOf(denominador), 8, RoundingMode.HALF_UP);
        return valorPresente.setScale(2, RoundingMode.HALF_UP);
    }

    default BigDecimal calcularPrecioBono(Integer idBono) {
        // Generar el flujo de caja completo
        List<Map<String, Object>> flujos = generarFlujoCaja(idBono);
        
        // Sumar todos los valores presentes
        BigDecimal precioBono = BigDecimal.ZERO;
        for (Map<String, Object> flujo : flujos) {
            Object valorPresenteObj = flujo.get("valorPresente");
            if (valorPresenteObj instanceof BigDecimal) {
                BigDecimal valorPresente = (BigDecimal) valorPresenteObj;
                precioBono = precioBono.add(valorPresente);
            }
        }
        
        return precioBono.setScale(2, RoundingMode.HALF_UP);
    }

    default BigDecimal calcularSumaValoresPresentesPorPeriodo(Integer idBono) {
        // Generar el flujo de caja completo
        List<Map<String, Object>> flujos = generarFlujoCaja(idBono);
        
        // Sumar todos los valores presentes multiplicados por su período
        BigDecimal sumaTotal = BigDecimal.ZERO;
        for (Map<String, Object> flujo : flujos) {
            Object periodoObj = flujo.get("periodo");
            Object valorPresenteObj = flujo.get("valorPresente");
            
            if (periodoObj instanceof Integer && valorPresenteObj instanceof BigDecimal) {
                Integer periodo = (Integer) periodoObj;
                BigDecimal valorPresente = (BigDecimal) valorPresenteObj;
                
                // Multiplicar período por valor presente
                BigDecimal producto = valorPresente.multiply(BigDecimal.valueOf(periodo));
                sumaTotal = sumaTotal.add(producto);
            }
        }
        
        return sumaTotal.setScale(2, RoundingMode.HALF_UP);
    }

    default BigDecimal calcularConvexidad(Integer idBono) {
        // Generar el flujo de caja completo
        List<Map<String, Object>> flujos = generarFlujoCaja(idBono);
        
        // Obtener precio del bono y TES del mercado
        BigDecimal precioBono = calcularPrecioBono(idBono);
        BigDecimal trea = calcularTreaManual(idBono);
        BigDecimal tesMercado = calcularTESMercado(trea);
        
        if (precioBono == null || precioBono.compareTo(BigDecimal.ZERO) == 0 || tesMercado == null) {
            return BigDecimal.ZERO;
        }
        
        // Calcular la sumatoria: ∑[CFt * t * (t+1) / (1+r)^(t+2)]
        BigDecimal sumatoria = BigDecimal.ZERO;
        BigDecimal rDecimal = tesMercado.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        BigDecimal unoMasR = BigDecimal.ONE.add(rDecimal);
        
        for (Map<String, Object> flujo : flujos) {
            Object periodoObj = flujo.get("periodo");
            Object cuotaObj = flujo.get("cuota");
            
            if (periodoObj instanceof Integer && cuotaObj instanceof BigDecimal) {
                Integer t = (Integer) periodoObj;
                BigDecimal CFt = (BigDecimal) cuotaObj;
                
                // CFt * t * (t+1)
                BigDecimal numerador = CFt.multiply(BigDecimal.valueOf(t))
                                         .multiply(BigDecimal.valueOf(t + 1));
                
                // (1+r)^(t+2)
                double denominador = Math.pow(unoMasR.doubleValue(), t + 2);
                
                // CFt * t * (t+1) / (1+r)^(t+2)
                BigDecimal termino = numerador.divide(BigDecimal.valueOf(denominador), 8, RoundingMode.HALF_UP);
                sumatoria = sumatoria.add(termino);
            }
        }
        
        // Convexidad = (1/P) * sumatoria
        BigDecimal convexidad = sumatoria.divide(precioBono, 2, RoundingMode.HALF_UP);
        
        return convexidad.setScale(6, RoundingMode.HALF_UP);
    }

    default List<Map<String, Object>> generarFlujoCaja(Integer idBono) {
        // Obtener el bono por ID
        Bonos bono = findById(idBono).orElse(null);
        if (bono == null) {
            return new ArrayList<>();
        }

        // Obtener TES del bono (ya convertido a BigDecimal)
        BigDecimal tes = obtenerTESPorId(idBono);
        if (tes == null) {
            return new ArrayList<>();
        }

        // Obtener TCEA del bono
        BigDecimal tcea = calcularTCEAManual(idBono);

        // Obtener TREA y calcular TES del mercado
        BigDecimal trea = calcularTreaManual(idBono);
        BigDecimal tesMercado = calcularTESMercado(trea);

        // Obtener período de gracia (máximo 6 meses)
        Integer periodoGracia = bono.getPeriodoGracia() != null ? 
            Math.min(bono.getPeriodoGracia(), 6) : 0;

        // Calcular número total de períodos del bono
        Integer totalPeriodosBono = calcularNumeroPeriodos(bono.getFrecuenciaPago(), bono.getPlazo());
        
        // Total de períodos incluyendo período de gracia
        Integer totalPeriodos = periodoGracia + totalPeriodosBono;

        // Valor nominal original y nuevo valor nominal después del período de gracia
        BigDecimal valorNominalOriginal = bono.getValorNominal();
        BigDecimal nuevoValorNominal = valorNominalOriginal;
        
        // Si hay período de gracia, calcular el nuevo valor nominal usando TES
        if (periodoGracia > 0) {
            BigDecimal tesDecimal = tes.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
            BigDecimal incremento = tesDecimal.multiply(valorNominalOriginal);
            nuevoValorNominal = valorNominalOriginal.add(incremento);
        }

        List<Map<String, Object>> flujos = new ArrayList<>();

        // Generar cada período del flujo
        for (int i = 1; i <= totalPeriodos; i++) {
            Map<String, Object> flujo = new HashMap<>();
            flujo.put("periodo", i);
            
            // Durante el período de gracia
            if (i <= periodoGracia) {
                flujo.put("saldoInicial", valorNominalOriginal);
                flujo.put("interes", BigDecimal.ZERO);
                flujo.put("cuota", BigDecimal.ZERO);
                flujo.put("amortizacion", BigDecimal.ZERO);
                flujo.put("saldoFinal", valorNominalOriginal);
                flujo.put("observacion", "Período de gracia - mes " + i);
                
                // Valor presente para período de gracia (cuota = 0)
                BigDecimal valorPresente = calcularValorPresente(BigDecimal.ZERO, tesMercado, i);
                flujo.put("valorPresente", valorPresente);
            } 
            // Después del período de gracia
            else {
                Integer periodoEfectivo = i - periodoGracia; // Período relativo al flujo normal
                
                flujo.put("saldoInicial", nuevoValorNominal);
                
                // Calcular interés con el nuevo valor nominal
                BigDecimal interes = calcularInteres(nuevoValorNominal, tes);
                flujo.put("interes", interes);
                
                // Calcular cuota
                BigDecimal cuota = calcularCuota(interes, nuevoValorNominal, periodoEfectivo, totalPeriodosBono);
                flujo.put("cuota", cuota);
                
                // Calcular amortización
                BigDecimal amortizacion = calcularAmortizacion(periodoEfectivo, totalPeriodosBono, nuevoValorNominal);
                flujo.put("amortizacion", amortizacion);
                
                // Calcular saldo final
                BigDecimal saldoFinal = calcularSaldoFinal(periodoEfectivo, totalPeriodosBono, nuevoValorNominal);
                flujo.put("saldoFinal", saldoFinal);
                
                // Calcular valor presente usando la cuota y el período actual
                BigDecimal valorPresente = calcularValorPresente(cuota, tesMercado, i);
                flujo.put("valorPresente", valorPresente);
                
                flujo.put("observacion", "Flujo normal");
            }
            
            // Información adicional
            flujo.put("tes", tes);
            flujo.put("tcea", tcea != null ? tcea : BigDecimal.ZERO);
            flujo.put("tesMercado", tesMercado);
            flujo.put("tipoGracia", bono.getTipoGracia());
            flujo.put("periodoGracia", bono.getPeriodoGracia());
            flujo.put("valorNominalOriginal", valorNominalOriginal);
            flujo.put("nuevoValorNominal", nuevoValorNominal);
            
            flujos.add(flujo);
        }

        return flujos;
    }

    default BigDecimal obtenerTESPorId(Integer idBono) {
        Map<String, Object> bonoTES = calcularTESPorId(idBono);
        if (bonoTES != null && bonoTES.containsKey("tes")) {
            Object tesValue = bonoTES.get("tes");
            if (tesValue instanceof Double) {
                return BigDecimal.valueOf((Double) tesValue);
            } else if (tesValue instanceof BigDecimal) {
                return (BigDecimal) tesValue;
            } else if (tesValue instanceof Number) {
                return BigDecimal.valueOf(((Number) tesValue).doubleValue());
            }
        }
        return null;
    }

//    default BigDecimal obtenerTCEAPorId(Integer idBono) {
//        // Primero intentar obtener TCEA desde ResultadosFinancieros
//        try {
//            BigDecimal tceaResultados = obtenerTCEADesdeResultados(idBono);
//            if (tceaResultados != null) {
//                return tceaResultados;
//            }
//        } catch (Exception e) {
//            // Si no existe en ResultadosFinancieros, continuar con FlujoCaja
//        }
//
//        // Segundo intento: obtener TCEA desde FlujoCaja
//        try {
//            List<BigDecimal> tceaFlujoCaja = obtenerTCEADesdeFlujoCaja(idBono);
//            if (tceaFlujoCaja != null && !tceaFlujoCaja.isEmpty() && tceaFlujoCaja.get(0) != null) {
//                return tceaFlujoCaja.get(0);
//            }
//        } catch (Exception e) {
//            // Si no existe en FlujoCaja, continuar con cálculo manual
//        }
//
//        // Tercer intento: calcular TCEA manualmente desde tabla Bonos
//        try {
//            Map<String, Object> bonoTCEA = calcularTCEAManual(idBono);
//            if (bonoTCEA != null && bonoTCEA.containsKey("tcea")) {
//                Object tceaValue = bonoTCEA.get("tcea");
//                return convertirABigDecimal(tceaValue);
//            }
//        } catch (Exception e) {
//            // Si falla todo, devolver null
//        }
//
//        return null;
//    }


//    // Método para debugging - verificar datos TCEA
//    default Map<String, Object> debugTCEA(Integer idBono) {
//        Map<String, Object> debug = new HashMap<>();
//
//        // Verificar ResultadosFinancieros
//        try {
//            BigDecimal tceaResultados = obtenerTCEADesdeResultados(idBono);
//            debug.put("tceaResultados", tceaResultados);
//            debug.put("hayResultados", tceaResultados != null);
//        } catch (Exception e) {
//            debug.put("errorResultados", e.getMessage());
//        }
//
//        // Verificar FlujoCaja
//        try {
//            List<BigDecimal> tceaFlujoCaja = obtenerTCEADesdeFlujoCaja(idBono);
//            debug.put("tceaFlujoCaja", tceaFlujoCaja);
//            debug.put("hayFlujoCaja", tceaFlujoCaja != null && !tceaFlujoCaja.isEmpty());
//        } catch (Exception e) {
//            debug.put("errorFlujoCaja", e.getMessage());
//        }
//
//        // Verificar cálculo manual
//        try {
//            Map<String, Object> tceaManual = calcularTCEAManual(idBono);
//            debug.put("tceaManual", tceaManual);
//            debug.put("hayTceaManual", tceaManual != null && tceaManual.containsKey("tcea"));
//        } catch (Exception e) {
//            debug.put("errorManual", e.getMessage());
//        }
//
//        return debug;
//    }

    // Método auxiliar para convertir Objects a BigDecimal de manera segura
    default BigDecimal convertirABigDecimal(Object valor) {
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

    default Map<String, Object> obtenerDatosCalculoBono(Integer idBono) {
        return obtenerDatosCalculoBono(idBono, null);
    }

    default Map<String, Object> obtenerDatosCalculoBono(Integer idBono, com.example.trabajofinanazas.Entites.Usuario usuarioActual) {
        Bonos bono = findById(idBono).orElse(null);
        if (bono == null) {
            return new HashMap<>();
        }

        BigDecimal tes = obtenerTESPorId(idBono);
        BigDecimal tcea = calcularTCEAManual(idBono);
        BigDecimal trea = calcularTreaManual(idBono);
        BigDecimal tesMercado = calcularTESMercado(trea);
        BigDecimal precioBono = calcularPrecioBono(idBono);
        BigDecimal sumaValoresPresentesPorPeriodo = calcularSumaValoresPresentesPorPeriodo(idBono);
        BigDecimal convexidad = calcularConvexidad(idBono);
        
        // Calcular Maduración: (suma de valores presentes por período / precio del bono) / 2
        BigDecimal maduracion = BigDecimal.ZERO;
        if (precioBono != null && precioBono.compareTo(BigDecimal.ZERO) != 0) {
            maduracion = sumaValoresPresentesPorPeriodo.divide(precioBono, 8, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        }
        
        Integer totalPeriodosBono = calcularNumeroPeriodos(bono.getFrecuenciaPago(), bono.getPlazo());
        // Obtener período de gracia (máximo 6 meses)
        Integer periodoGracia = bono.getPeriodoGracia() != null ? 
            Math.min(bono.getPeriodoGracia(), 2) : 0;
        
        // Calcular nuevo valor nominal si hay período de gracia
        BigDecimal valorNominalOriginal = bono.getValorNominal();
        BigDecimal nuevoValorNominal = valorNominalOriginal;
        
        if (periodoGracia > 0) {
            BigDecimal tesDecimal = tes.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
            BigDecimal incremento = tesDecimal.multiply(valorNominalOriginal);
            nuevoValorNominal = valorNominalOriginal.add(incremento);
        }
        
        // Calcular interés con el valor nominal que se usará en los flujos efectivos
        BigDecimal valorParaInteres = periodoGracia > 0 ? nuevoValorNominal : valorNominalOriginal;
        BigDecimal interes = calcularInteres(valorParaInteres, tes);

        Map<String, Object> datos = new HashMap<>();
        datos.put("idBono", idBono);
        datos.put("nombreBono", bono.getNombreBono());
        datos.put("valorNominalOriginal", 3500);
        datos.put("nuevoValorNominal", nuevoValorNominal);
        datos.put("frecuenciaPago", bono.getFrecuenciaPago());
        datos.put("plazoAnios", bono.getPlazo());
        datos.put("totalPeriodosBono", totalPeriodosBono);
        datos.put("periodoGracia", periodoGracia);
        datos.put("totalPeriodosConGracia", periodoGracia + totalPeriodosBono);
        datos.put("tes", tes);

        // Solo mostrar TCEA si el usuario actual es EMISOR
        boolean mostrarTCEA = usuarioActual != null && 
                             RolUsuario.EMISOR.equals(usuarioActual.getRol());
        
        if (mostrarTCEA) {
            datos.put("tcea", tcea );
        }
        
        datos.put("interesPorPeriodo", interes);
        datos.put("tipoGracia", bono.getTipoGracia());
        datos.put("porcentajeTasa", bono.getPorcentajeTasa());
        datos.put("TREA", trea);
        datos.put("TESMercado", tesMercado);
        datos.put("precioBono", precioBono);
        datos.put("sumaValoresPresentesPorPeriodo", sumaValoresPresentesPorPeriodo);
        datos.put("maduracion", maduracion);
        datos.put("convexidad", convexidad);


        return datos;
    }
}
