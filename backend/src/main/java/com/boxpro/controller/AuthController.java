package com.boxpro.controller;

import com.boxpro.dto.request.LoginRequest;
import com.boxpro.dto.request.UsuarioCreateRequest;
import com.boxpro.dto.response.LoginResponse;
import com.boxpro.dto.response.MessageResponse;
import com.boxpro.dto.response.UsuarioResponse;
import com.boxpro.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints de autenticação e registro")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário e retorna um token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Realizar logout", description = "Invalida o token do usuário")
    public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(MessageResponse.success("Logout realizado com sucesso"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Renova o token JWT do usuário")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader("Authorization") String token) {
        LoginResponse response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token", description = "Verifica se o token é válido")
    public ResponseEntity<MessageResponse> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(MessageResponse.success("Token válido"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(MessageResponse.error("Token inválido"));
    }
}