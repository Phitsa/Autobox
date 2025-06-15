package com.boxpro.controller;

import com.boxpro.dto.request.EmpresaRequestDTO;
import com.boxpro.dto.response.EmpresaResponseDTO;
import com.boxpro.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/empresa")
@CrossOrigin(origins = "*")
public class EmpresaController {
    
    @Autowired
    private EmpresaService empresaService;
    
    /**
     * Endpoint para teste de conexão
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "empresa");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar dados da empresa
     */
    @GetMapping
    public ResponseEntity<EmpresaResponseDTO> getEmpresa() {
        try {
            EmpresaResponseDTO empresa = empresaService.getEmpresa();
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Criar nova empresa
     */
    @PostMapping
    public ResponseEntity<?> createEmpresa(@Valid @RequestBody EmpresaRequestDTO requestDTO) {
        try {
            EmpresaResponseDTO empresa = empresaService.createEmpresa(requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Empresa criada com sucesso");
            response.put("empresa", empresa);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Atualizar empresa existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpresa(@PathVariable Long id, 
                                         @Valid @RequestBody EmpresaRequestDTO requestDTO) {
        try {
            EmpresaResponseDTO empresa = empresaService.updateEmpresa(id, requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Empresa atualizada com sucesso");
            response.put("empresa", empresa);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Verificar se existe empresa cadastrada
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> existeEmpresa() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", empresaService.existeEmpresa());
        return ResponseEntity.ok(response);
    }
}
