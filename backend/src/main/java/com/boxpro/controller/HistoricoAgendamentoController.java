package com.boxpro.controller;

import com.boxpro.entity.HistoricoAgendamento;
import com.boxpro.service.HistoricoAgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/historico-agendamentos")
@CrossOrigin(origins = "*")
public class HistoricoAgendamentoController {

    @Autowired
    private HistoricoAgendamentoService historicoService;

    // ===== ENDPOINTS PÚBLICOS =====

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ HistoricoAgendamentoController funcionando!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", List.of(
            "GET /api/historico-agendamentos - Listar com paginação",
            "GET /api/historico-agendamentos/todos - Listar todos",
            "GET /api/historico-agendamentos/{id} - Buscar por ID",
            "POST /api/historico-agendamentos - Criar histórico",
            "DELETE /api/historico-agendamentos/{id} - Deletar histórico"
        ));
        return ResponseEntity.ok(response);
    }

    // ===== CRUD BÁSICO =====

    // Endpoint paginado para a lista principal
    @GetMapping
    public ResponseEntity<Page<HistoricoAgendamento>> listarHistoricos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataAcao") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<HistoricoAgendamento> historicos = historicoService.listarHistoricosPaginados(pageable);
            return ResponseEntity.ok(historicos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para buscar todos os históricos
    @GetMapping("/todos")
    public ResponseEntity<List<HistoricoAgendamento>> listarTodos() {
        try {
            List<HistoricoAgendamento> historicos = historicoService.listarHistoricos();
            return ResponseEntity.ok(historicos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoAgendamento> buscarPorId(@PathVariable Integer id) {
        try {
            Optional<HistoricoAgendamento> historico = historicoService.buscarPorId(id);
            if (historico.isPresent()) {
                return ResponseEntity.ok(historico.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarHistorico(@Valid @RequestBody HistoricoAgendamento historico) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validações básicas
            if (historico.getAgendamento() == null) {
                response.put("error", "Agendamento é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (historico.getFuncionarioId() == null) {
                response.put("error", "Funcionário é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (historico.getAcao() == null || historico.getAcao().trim().isEmpty()) {
                response.put("error", "Ação é obrigatória");
                return ResponseEntity.badRequest().body(response);
            }

            HistoricoAgendamento novoHistorico = historicoService.criarHistorico(historico);
            
            response.put("message", "Histórico criado com sucesso");
            response.put("historico", novoHistorico);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarHistorico(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<HistoricoAgendamento> historico = historicoService.buscarPorId(id);
            
            if (!historico.isPresent()) {
                response.put("error", "Histórico não encontrado");
                return ResponseEntity.notFound().build();
            }

            historicoService.deletarHistorico(id);
            
            response.put("message", "Histórico deletado com sucesso");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== ENDPOINTS DE BUSCA =====

    @GetMapping("/agendamento/{agendamentoId}")
    public ResponseEntity<List<HistoricoAgendamento>> buscarPorAgendamento(@PathVariable Integer agendamentoId) {
        try {
            List<HistoricoAgendamento> historicos = historicoService.buscarPorAgendamentoId(agendamentoId);
            return ResponseEntity.ok(historicos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<HistoricoAgendamento>> buscarPorFuncionario(@PathVariable Integer funcionarioId) {
        try {
            List<HistoricoAgendamento> historicos = historicoService.buscarPorFuncionarioId(funcionarioId);
            return ResponseEntity.ok(historicos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/acao/{acao}")
    public ResponseEntity<List<HistoricoAgendamento>> buscarPorAcao(@PathVariable String acao) {
        try {
            List<HistoricoAgendamento> historicos = historicoService.buscarPorAcao(acao);
            return ResponseEntity.ok(historicos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS DE ESTATÍSTICAS =====

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> estatisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            List<HistoricoAgendamento> todos = historicoService.listarHistoricos();
            List<HistoricoAgendamento> criados = historicoService.buscarPorAcao("CRIADO");
            List<HistoricoAgendamento> atualizados = historicoService.buscarPorAcao("ATUALIZADO");
            List<HistoricoAgendamento> statusAlterados = historicoService.buscarPorAcao("STATUS_ALTERADO");
            
            stats.put("total", todos.size());
            stats.put("criados", criados.size());
            stats.put("atualizados", atualizados.size());
            stats.put("statusAlterados", statusAlterados.size());
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}