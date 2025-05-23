package com.boxpro.repository;

import com.boxpro.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Integer> {
    
    // Buscar serviços ativos
    List<Servico> findByAtivoTrue();
    
    // Buscar serviços inativos
    List<Servico> findByAtivoFalse();
    
    // Buscar por categoria
    List<Servico> findByCategoriaId(Integer categoriaId);
    
    // Buscar por nome
    Optional<Servico> findByNome(String nome);
    
    // Buscar por nome (contém) e ativo
    List<Servico> findByNomeContainingIgnoreCaseAndAtivo(String nome, Boolean ativo);
    
    // Buscar serviços por faixa de preço
    List<Servico> findByPrecoBetweenAndAtivoTrue(BigDecimal precoMin, BigDecimal precoMax);
    
    // Buscar serviços com duração menor que X minutos
    List<Servico> findByDuracaoEstimadaLessThanEqualAndAtivoTrue(Integer minutos);
    
    // Buscar serviços ordenados por preço
    List<Servico> findByAtivoTrueOrderByPrecoAsc();
    
    // Contar serviços por categoria
    @Query("SELECT COUNT(s) FROM Servico s WHERE s.categoria.id = :categoriaId AND s.ativo = true")
    Long countByCategoriaAndAtivo(@Param("categoriaId") Integer categoriaId);
    
    // Buscar serviços mais utilizados
    @Query("SELECT s FROM Servico s LEFT JOIN s.agendamentos a " +
           "WHERE s.ativo = true " +
           "GROUP BY s.id " +
           "ORDER BY COUNT(a) DESC")
    List<Servico> findServicosMaisUtilizados();
}