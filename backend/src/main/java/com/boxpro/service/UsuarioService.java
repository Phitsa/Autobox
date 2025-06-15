package com.boxpro.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.boxpro.entity.Usuario;
import com.boxpro.repository.UsuarioRepository;
@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario criarCliente(Usuario cliente) {
        return usuarioRepository.save(cliente);
    }

    public Usuario editarCliente(Long id, Usuario cliente) {
        Usuario clienteExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado para edição"));

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setEmail(cliente.getEmail());
        clienteExistente.setTelefone(cliente.getTelefone());
        clienteExistente.setCpf(cliente.getCpf());

        return usuarioRepository.save(clienteExistente);
    }
    
    public Page<Usuario> listarTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return usuarioRepository.findAll(pageable);
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

    public void removerCliente(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado para remoção");
        }
        usuarioRepository.deleteById(id);
    }
    
}
