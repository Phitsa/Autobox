package com.boxpro.service;

import com.boxpro.dto.request.LoginRequest;
import com.boxpro.dto.request.RegisterRequest;
import com.boxpro.dto.response.AuthResponse;
import com.boxpro.entity.Funcionario;
import com.boxpro.repository.FuncionarioRepository;
import com.boxpro.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getSenha()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);

        Funcionario funcionario = (Funcionario) authentication.getPrincipal();
        
        // Registrar login
        funcionario.registrarLogin();
        funcionarioRepository.save(funcionario);

        return new AuthResponse(jwt, funcionario.getId(), funcionario.getNome(), 
                               funcionario.getEmail(), funcionario.getTipoFuncionario());
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Verificar se email já existe
        if (funcionarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email já está em uso!");
        }

        // Verificar se CPF já existe (se fornecido)
        if (registerRequest.getCpf() != null && funcionarioRepository.existsByCpf(registerRequest.getCpf())) {
            throw new RuntimeException("CPF já está em uso!");
        }

        // Criar funcionário
        Funcionario funcionario = new Funcionario();
        funcionario.setNome(registerRequest.getNome());
        funcionario.setEmail(registerRequest.getEmail());
        funcionario.setSenha(passwordEncoder.encode(registerRequest.getSenha()));
        funcionario.setTelefone(registerRequest.getTelefone());
        funcionario.setCpf(registerRequest.getCpf());
        funcionario.setTipoFuncionario(registerRequest.getTipoFuncionario());
        funcionario.setAtivo(true);

        funcionario = funcionarioRepository.save(funcionario);

        // Autenticar automaticamente após registro
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                registerRequest.getEmail(),
                registerRequest.getSenha()
            )
        );

        String jwt = jwtUtil.generateToken(authentication);

        return new AuthResponse(jwt, funcionario.getId(), funcionario.getNome(), 
                               funcionario.getEmail(), funcionario.getTipoFuncionario());
    }

    public Funcionario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Funcionario) {
            return (Funcionario) authentication.getPrincipal();
        }
        throw new RuntimeException("Funcionário não autenticado");
    }

    public AuthResponse refreshToken(Funcionario funcionario) {
        // Criar nova autenticação para gerar novo token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            funcionario, null, funcionario.getAuthorities());
        
        String newJwt = jwtUtil.generateToken(authentication);
        
        return new AuthResponse(newJwt, funcionario.getId(), funcionario.getNome(), 
                               funcionario.getEmail(), funcionario.getTipoFuncionario());
    }
}
