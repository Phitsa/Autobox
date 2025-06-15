package com.boxpro.repository;

import com.boxpro.entity.EmpresaContatos;
import com.boxpro.entity.EmpresaContatos.TipoContato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaContatosRepository extends JpaRepository<EmpresaContatos, Long> {
    
    @Query("SELECT ec FROM EmpresaContatos ec WHERE ec.empresa.id = :empresaId AND ec.ativo = true ORDER BY ec.principal DESC, ec.tipoContato, ec.id")
    List<EmpresaContatos> findByEmpresaIdAndAtivoTrue(@Param("empresaId") Long empresaId);
    
    @Query("SELECT ec FROM EmpresaContatos ec WHERE ec.empresa.id = :empresaId AND ec.tipoContato = :tipoContato AND ec.ativo = true ORDER BY ec.principal DESC")
    List<EmpresaContatos> findByEmpresaIdAndTipoContatoAndAtivoTrue(@Param("empresaId") Long empresaId, 
                                                                    @Param("tipoContato") TipoContato tipoContato);
    
    @Query("SELECT ec FROM EmpresaContatos ec WHERE ec.empresa.id = :empresaId AND ec.principal = true AND ec.ativo = true")
    List<EmpresaContatos> findContatosPrincipaisByEmpresaId(@Param("empresaId") Long empresaId);
    
    @Query("SELECT ec FROM EmpresaContatos ec WHERE ec.empresa.id = :empresaId AND ec.tipoContato = :tipoContato AND ec.principal = true AND ec.ativo = true")
    Optional<EmpresaContatos> findContatoPrincipalByEmpresaIdAndTipo(@Param("empresaId") Long empresaId, 
                                                                     @Param("tipoContato") TipoContato tipoContato);
    
    @Query("SELECT ec FROM EmpresaContatos ec WHERE ec.ativo = true ORDER BY ec.empresa.id, ec.principal DESC, ec.tipoContato")
    List<EmpresaContatos> findAllAtivos();
    
    boolean existsByEmpresaIdAndTipoContatoAndValorAndIdNotAndAtivoTrue(Long empresaId, TipoContato tipoContato, String valor, Long id);
    
    boolean existsByEmpresaIdAndTipoContatoAndValorAndAtivoTrue(Long empresaId, TipoContato tipoContato, String valor);
    
    @Modifying
    @Query("UPDATE EmpresaContatos ec SET ec.principal = false WHERE ec.empresa.id = :empresaId AND ec.tipoContato = :tipoContato AND ec.id != :excludeId AND ec.ativo = true")
    void removerPrincipalPorTipo(@Param("empresaId") Long empresaId, 
                                 @Param("tipoContato") TipoContato tipoContato, 
                                 @Param("excludeId") Long excludeId);
    
    @Modifying
    @Query("UPDATE EmpresaContatos ec SET ec.principal = false WHERE ec.empresa.id = :empresaId AND ec.tipoContato = :tipoContato AND ec.ativo = true")
    void removerTodosPrincipaisPorTipo(@Param("empresaId") Long empresaId, 
                                       @Param("tipoContato") TipoContato tipoContato);
}
