package com.boxpro.controller;

import com.boxpro.dto.request.LoginRequest;
import com.boxpro.dto.request.RegisterRequest;
import com.boxpro.dto.response.AuthResponse;
import com.boxpro.entity.Funcionario;
import com.boxpro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "APIs para autenticação e autorização de funcionários")
public class AuthController {

    private static final Logger log = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private AuthService authService;

    @Operation(
        summary = "Fazer login",
        description = "Autentica um funcionário e retorna um token JWT para acesso aos endpoints protegidos"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    value = "{\"token\":\"eyJhbGciOiJIUzUxMiJ9...\",\"tipo\":\"Bearer\",\"id\":1,\"nome\":\"Administrador\",\"email\":\"admin@boxpro.com\",\"tipoFuncionario\":\"ADMIN\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Credenciais inválidas",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\":\"Email ou senha inválidos\",\"status\":\"error\"}"
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Parameter(description = "Dados de login do funcionário", required = true)
        @Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Tentativa de login para email: " + loginRequest.getEmail());
            AuthResponse response = authService.login(loginRequest);
            log.info("Login realizado com sucesso para funcionário: " + response.getEmail());
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

    @Operation(
        summary = "Registrar novo funcionário",
        description = "Cria um novo funcionário no sistema e retorna um token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Funcionário registrado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Dados inválidos ou email já em uso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\":\"Email já está em uso!\",\"status\":\"error\"}"
                )
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
        @Parameter(description = "Dados para registro do funcionário", required = true)
        @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Tentativa de registro para email: " + registerRequest.getEmail());
            AuthResponse response = authService.register(registerRequest);
            log.info("Funcionário registrado com sucesso: " + response.getEmail());
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

    @Operation(
        summary = "Obter dados do funcionário logado",
        description = "Retorna os dados do funcionário autenticado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Dados do funcionário retornados com sucesso",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"id\":1,\"nome\":\"Administrador\",\"email\":\"admin@boxpro.com\",\"telefone\":\"(11) 99999-9999\",\"tipoFuncionario\":\"ADMIN\",\"ativo\":true}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Token inválido ou não fornecido",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"message\":\"Funcionário não autenticado\",\"status\":\"error\"}"
                )
            )
        )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Funcionario funcionario = authService.getCurrentUser();
            log.info("Dados do funcionário solicitados: " + funcionario.getEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", funcionario.getId());
            response.put("nome", funcionario.getNome());
            response.put("email", funcionario.getEmail());
            response.put("telefone", funcionario.getTelefone());
            response.put("cpf", funcionario.getCpf());
            response.put("tipoFuncionario", funcionario.getTipoFuncionario());
            response.put("ativo", funcionario.getAtivo());
            response.put("dataCriacao", funcionario.getDataCriacao());
            response.put("ultimoLogin", funcionario.getUltimoLogin());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warning("Funcionário não autenticado tentou acessar /me");
            Map<String, String> error = new HashMap<>();
            error.put("message", "Funcionário não autenticado");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            log.severe("Erro ao obter dados do funcionário: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro interno do servidor");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(
        summary = "Validar token JWT",
        description = "Verifica se o token JWT fornecido é válido"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken() {
        try {
            Funcionario funcionario = authService.getCurrentUser();
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("funcionario", funcionario.getNome());
            response.put("email", funcionario.getEmail());
            response.put("tipoFuncionario", funcionario.getTipoFuncionario());
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

    @Operation(
        summary = "Renovar token JWT",
        description = "Gera um novo token JWT para o funcionário autenticado"
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken() {
        try {
            Funcionario funcionario = authService.getCurrentUser();
            AuthResponse response = authService.refreshToken(funcionario);
            log.info("Token renovado para funcionário: " + funcionario.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.severe("Erro ao renovar token: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Não foi possível renovar o token");
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Operation(
        summary = "Fazer logout",
        description = "Endpoint para logout (com JWT stateless, remove o token no frontend)"
    )
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        log.info("Logout solicitado");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout realizado com sucesso");
        response.put("status", "success");
        response.put("instruction", "Remova o token do localStorage/sessionStorage no frontend");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Status da API",
        description = "Retorna informações sobre o status e endpoints disponíveis da API"
    )
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
