package com.boxpro.controller;

import com.boxpro.dto.request.LoginRequest;
import com.boxpro.dto.request.RegisterRequest;
import com.boxpro.dto.response.AuthResponse;
import com.boxpro.entity.Usuario;
import com.boxpro.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Tentativa de login para email: " + loginRequest.getEmail());
            AuthResponse response = authService.login(loginRequest);
            log.info("Login realizado com sucesso para usuário: " + response.getEmail());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warning("Credenciais inválidas para email: " + loginRequest.getEmail());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Email ou senha inválidos");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            log.severe("Erro durante login: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro interno do servidor");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Tentativa de registro para email: " + registerRequest.getEmail());
            AuthResponse response = authService.register(registerRequest);
            log.info("Usuário registrado com sucesso: " + response.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.warning("Erro durante registro: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", "error");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.severe("Erro interno durante registro: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro interno do servidor");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Usuario usuario = authService.getCurrentUser();
            log.info("Dados do usuário solicitados: " + usuario.getEmail());
            
            // Criar resposta sem dados sensíveis
            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("telefone", usuario.getTelefone());
            response.put("cpf", usuario.getCpf());
            response.put("tipoUsuario", usuario.getTipoUsuario());
            response.put("ativo", usuario.getAtivo());
            response.put("dataCriacao", usuario.getDataCriacao());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warning("Usuario não autenticado tentou acessar /me");
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuário não autenticado");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            log.severe("Erro ao obter dados do usuário: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro interno do servidor");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Com JWT stateless, o logout é feito no frontend removendo o token
        log.info("Logout solicitado");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout realizado com sucesso");
        response.put("status", "success");
        response.put("instruction", "Remova o token do localStorage/sessionStorage no frontend");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken() {
        try {
            Usuario usuario = authService.getCurrentUser();
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("usuario", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("tipoUsuario", usuario.getTipoUsuario());
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token inválido ou expirado");
            response.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken() {
        try {
            Usuario usuario = authService.getCurrentUser();
            // Gerar novo token para o usuário atual
            AuthResponse response = authService.refreshToken(usuario);
            log.info("Token renovado para usuário: " + usuario.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao renovar token: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Não foi possível renovar o token");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "BoxPro Authentication API");
        response.put("status", "running");
        response.put("version", "1.0.0");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("login", "/auth/login");
        endpoints.put("register", "/auth/register");
        endpoints.put("me", "/auth/me");
        endpoints.put("logout", "/auth/logout");
        endpoints.put("validate", "/auth/validate-token");
        endpoints.put("refresh", "/auth/refresh-token");
        
        response.put("endpoints", endpoints);
        return ResponseEntity.ok(response);
    }
}