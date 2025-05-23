package com.boxpro.repository;

import com.boxpro.entity.CategoriaServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaServicoRepository extends JpaRepository<CategoriaServico, Integer> {
    
    // Buscar por nome
    Optional<CategoriaServico> findByNome(String nome);
    
    // Verificar se nome já existe
    boolean existsByNome(String nome);
    
    // Buscar categorias com serviços
    @Query("SELECT DISTINCT c FROM CategoriaServico c LEFT JOIN FETCH c.servicos")
    List<CategoriaServico> findAllWithServicos();
    
    // Buscar por nome (contém)
    List<CategoriaServico> findByNomeContainingIgnoreCase(String nome);
    
    // Contar categorias com serviços ativos
    @Query("SELECT COUNT(DISTINCT c) FROM CategoriaServico c JOIN c.servicos s WHERE s.ativo = true")
    Long countCategoriasComServicosAtivos();
    
    // Buscar categorias ordenadas por nome
    List<CategoriaServico> findAllByOrderByNomeAsc();
}