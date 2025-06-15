package com.boxpro.repository;

import com.boxpro.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    @Query("SELECT e FROM Empresa e WHERE e.ativo = true")
    Optional<Empresa> findActiveEmpresa();
    
    @Query("SELECT COUNT(e) FROM Empresa e WHERE e.ativo = true")
    long countActiveEmpresas();
    
    Optional<Empresa> findByCnpj(String cnpj);
    
    boolean existsByCnpjAndIdNot(String cnpj, Long id);
}
