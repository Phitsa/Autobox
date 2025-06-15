package com.boxpro.repository;

import com.boxpro.entity.EmpresaHorarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaHorariosRepository extends JpaRepository<EmpresaHorarios, Long> {
    
    @Query("SELECT eh FROM EmpresaHorarios eh WHERE eh.empresa.id = :empresaId AND eh.ativo = true ORDER BY eh.diaSemana")
    List<EmpresaHorarios> findByEmpresaIdAndAtivoTrue(@Param("empresaId") Long empresaId);
    
    @Query("SELECT eh FROM EmpresaHorarios eh WHERE eh.empresa.id = :empresaId AND eh.diaSemana = :diaSemana AND eh.ativo = true")
    Optional<EmpresaHorarios> findByEmpresaIdAndDiaSemanaAndAtivoTrue(@Param("empresaId") Long empresaId, 
                                                                      @Param("diaSemana") Integer diaSemana);
    
    @Query("SELECT eh FROM EmpresaHorarios eh WHERE eh.ativo = true ORDER BY eh.empresa.id, eh.diaSemana")
    List<EmpresaHorarios> findAllAtivos();
    
    @Query("SELECT eh FROM EmpresaHorarios eh WHERE eh.fechado = false AND eh.ativo = true ORDER BY eh.diaSemana")
    List<EmpresaHorarios> findHorariosAbertos();
    
    boolean existsByEmpresaIdAndDiaSemanaAndAtivoTrue(Long empresaId, Integer diaSemana);
    
    boolean existsByEmpresaIdAndDiaSemanaAndIdNotAndAtivoTrue(Long empresaId, Integer diaSemana, Long id);
}
