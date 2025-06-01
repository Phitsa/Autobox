package com.boxpro.service;

import com.boxpro.dto.request.LoginRequest;
import com.boxpro.dto.request.RegisterRequest;
import com.boxpro.dto.response.AuthResponse;
import com.boxpro.entity.Usuario;
import com.boxpro.repository.UsuarioRepository;
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
    private UsuarioRepository usuarioRepository;
    
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

        Usuario usuario = (Usuario) authentication.getPrincipal();

        return new AuthResponse(jwt, usuario.getId(), usuario.getNome(), 
                               usuario.getEmail(), usuario.getTipoUsuario());
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email já está em uso!");
        }

        // Verificar se CPF já existe (se fornecido)
        if (registerRequest.getCpf() != null && usuarioRepository.existsByCpf(registerRequest.getCpf())) {
            throw new RuntimeException("CPF já está em uso!");
        }

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setNome(registerRequest.getNome());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setSenha(passwordEncoder.encode(registerRequest.getSenha()));
        usuario.setTelefone(registerRequest.getTelefone());
        usuario.setCpf(registerRequest.getCpf());
        usuario.setTipoUsuario(registerRequest.getTipoUsuario());
        usuario.setAtivo(true);

        usuario = usuarioRepository.save(usuario);

        // Autenticar automaticamente após registro
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                registerRequest.getEmail(),
                registerRequest.getSenha()
            )
        );

        String jwt = jwtUtil.generateToken(authentication);

        return new AuthResponse(jwt, usuario.getId(), usuario.getNome(), 
                               usuario.getEmail(), usuario.getTipoUsuario());
    }

    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Usuario) {
            return (Usuario) authentication.getPrincipal();
        }
        throw new RuntimeException("Usuário não autenticado");
    }

    public AuthResponse refreshToken(Usuario usuario) {
        // Criar nova autenticação para gerar novo token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            usuario, null, usuario.getAuthorities());
        
        String newJwt = jwtUtil.generateToken(authentication);
        
        return new AuthResponse(newJwt, usuario.getId(), usuario.getNome(), 
                               usuario.getEmail(), usuario.getTipoUsuario());
    }
}