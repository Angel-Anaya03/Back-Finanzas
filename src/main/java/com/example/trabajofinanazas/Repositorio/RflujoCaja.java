package com.example.trabajofinanazas.Repositorio;

import com.example.trabajofinanazas.Entites.FlujoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RflujoCaja extends JpaRepository<FlujoCaja, Integer> {
    
    /**
     * Obtiene todos los flujos de caja de un bono específico ordenados por número de período
     * @param idBono ID del bono
     * @return Lista de flujos de caja ordenados por período
     */
    List<FlujoCaja> findByBonoIdBonoOrderByNroPeriodo(Integer idBono);
    
    /**
     * Elimina todos los flujos de caja de un bono específico
     * @param idBono ID del bono
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM FlujoCaja f WHERE f.bono.idBono = :idBono")
    void deleteByBonoIdBono(@Param("idBono") Integer idBono);
    
    /**
     * Cuenta el número de períodos (flujos) de un bono
     * @param idBono ID del bono
     * @return Número total de períodos
     */
    @Query("SELECT COUNT(f) FROM FlujoCaja f WHERE f.bono.idBono = :idBono")
    Integer countByBonoIdBono(@Param("idBono") Integer idBono);
}
