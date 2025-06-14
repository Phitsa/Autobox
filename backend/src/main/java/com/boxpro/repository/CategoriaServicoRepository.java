package com.boxpro.repository;

import com.boxpro.entity.CategoriaServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaServicoRepository extends JpaRepository<CategoriaServico, Long> {
    
    List<CategoriaServico> findByNomeContainingIgnoreCase(String nome);
    
    Optional<CategoriaServico> findByNomeIgnoreCase(String nome);
    
    boolean existsByNomeIgnoreCase(String nome);
}