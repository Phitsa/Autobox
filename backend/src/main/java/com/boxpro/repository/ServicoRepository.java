package com.boxpro.repository;

import com.boxpro.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    
    List<Servico> findByNomeContainingIgnoreCase(String nome);
    
    List<Servico> findByCategoriaId(Long categoriaId);
    
    List<Servico> findByAtivoTrue();
    
    List<Servico> findByCategoriaIdAndAtivoTrue(Long categoriaId);
}