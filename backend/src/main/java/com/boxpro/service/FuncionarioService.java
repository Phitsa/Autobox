package com.boxpro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.boxpro.entity.Funcionario;
import com.boxpro.repository.FuncionarioRepository;

import jakarta.annotation.PostConstruct;

import com.boxpro.entity.enums.TipoFuncionario;

@Service
public class FuncionarioService {
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        System.out.println("✅ FuncionarioService carregado!");
    }

    public Funcionario criarFuncionario(Funcionario funcionario) {
        // Criptografar senha antes de salvar
        funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        funcionario.setAtivo(true);
        return funcionarioRepository.save(funcionario);
    }

    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public List<Funcionario> listarFuncionariosAtivos() {
        return funcionarioRepository.findByAtivoTrue();
    }

    public List<Funcionario> listarFuncionariosDisponiveis() {
        return funcionarioRepository.findFuncionariosDisponiveis();
    }

    public List<Funcionario> listarPorTipo(TipoFuncionario tipo) {
        return funcionarioRepository.findByTipoFuncionarioAndAtivoTrue(tipo);
    }

    public Optional<Funcionario> buscarPorId(Integer id) {
        return funcionarioRepository.findById(id);
    }

    public Optional<Funcionario> buscarPorEmail(String email) {
        return funcionarioRepository.findByEmail(email);
    }

    public Optional<Funcionario> buscarPorCpf(String cpf) {
        return funcionarioRepository.findByCpf(cpf);
    }

    public List<Funcionario> buscarFuncionarioPorNome(String nome) {
        return funcionarioRepository.findByNomeContaining(nome);
    }

    public boolean emailExiste(String email) {
        return funcionarioRepository.existsByEmail(email);
    }

    public boolean cpfExiste(String cpf) {
        return funcionarioRepository.existsByCpf(cpf);
    }

    public Funcionario atualizarFuncionario(Funcionario funcionario) {
        // Se a senha foi alterada, criptografar
        Optional<Funcionario> funcionarioExistente = funcionarioRepository.findById(funcionario.getId());
        if (funcionarioExistente.isPresent()) {
            Funcionario existente = funcionarioExistente.get();
            if (!existente.getSenha().equals(funcionario.getSenha())) {
                funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
            }
        }
        return funcionarioRepository.save(funcionario);
    }

    public void desativarFuncionario(Integer id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.setAtivo(false);
            funcionarioRepository.save(f);
        }
    }

    public void bloquearFuncionario(Integer id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.setBloqueado(true);
            funcionarioRepository.save(f);
        }
    }

    public void desbloquearFuncionario(Integer id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.setBloqueado(false);
            f.resetarTentativasLogin();
            funcionarioRepository.save(f);
        }
    }

    // Método para controle de tentativas de login
    public void incrementarTentativasLogin(String email) {
        Optional<Funcionario> funcionario = funcionarioRepository.findByEmail(email);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.incrementarTentativasLogin();
            
            // Bloquear após 5 tentativas falhadas
            if (f.getTentativasLogin() >= 5) {
                f.setBloqueado(true);
            }
            
            funcionarioRepository.save(f);
        }
    }
}
