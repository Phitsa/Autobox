package com.boxpro.repository;

import com.boxpro.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.boxpro.entity.enums.TipoFuncionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    
    // Métodos básicos baseados no UsuarioRepository original
    Optional<Funcionario> findByEmail(String email);
    
    Optional<Funcionario> findByCpf(String cpf);
    
    List<Funcionario> findByNomeContaining(String nome);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    // Métodos específicos para funcionários
    Optional<Funcionario> findByEmailAndAtivo(String email, Boolean ativo);
    
    List<Funcionario> findByAtivoTrue();
    
    List<Funcionario> findByTipoFuncionarioAndAtivoTrue(TipoFuncionario tipoFuncionario);
    
    // Buscar funcionários não bloqueados
    @Query("SELECT f FROM Funcionario f WHERE f.ativo = true AND f.bloqueado = false")
    List<Funcionario> findFuncionariosDisponiveis();
    
    // Buscar funcionários com muitas tentativas de login
    @Query("SELECT f FROM Funcionario f WHERE f.tentativasLogin >= :maxTentativas AND f.bloqueado = false")
    List<Funcionario> findFuncionariosComMuitasTentativas(Integer maxTentativas);
    
    // Buscar funcionários bloqueados
    List<Funcionario> findByBloqueadoTrue();
}