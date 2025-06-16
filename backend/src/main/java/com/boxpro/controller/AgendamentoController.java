package com.boxpro.controller;

import com.boxpro.entity.Agendamento;
import com.boxpro.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    // ===== ENDPOINTS PÚBLICOS =====

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ AgendamentoController funcionando!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", List.of(
            "GET /api/agendamentos - Listar com paginação",
            "GET /api/agendamentos/todos - Listar todos",
            "GET /api/agendamentos/{id} - Buscar por ID",
            "POST /api/agendamentos - Criar agendamento",
            "PUT /api/agendamentos/{id} - Atualizar agendamento",
            "DELETE /api/agendamentos/{id} - Deletar agendamento"
        ));
        return ResponseEntity.ok(response);
    }

    // ===== CRUD BÁSICO =====

    // Endpoint paginado para a lista principal
    @GetMapping
    public ResponseEntity<Page<Agendamento>> listarAgendamentos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataAgendamento") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Agendamento> agendamentos = agendamentoService.listarAgendamentosPaginados(pageable);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Endpoint para buscar todos os agendamentos
    @GetMapping("/todos")
    public ResponseEntity<List<Agendamento>> listarTodos() {
        try {
            List<Agendamento> agendamentos = agendamentoService.listarAgendamentos();
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Integer id) {
        try {
            Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
            if (agendamento.isPresent()) {
                return ResponseEntity.ok(agendamento.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criarAgendamento(@Valid @RequestBody Agendamento agendamento) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validações básicas
            if (agendamento.getClienteId() == null) {
                response.put("error", "Cliente é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getVeiculoId() == null) {
                response.put("error", "Veículo é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getServicoId() == null) {
                response.put("error", "Serviço é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getDataAgendamento() == null) {
                response.put("error", "Data do agendamento é obrigatória");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getHoraInicio() == null) {
                response.put("error", "Hora de início é obrigatória");
                return ResponseEntity.badRequest().body(response);
            }

            Agendamento novoAgendamento = agendamentoService.criarAgendamento(agendamento);
            
            response.put("message", "Agendamento criado com sucesso");
            response.put("agendamento", novoAgendamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizarAgendamento(
            @PathVariable Integer id, 
            @Valid @RequestBody Agendamento agendamento) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Agendamento> agendamentoExistente = agendamentoService.buscarPorId(id);
            
            if (!agendamentoExistente.isPresent()) {
                response.put("error", "Agendamento não encontrado");
                return ResponseEntity.notFound().build();
            }

            // Validações básicas
            if (agendamento.getClienteId() == null) {
                response.put("error", "Cliente é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getVeiculoId() == null) {
                response.put("error", "Veículo é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getServicoId() == null) {
                response.put("error", "Serviço é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getDataAgendamento() == null) {
                response.put("error", "Data do agendamento é obrigatória");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (agendamento.getHoraInicio() == null) {
                response.put("error", "Hora de início é obrigatória");
                return ResponseEntity.badRequest().body(response);
            }

            // Definir ID e atualizar
            agendamento.setId(id);
            Agendamento agendamentoAtualizado = agendamentoService.atualizarAgendamento(agendamento);
            
            response.put("message", "Agendamento atualizado com sucesso");
            response.put("agendamento", agendamentoAtualizado);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarAgendamento(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
            
            if (!agendamento.isPresent()) {
                response.put("error", "Agendamento não encontrado");
                return ResponseEntity.notFound().build();
            }

            agendamentoService.deletarAgendamento(id);
            
            response.put("message", "Agendamento deletado com sucesso");
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

    @GetMapping("/data/{data}")
    public ResponseEntity<List<Agendamento>> buscarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            List<Agendamento> agendamentos = agendamentoService.buscarPorDataAgendamento(data);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Agendamento>> buscarPorCliente(@PathVariable Integer clienteId) {
        try {
            List<Agendamento> agendamentos = agendamentoService.buscarPorClienteId(clienteId);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<Agendamento>> buscarPorFuncionario(@PathVariable Integer funcionarioId) {
        try {
            List<Agendamento> agendamentos = agendamentoService.buscarPorFuncionarioId(funcionarioId);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Agendamento>> buscarPorStatus(@PathVariable String status) {
        try {
            List<Agendamento> agendamentos = agendamentoService.buscarPorStatus(status);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Agendamento>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        try {
            List<Agendamento> agendamentos = agendamentoService.buscarPorPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/futuros")
    public ResponseEntity<List<Agendamento>> buscarAgendamentosFuturos() {
        try {
            List<Agendamento> agendamentos = agendamentoService.buscarAgendamentosFuturos();
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Agendamento>> buscarAgendamentosAtivos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        try {
            LocalDate dataConsulta = (data != null) ? data : LocalDate.now();
            List<Agendamento> agendamentos = agendamentoService.buscarAgendamentosAtivos(dataConsulta);
            return ResponseEntity.ok(agendamentos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS DE GERENCIAMENTO =====

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> atualizarStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            @RequestParam Integer funcionarioId,
            @RequestParam(required = false) String motivo) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Agendamento> agendamento = agendamentoService.buscarPorId(id);
            
            if (!agendamento.isPresent()) {
                response.put("error", "Agendamento não encontrado");
                return ResponseEntity.notFound().build();
            }

            Agendamento agendamentoAtualizado = agendamentoService.atualizarStatus(id, status, funcionarioId, motivo);
            
            response.put("message", "Status do agendamento atualizado com sucesso");
            response.put("agendamento", agendamentoAtualizado);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== ENDPOINTS DE ESTATÍSTICAS =====

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> estatisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            List<Agendamento> todos = agendamentoService.listarAgendamentos();
            List<Agendamento> agendados = agendamentoService.buscarPorStatus("agendado");
            List<Agendamento> concluidos = agendamentoService.buscarPorStatus("concluido");
            List<Agendamento> cancelados = agendamentoService.buscarPorStatus("cancelado");
            List<Agendamento> emAndamento = agendamentoService.buscarPorStatus("em_andamento");
            List<Agendamento> futuros = agendamentoService.buscarAgendamentosFuturos();
            
            stats.put("total", todos.size());
            stats.put("agendados", agendados.size());
            stats.put("concluidos", concluidos.size());
            stats.put("cancelados", cancelados.size());
            stats.put("emAndamento", emAndamento.size());
            stats.put("futuros", futuros.size());
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}