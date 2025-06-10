package com.boxpro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.TipoUsuario;
import com.boxpro.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostConstruct
    public void init() {
        System.out.println("✅ UsuarioService carregado!");
    }

    public Usuario criarCliente(Usuario cliente) {
        cliente.setTipoUsuario(TipoUsuario.valueOf("CLIENTE"));
        cliente.setAtivo(true);
        return usuarioRepository.save(cliente);
    }

    public List<Usuario> listarClientes() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    public List<Usuario> buscarUsuarioPorNome(String nome) {
        return usuarioRepository.findByNomeContaining(nome);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean cpfExiste(String cpf) {
        return usuarioRepository.existsByCpf(cpf);
    }
}
