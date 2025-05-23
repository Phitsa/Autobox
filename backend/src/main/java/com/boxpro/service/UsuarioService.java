package com.boxpro.service;

import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.TipoUsuario;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Criar novo usuário
     */
    public Usuario criar(Usuario usuario) {
        log.info("Criando novo usuário: {}", usuario.getEmail());
        
        // Validar se email já existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + usuario.getEmail());
        }
        
        // Criptografar senha
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        // Definir tipo padrão se não especificado
        if (usuario.getTipoUsuario() == null) {
            usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        }
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Buscar usuário por ID
     */
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    /**
     * Buscar usuário por email
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Listar todos os usuários
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Listar usuários por tipo
     */
    @Transactional(readOnly = true)
    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return usuarioRepository.findByTipoUsuario(tipo);
    }

    /**
     * Buscar usuários por nome
     */
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Atualizar usuário
     */
    public Usuario atualizar(Integer id, Usuario usuarioAtualizado) {
        log.info("Atualizando usuário ID: {}", id);
        
        Usuario usuario = buscarPorId(id);
        
        // Verificar se email mudou e já existe
        if (!usuario.getEmail().equals(usuarioAtualizado.getEmail()) 
            && usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
            throw new BusinessException("Email já cadastrado: " + usuarioAtualizado.getEmail());
        }
        
        // Atualizar campos
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setTelefone(usuarioAtualizado.getTelefone());
        
        // Só atualizar tipo se for admin fazendo a alteração
        if (usuarioAtualizado.getTipoUsuario() != null) {
            usuario.setTipoUsuario(usuarioAtualizado.getTipoUsuario());
        }
        
        return usuarioRepository.save(usuario);
    }

    /**
     * Alterar senha do usuário
     */
    public void alterarSenha(Integer id, String senhaAtual, String novaSenha) {
        log.info("Alterando senha do usuário ID: {}", id);
        
        Usuario usuario = buscarPorId(id);
        
        // Verificar senha atual
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new BusinessException("Senha atual incorreta");
        }
        
        // Atualizar senha
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    /**
     * Deletar usuário
     */
    public void deletar(Integer id) {
        log.info("Deletando usuário ID: {}", id);
        
        Usuario usuario = buscarPorId(id);
        
        // Verificar se usuário tem agendamentos ativos
        if (!usuario.getAgendamentos().isEmpty()) {
            throw new BusinessException("Usuário possui agendamentos e não pode ser deletado");
        }
        
        usuarioRepository.delete(usuario);
    }

    /**
     * Contar usuários por tipo
     */
    @Transactional(readOnly = true)
    public Long contarPorTipo(TipoUsuario tipo) {
        return usuarioRepository.countByTipo(tipo);
    }

    /**
     * Verificar se é administrador
     */
    @Transactional(readOnly = true)
    public boolean isAdministrador(Integer usuarioId) {
        Usuario usuario = buscarPorId(usuarioId);
        return TipoUsuario.ADMINISTRADOR.equals(usuario.getTipoUsuario());
    }
}