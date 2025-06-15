package com.boxpro.controller;

import com.boxpro.dto.request.EmpresaContatosRequestDTO;
import com.boxpro.dto.response.EmpresaContatosResponseDTO;
import com.boxpro.entity.EmpresaContatos.TipoContato;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.service.EmpresaContatosService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresa-contatos")
@CrossOrigin(origins = "*")
public class EmpresaContatosController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmpresaContatosController.class);
    
    @Autowired
    private EmpresaContatosService contatosService;
    
    /**
     * Endpoint para teste de conexão
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        logger.info("🔍 Verificando status do serviço empresa-contatos");
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "empresa-contatos");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        logger.info("✅ Serviço empresa-contatos está online");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar todos os contatos de uma empresa
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<?> getContatosByEmpresa(@PathVariable Long empresaId) {
        logger.info("📞 GET /empresa/{} - Buscando contatos da empresa", empresaId);
        
        try {
            List<EmpresaContatosResponseDTO> contatos = contatosService.getContatosByEmpresa(empresaId);
            logger.info("✅ Retornando {} contatos para empresa {}", contatos.size(), empresaId);
            return ResponseEntity.ok(contatos);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Empresa {} não encontrada: {}", empresaId, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("empresaId", empresaId.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao buscar contatos da empresa {}: {}", empresaId, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Buscar contato específico por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getContatoById(@PathVariable Long id) {
        logger.info("📞 GET /{} - Buscando contato por ID", id);
        
        try {
            EmpresaContatosResponseDTO contato = contatosService.getContatoById(id);
            logger.info("✅ Contato {} encontrado", id);
            return ResponseEntity.ok(contato);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Contato {} não encontrado: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("contatoId", id.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao buscar contato {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Buscar contatos por tipo
     */
    @GetMapping("/empresa/{empresaId}/tipo/{tipoContato}")
    public ResponseEntity<?> getContatosByTipo(
            @PathVariable Long empresaId, 
            @PathVariable String tipoContato) {
        
        logger.info("📞 GET /empresa/{}/tipo/{} - Buscando contatos por tipo", empresaId, tipoContato);
        
        try {
            TipoContato tipo = TipoContato.fromString(tipoContato);
            List<EmpresaContatosResponseDTO> contatos = contatosService.getContatosByEmpresaAndTipo(empresaId, tipo);
            logger.info("✅ Retornando {} contatos tipo {} para empresa {}", contatos.size(), tipo, empresaId);
            return ResponseEntity.ok(contatos);
            
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Tipo de contato inválido: {}", tipoContato);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Tipo de contato inválido: " + tipoContato);
            error.put("tiposValidos", "telefone, celular, whatsapp, email, fax");
            return ResponseEntity.badRequest().body(error);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Empresa {} não encontrada: {}", empresaId, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao buscar contatos por tipo: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Buscar contatos principais
     */
    @GetMapping("/empresa/{empresaId}/principais")
    public ResponseEntity<?> getContatosPrincipais(@PathVariable Long empresaId) {
        logger.info("🌟 GET /empresa/{}/principais - Buscando contatos principais", empresaId);
        
        try {
            List<EmpresaContatosResponseDTO> contatos = contatosService.getContatosPrincipais(empresaId);
            logger.info("✅ Retornando {} contatos principais para empresa {}", contatos.size(), empresaId);
            return ResponseEntity.ok(contatos);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Empresa {} não encontrada: {}", empresaId, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao buscar contatos principais: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Criar novo contato
     */
    @PostMapping
    public ResponseEntity<?> createContato(@Valid @RequestBody EmpresaContatosRequestDTO requestDTO) {
        logger.info("📝 POST / - Criando novo contato: {}", requestDTO);
        
        try {
            EmpresaContatosResponseDTO contato = contatosService.createContato(requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contato criado com sucesso");
            response.put("contato", contato);
            
            logger.info("✅ Contato criado com sucesso - ID: {}", contato.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Recurso não encontrado ao criar contato: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (BusinessException e) {
            logger.warn("⚠️ Erro de negócio ao criar contato: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao criar contato: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Atualizar contato existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContato(@PathVariable Long id, 
                                         @Valid @RequestBody EmpresaContatosRequestDTO requestDTO) {
        logger.info("✏️ PUT /{} - Atualizando contato: {}", id, requestDTO);
        
        try {
            EmpresaContatosResponseDTO contato = contatosService.updateContato(id, requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contato atualizado com sucesso");
            response.put("contato", contato);
            
            logger.info("✅ Contato {} atualizado com sucesso", id);
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Recurso não encontrado ao atualizar contato {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (BusinessException e) {
            logger.warn("⚠️ Erro de negócio ao atualizar contato {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao atualizar contato {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Deletar contato (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContato(@PathVariable Long id) {
        logger.info("🗑️ DELETE /{} - Deletando contato", id);
        
        try {
            contatosService.deleteContato(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Contato deletado com sucesso");
            response.put("contatoId", id.toString());
            
            logger.info("✅ Contato {} deletado com sucesso", id);
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Contato {} não encontrado para deleção: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (BusinessException e) {
            logger.warn("⚠️ Erro de negócio ao deletar contato {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao deletar contato {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Definir contato como principal
     */
    @PatchMapping("/{id}/principal")
    public ResponseEntity<?> definirComoPrincipal(@PathVariable Long id) {
        logger.info("🌟 PATCH /{}/principal - Definindo contato como principal", id);
        
        try {
            EmpresaContatosResponseDTO contato = contatosService.definirComoPrincipal(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contato definido como principal com sucesso");
            response.put("contato", contato);
            
            logger.info("✅ Contato {} definido como principal", id);
            return ResponseEntity.ok(response);
            
        } catch (ResourceNotFoundException e) {
            logger.warn("⚠️ Contato {} não encontrado: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            
        } catch (BusinessException e) {
            logger.warn("⚠️ Erro de negócio ao definir contato {} como principal: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao definir contato {} como principal: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Listar todos os contatos
     */
    @GetMapping
    public ResponseEntity<?> getAllContatos() {
        logger.info("📋 GET / - Listando todos os contatos");
        
        try {
            List<EmpresaContatosResponseDTO> contatos = contatosService.getAllContatos();
            logger.info("✅ Retornando {} contatos no total", contatos.size());
            return ResponseEntity.ok(contatos);
            
        } catch (BusinessException e) {
            logger.warn("⚠️ Erro de negócio ao listar contatos: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao listar contatos: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Listar tipos de contato disponíveis
     */
    @GetMapping("/tipos")
    public ResponseEntity<?> getTiposContato() {
        logger.info("📋 GET /tipos - Listando tipos de contato disponíveis");
        
        try {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> tipos = new HashMap<>();
            
            // Retornar tipos em minúsculo para compatibilidade com frontend
            for (TipoContato tipo : TipoContato.values()) {
                tipos.put(tipo.name().toLowerCase(), tipo.getNome());
            }
            
            response.put("tipos", tipos);
            response.put("total", tipos.size());
            
            logger.info("✅ Retornando {} tipos de contato", tipos.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao listar tipos de contato: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}