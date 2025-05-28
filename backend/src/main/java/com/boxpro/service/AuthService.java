package com.boxpro.service;

import com.boxpro.dto.request.LoginRequest;
import com.boxpro.dto.request.UsuarioCreateRequest;
import com.boxpro.dto.response.LoginResponse;
import com.boxpro.dto.response.UsuarioResponse;
import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.TipoUsuario;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.UnauthorizedException;
import com.boxpro.mapper.UsuarioMapper;
import com.boxpro.repository.UsuarioRepository;
import com.boxpro.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioMapper usuarioMapper;
    
    // Set para armazenar tokens invalidados (em produção, usar Redis)
    private final Set<String> blacklistedTokens = new HashSet<>();

    /**
     * Realizar login
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Tentativa de login para: {}", request.getEmail());
        
        try {
            // Autenticar usuário
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getSenha()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Gerar token JWT
            String jwt = tokenProvider.generateToken(authentication);
            
            // Buscar usuário para retornar informações
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciais inválidas"));

            log.info("Login realizado com sucesso para: {}", request.getEmail());

            return LoginResponse.builder()
                .token(jwt)
                .tipo("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .usuario(LoginResponse.UsuarioInfo.builder()
                    .id(usuario.getId())
                    .nome(usuario.getNome())
                    .email(usuario.getEmail())
                    .tipoUsuario(usuario.getTipoUsuario())
                    .build())
                .build();

        } catch (BadCredentialsException e) {
            log.error("Falha no login - credenciais inválidas para: {}", request.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos");
        }
    }

    /**
     * Registrar novo usuário
     */
    public UsuarioResponse register(UsuarioCreateRequest request) {
        log.info("Registrando novo usuário: {}", request.getEmail());
        
        // Verificar se email já existe
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        // Criar novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setTelefone(request.getTelefone());
        usuario.setTipoUsuario(TipoUsuario.CLIENTE); // Sempre registra como cliente
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        
        log.info("Usuário registrado com sucesso: {}", usuarioSalvo.getEmail());
        
        return usuarioMapper.toResponse(usuarioSalvo);
    }

    /**
     * Realizar logout
     */
    public void logout(String token) {
        // Remover "Bearer " do token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // Adicionar token à blacklist
        blacklistedTokens.add(token);
        
        // Limpar contexto de segurança
        SecurityContextHolder.clearContext();
        
        log.info("Logout realizado com sucesso");
    }

    /**
     * Renovar token
     */
    public LoginResponse refreshToken(String token) {
        // Remover "Bearer " do token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // Validar token atual
        if (!tokenProvider.validateToken(token)) {
            throw new UnauthorizedException("Token inválido ou expirado");
        }
        
        // Verificar se token está na blacklist
        if (blacklistedTokens.contains(token)) {
            throw new UnauthorizedException("Token foi invalidado");
        }
        
        // Extrair username e gerar novo token
        String username = tokenProvider.getUsernameFromToken(token);
        String newToken = tokenProvider.generateToken(username);
        
        // Buscar usuário
        Usuario usuario = usuarioRepository.findByEmail(username)
            .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
        
        // Invalidar token antigo
        blacklistedTokens.add(token);
        
        return LoginResponse.builder()
            .token(newToken)
            .tipo("Bearer")
            .expiresIn(tokenProvider.getExpirationTime())
            .usuario(LoginResponse.UsuarioInfo.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .tipoUsuario(usuario.getTipoUsuario())
                .build())
            .build();
    }

    /**
     * Validar token
     */
    public boolean validateToken(String token) {
        // Remover "Bearer " do token
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // Verificar se está na blacklist
        if (blacklistedTokens.contains(token)) {
            return false;
        }
        
        return tokenProvider.validateToken(token);
    }

    /**
     * Obter usuário autenticado atual
     */
    public Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        
        String email = authentication.getName();
        
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
    }
}