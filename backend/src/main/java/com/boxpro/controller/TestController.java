package com.boxpro.controller;

import com.boxpro.entity.Usuario;
import com.boxpro.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private AuthService authService;

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este é um endpoint público - sem autenticação necessária");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    public ResponseEntity<Map<String, Object>> protectedEndpoint() {
        Usuario usuario = authService.getCurrentUser();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Este é um endpoint protegido - autenticação necessária");
        response.put("usuario", usuario.getNome());
        response.put("email", usuario.getEmail());
        response.put("tipo", usuario.getTipoUsuario());
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este é um endpoint apenas para ADMIN");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/funcionario")
    @PreAuthorize("hasRole('FUNCIONARIO') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> funcionarioEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este é um endpoint para FUNCIONARIO e ADMIN");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cliente")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('FUNCIONARIO') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> clienteEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Este é um endpoint para todos os tipos de usuário");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}