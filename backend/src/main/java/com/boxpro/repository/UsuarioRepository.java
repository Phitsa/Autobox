package com.boxpro.repository;

import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Buscar por email
    Optional<Usuario> findByEmail(String email);
    
    // Verificar se email já existe
    boolean existsByEmail(String email);
    
    // Buscar por tipo de usuário
    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);
    
    // Buscar por nome (contém)
    List<Usuario> findByNomeContainingIgnoreCase(String nome);
    
    // Buscar por telefone
    Optional<Usuario> findByTelefone(String telefone);
    
    // Buscar usuários com agendamentos
    @Query("SELECT DISTINCT u FROM Usuario u JOIN FETCH u.agendamentos")
    List<Usuario> findAllWithAgendamentos();
    
    // Contar clientes ativos
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.tipoUsuario = :tipo")
    Long countByTipo(@Param("tipo") TipoUsuario tipo);
}