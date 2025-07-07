package com.example.trabajofinanazas;

import com.example.trabajofinanazas.Entites.Bonos;
import com.example.trabajofinanazas.Repositorio.Rbono;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootApplication
public class TrabajoFinanazasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrabajoFinanazasApplication.class, args);
    }

    @Bean
    CommandLineRunner init(Rbono rBono) {
        return args -> {
            // Crear bono de prueba
            Bonos bono = new Bonos();
            bono.setNombreBono("BONO_TEST_001");
            bono.setValorNominal(new BigDecimal("5000.00"));
            bono.setValorComercial(new BigDecimal("1050.00"));
            bono.setTasaCupon(new BigDecimal("3.923"));
            bono.setFrecuenciaPago("semestral");
            bono.setPlazo(4); // 2 años
            bono.setPorcentajeTasa(new BigDecimal("3")); // 4.5%
            bono.setTiempoTasa("180");
            bono.setPrimaRedencion(new BigDecimal("1.00")); // 1%
            bono.setEstructuracion(new BigDecimal("1.00")); // 1%
            bono.setColocacion(new BigDecimal("0.75")); // 0.75% (valor al azar)
            bono.setCavali(new BigDecimal("0.05250")); // 0.5%
            bono.setTipoGracia("Sin gracia"); // valor al azar
            bono.setPeriodoGracia(2); // valor al azar
            bono.setFechaCreacion(new Date());
            
            // Guardar el bono
            rBono.save(bono);
            System.out.println("Bono de prueba creado con ID: " + bono.getIdBono());
        };
    }

}
