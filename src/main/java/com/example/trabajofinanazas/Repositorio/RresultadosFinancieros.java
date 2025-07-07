package com.example.trabajofinanazas.Repositorio;

import com.example.trabajofinanazas.Entites.ResultadosFinancieros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RresultadosFinancieros extends JpaRepository<ResultadosFinancieros, Integer> {
}
