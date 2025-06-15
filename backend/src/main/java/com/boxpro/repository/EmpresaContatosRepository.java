package com.boxpro.repository;

import com.boxpro.entity.EmpresaContatos;
import com.boxpro.entity.EmpresaContatos.TipoContato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaContatosRepository extends JpaRepository<EmpresaContatos, Long> {

    /**
     * Buscar contatos ativos de uma empresa
     */
    List<EmpresaContatos> findByEmpresaIdAndAtivoTrue(Long empresaId);

    /**
     * Buscar contatos por empresa e tipo
     */
    List<EmpresaContatos> findByEmpresaIdAndTipoContatoAndAtivoTrue(Long empresaId, TipoContato tipoContato);

    /**
     * Buscar contatos principais de uma empresa
     */
    @Query("SELECT c FROM EmpresaContatos c WHERE c.empresa.id = :empresaId AND c.principal = true AND c.ativo = true")
    List<EmpresaContatos> findContatosPrincipaisByEmpresaId(@Param("empresaId") Long empresaId);

    /**
     * Verificar se existe contato com mesmo valor para a empresa
     */
    boolean existsByEmpresaIdAndTipoContatoAndValorAndAtivoTrue(Long empresaId, TipoContato tipoContato, String valor);

    /**
     * Verificar se existe contato com mesmo valor para a empresa (excluindo um ID
     * específico)
     */
    boolean existsByEmpresaIdAndTipoContatoAndValorAndIdNotAndAtivoTrue(
            Long empresaId, TipoContato tipoContato, String valor, Long excludeId);

    /**
     * Remover flag principal de todos os contatos de um tipo para uma empresa
     */
    @Modifying
    @Query("UPDATE EmpresaContatos c SET c.principal = false WHERE c.empresa.id = :empresaId AND c.tipoContato = :tipoContato AND c.ativo = true")
    void removerTodosPrincipaisPorTipo(@Param("empresaId") Long empresaId,
            @Param("tipoContato") TipoContato tipoContato);

    /**
     * Remover flag principal de contatos de um tipo para uma empresa (exceto um
     * específico)
     */
    @Modifying
    @Query("UPDATE EmpresaContatos c SET c.principal = false WHERE c.empresa.id = :empresaId AND c.tipoContato = :tipoContato AND c.id != :excludeId AND c.ativo = true")
    void removerPrincipalPorTipo(@Param("empresaId") Long empresaId, @Param("tipoContato") TipoContato tipoContato,
            @Param("excludeId") Long excludeId);

    /**
     * Buscar todos os contatos ativos
     */
    @Query("SELECT c FROM EmpresaContatos c WHERE c.ativo = true ORDER BY c.empresa.id, c.tipoContato, c.principal DESC")
    List<EmpresaContatos> findAllAtivos();

    /**
     * Buscar contatos por empresa (incluindo inativos)
     */
    List<EmpresaContatos> findByEmpresaId(Long empresaId);

    /**
     * Contar contatos ativos por empresa
     */
    long countByEmpresaIdAndAtivoTrue(Long empresaId);

    /**
     * Contar contatos ativos por empresa e tipo
     */
    long countByEmpresaIdAndTipoContatoAndAtivoTrue(Long empresaId, TipoContato tipoContato);

    /**
     * Buscar contato principal de um tipo específico para uma empresa
     */
    @Query("SELECT c FROM EmpresaContatos c WHERE c.empresa.id = :empresaId AND c.tipoContato = :tipoContato AND c.principal = true AND c.ativo = true")
    EmpresaContatos findContatoPrincipalByEmpresaIdAndTipo(@Param("empresaId") Long empresaId,
            @Param("tipoContato") TipoContato tipoContato);

    /**
     * Buscar contatos por valor (busca parcial)
     */
    @Query("SELECT c FROM EmpresaContatos c WHERE c.valor LIKE %:valor% AND c.ativo = true")
    List<EmpresaContatos> findByValorContaining(@Param("valor") String valor);

    /**
     * Buscar contatos por empresa e descrição
     */
    @Query("SELECT c FROM EmpresaContatos c WHERE c.empresa.id = :empresaId AND c.descricao LIKE %:descricao% AND c.ativo = true")
    List<EmpresaContatos> findByEmpresaIdAndDescricaoContaining(@Param("empresaId") Long empresaId,
            @Param("descricao") String descricao);
}