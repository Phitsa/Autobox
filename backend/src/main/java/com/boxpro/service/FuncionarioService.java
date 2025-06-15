package com.boxpro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boxpro.entity.Funcionario;
import com.boxpro.repository.FuncionarioRepository;

import jakarta.annotation.PostConstruct;

import com.boxpro.entity.enums.TipoFuncionario;

@Service
@Transactional
public class FuncionarioService {
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        System.out.println("✅ FuncionarioService carregado!");
    }

    // Método paginado
    public Page<Funcionario> listarFuncionariosPaginados(Pageable pageable) {
        return funcionarioRepository.findAll(pageable);
    }

    // Método para listar todos (para estatísticas)
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Funcionario criarFuncionario(Funcionario funcionario) {
        // Validações básicas
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (funcionario.getEmail() == null || funcionario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        
        if (funcionario.getSenha() == null || funcionario.getSenha().length() < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
        }

        // Verificar duplicatas
        if (emailExiste(funcionario.getEmail())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        if (funcionario.getCpf() != null && !funcionario.getCpf().trim().isEmpty() && cpfExiste(funcionario.getCpf())) {
            throw new IllegalArgumentException("CPF já está em uso");
        }

        // Configurar dados padrão
        funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        funcionario.setAtivo(true);
        funcionario.setBloqueado(false);
        funcionario.setTentativasLogin(0);
        
        if (funcionario.getTipoFuncionario() == null) {
            funcionario.setTipoFuncionario(TipoFuncionario.FUNCIONARIO);
        }

        return funcionarioRepository.save(funcionario);
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
        // Buscar funcionário existente
        Optional<Funcionario> funcionarioExistente = funcionarioRepository.findById(funcionario.getId());
        
        if (!funcionarioExistente.isPresent()) {
            throw new IllegalArgumentException("Funcionário não encontrado");
        }

        Funcionario existente = funcionarioExistente.get();

        // Validações básicas
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (funcionario.getEmail() == null || funcionario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }

        // Verificar email único (exceto o próprio funcionário)
        Optional<Funcionario> funcionarioComEmail = buscarPorEmail(funcionario.getEmail());
        if (funcionarioComEmail.isPresent() && !funcionarioComEmail.get().getId().equals(funcionario.getId())) {
            throw new IllegalArgumentException("Email já está em uso por outro funcionário");
        }

        // Verificar CPF único (se fornecido)
        if (funcionario.getCpf() != null && !funcionario.getCpf().trim().isEmpty()) {
            Optional<Funcionario> funcionarioComCpf = buscarPorCpf(funcionario.getCpf());
            if (funcionarioComCpf.isPresent() && !funcionarioComCpf.get().getId().equals(funcionario.getId())) {
                throw new IllegalArgumentException("CPF já está em uso por outro funcionário");
            }
        }

        // Atualizar campos básicos
        existente.setNome(funcionario.getNome());
        existente.setEmail(funcionario.getEmail());
        existente.setTelefone(funcionario.getTelefone());
        existente.setCpf(funcionario.getCpf());
        existente.setTipoFuncionario(funcionario.getTipoFuncionario());
        
        // Para reativação, sempre marcar como ativo
        existente.setAtivo(true);

        // Atualizar senha apenas se foi fornecida uma nova
        if (funcionario.getSenha() != null && !funcionario.getSenha().trim().isEmpty()) {
            if (funcionario.getSenha().length() < 6) {
                throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
            }
            // Verificar se a senha é diferente da atual (não criptografada)
            if (!funcionario.getSenha().equals(existente.getSenha())) {
                existente.setSenha(passwordEncoder.encode(funcionario.getSenha()));
            }
        }

        return funcionarioRepository.save(existente);
    }

    public void desativarFuncionario(Integer id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.setAtivo(false);
            funcionarioRepository.save(f);
        } else {
            throw new IllegalArgumentException("Funcionário não encontrado");
        }
    }

    public void bloquearFuncionario(Integer id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.setBloqueado(true);
            funcionarioRepository.save(f);
        } else {
            throw new IllegalArgumentException("Funcionário não encontrado");
        }
    }

    public void desbloquearFuncionario(Integer id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.setBloqueado(false);
            f.resetarTentativasLogin();
            funcionarioRepository.save(f);
        } else {
            throw new IllegalArgumentException("Funcionário não encontrado");
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

    // Método adicional para registrar login bem-sucedido
    public void registrarLogin(Integer id) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(id);
        if (funcionario.isPresent()) {
            Funcionario f = funcionario.get();
            f.registrarLogin();
            funcionarioRepository.save(f);
        }
    }
}