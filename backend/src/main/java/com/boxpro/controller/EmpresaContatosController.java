package com.boxpro.controller;

import com.boxpro.dto.request.EmpresaContatosRequestDTO;
import com.boxpro.dto.response.EmpresaContatosResponseDTO;
import com.boxpro.entity.EmpresaContatos.TipoContato;
import com.boxpro.service.EmpresaContatosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresa-contatos")
@CrossOrigin(origins = "*")
public class EmpresaContatosController {
    
    @Autowired
    private EmpresaContatosService contatosService;
    
    /**
     * Endpoint para teste de conexão
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "empresa-contatos");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar todos os contatos de uma empresa
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<EmpresaContatosResponseDTO>> getContatosByEmpresa(@PathVariable Long empresaId) {
        try {
            List<EmpresaContatosResponseDTO> contatos = contatosService.getContatosByEmpresa(empresaId);
            return ResponseEntity.ok(contatos);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Buscar contato específico por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaContatosResponseDTO> getContatoById(@PathVariable Long id) {
        try {
            EmpresaContatosResponseDTO contato = contatosService.getContatoById(id);
            return ResponseEntity.ok(contato);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Buscar contatos por tipo
     */
    @GetMapping("/empresa/{empresaId}/tipo/{tipoContato}")
    public ResponseEntity<List<EmpresaContatosResponseDTO>> getContatosByTipo(
            @PathVariable Long empresaId, 
            @PathVariable TipoContato tipoContato) {
        try {
            List<EmpresaContatosResponseDTO> contatos = contatosService.getContatosByEmpresaAndTipo(empresaId, tipoContato);
            return ResponseEntity.ok(contatos);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Buscar contatos principais
     */
    @GetMapping("/empresa/{empresaId}/principais")
    public ResponseEntity<List<EmpresaContatosResponseDTO>> getContatosPrincipais(@PathVariable Long empresaId) {
        try {
            List<EmpresaContatosResponseDTO> contatos = contatosService.getContatosPrincipais(empresaId);
            return ResponseEntity.ok(contatos);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Criar novo contato
     */
    @PostMapping
    public ResponseEntity<?> createContato(@Valid @RequestBody EmpresaContatosRequestDTO requestDTO) {
        try {
            EmpresaContatosResponseDTO contato = contatosService.createContato(requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contato criado com sucesso");
            response.put("contato", contato);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Atualizar contato existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContato(@PathVariable Long id, 
                                         @Valid @RequestBody EmpresaContatosRequestDTO requestDTO) {
        try {
            EmpresaContatosResponseDTO contato = contatosService.updateContato(id, requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contato atualizado com sucesso");
            response.put("contato", contato);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Deletar contato (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContato(@PathVariable Long id) {
        try {
            contatosService.deleteContato(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contato deletado com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Definir contato como principal
     */
    @PatchMapping("/{id}/principal")
    public ResponseEntity<?> definirComoPrincipal(@PathVariable Long id) {
        try {
            EmpresaContatosResponseDTO contato = contatosService.definirComoPrincipal(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contato definido como principal com sucesso");
            response.put("contato", contato);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Listar todos os contatos
     */
    @GetMapping
    public ResponseEntity<List<EmpresaContatosResponseDTO>> getAllContatos() {
        try {
            List<EmpresaContatosResponseDTO> contatos = contatosService.getAllContatos();
            return ResponseEntity.ok(contatos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Listar tipos de contato disponíveis
     */
    @GetMapping("/tipos")
    public ResponseEntity<Map<String, Object>> getTiposContato() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> tipos = new HashMap<>();
        
        for (TipoContato tipo : TipoContato.values()) {
            tipos.put(tipo.getCodigo(), tipo.getNome());
        }
        
        response.put("tipos", tipos);
        return ResponseEntity.ok(response);
    }
}