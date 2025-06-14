package com.boxpro.controller;

import com.boxpro.entity.Funcionario;
import com.boxpro.entity.enums.TipoFuncionario;
import com.boxpro.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    // ===== ENDPOINTS PÚBLICOS =====

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "✅ FuncionarioController funcionando!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("endpoints", List.of(
            "GET /api/funcionarios - Listar todos",
            "GET /api/funcionarios/{id} - Buscar por ID",
            "POST /api/funcionarios - Criar funcionário",
            "PUT /api/funcionarios/{id} - Atualizar funcionário",
            "DELETE /api/funcionarios/{id} - Desativar funcionário"
        ));
        return ResponseEntity.ok(response);
    }

    // ===== CRUD BÁSICO (ADMIN ONLY) =====

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Funcionario>> listarTodos() {
        try {
            List<Funcionario> funcionarios = funcionarioService.listarFuncionarios();
            return ResponseEntity.ok(funcionarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ativos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Funcionario>> listarAtivos() {
        try {
            List<Funcionario> funcionarios = funcionarioService.listarFuncionariosAtivos();
            return ResponseEntity.ok(funcionarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/disponiveis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Funcionario>> listarDisponiveis() {
        try {
            List<Funcionario> funcionarios = funcionarioService.listarFuncionariosDisponiveis();
            return ResponseEntity.ok(funcionarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Integer id) {
        try {
            Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);
            if (funcionario.isPresent()) {
                return ResponseEntity.ok(funcionario.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> criarFuncionario(@Valid @RequestBody Funcionario funcionario) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validações básicas
            if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
                response.put("error", "Nome é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (funcionario.getEmail() == null || funcionario.getEmail().trim().isEmpty()) {
                response.put("error", "Email é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (funcionario.getSenha() == null || funcionario.getSenha().length() < 6) {
                response.put("error", "Senha deve ter pelo menos 6 caracteres");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar se email já existe
            if (funcionarioService.emailExiste(funcionario.getEmail())) {
                response.put("error", "Email já está em uso");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar se CPF já existe (se fornecido)
            if (funcionario.getCpf() != null && !funcionario.getCpf().trim().isEmpty()) {
                if (funcionarioService.cpfExiste(funcionario.getCpf())) {
                    response.put("error", "CPF já está em uso");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Definir tipo padrão se não fornecido
            if (funcionario.getTipoFuncionario() == null) {
                funcionario.setTipoFuncionario(TipoFuncionario.FUNCIONARIO);
            }

            Funcionario novoFuncionario = funcionarioService.criarFuncionario(funcionario);
            
            response.put("message", "Funcionário criado com sucesso");
            response.put("funcionario", novoFuncionario);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> atualizarFuncionario(
            @PathVariable Integer id, 
            @Valid @RequestBody Funcionario funcionario) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Funcionario> funcionarioExistente = funcionarioService.buscarPorId(id);
            
            if (!funcionarioExistente.isPresent()) {
                response.put("error", "Funcionário não encontrado");
                return ResponseEntity.notFound().build();
            }

            // Validações básicas
            if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
                response.put("error", "Nome é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (funcionario.getEmail() == null || funcionario.getEmail().trim().isEmpty()) {
                response.put("error", "Email é obrigatório");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar se email já existe em outro funcionário
            Optional<Funcionario> funcionarioComEmail = funcionarioService.buscarPorEmail(funcionario.getEmail());
            if (funcionarioComEmail.isPresent() && !funcionarioComEmail.get().getId().equals(id)) {
                response.put("error", "Email já está em uso por outro funcionário");
                return ResponseEntity.badRequest().body(response);
            }

            // Verificar se CPF já existe em outro funcionário (se fornecido)
            if (funcionario.getCpf() != null && !funcionario.getCpf().trim().isEmpty()) {
                Optional<Funcionario> funcionarioComCpf = funcionarioService.buscarPorCpf(funcionario.getCpf());
                if (funcionarioComCpf.isPresent() && !funcionarioComCpf.get().getId().equals(id)) {
                    response.put("error", "CPF já está em uso por outro funcionário");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Definir ID e atualizar
            funcionario.setId(id);
            Funcionario funcionarioAtualizado = funcionarioService.atualizarFuncionario(funcionario);
            
            response.put("message", "Funcionário atualizado com sucesso");
            response.put("funcionario", funcionarioAtualizado);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> desativarFuncionario(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);
            
            if (!funcionario.isPresent()) {
                response.put("error", "Funcionário não encontrado");
                return ResponseEntity.notFound().build();
            }

            funcionarioService.desativarFuncionario(id);
            
            response.put("message", "Funcionário desativado com sucesso");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== ENDPOINTS DE BUSCA =====

    @GetMapping("/buscar/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Funcionario> buscarPorEmail(@PathVariable String email) {
        try {
            Optional<Funcionario> funcionario = funcionarioService.buscarPorEmail(email);
            if (funcionario.isPresent()) {
                return ResponseEntity.ok(funcionario.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscar/nome/{nome}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Funcionario>> buscarPorNome(@PathVariable String nome) {
        try {
            List<Funcionario> funcionarios = funcionarioService.buscarFuncionarioPorNome(nome);
            return ResponseEntity.ok(funcionarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Funcionario>> listarPorTipo(@PathVariable String tipo) {
        try {
            TipoFuncionario tipoFuncionario = TipoFuncionario.valueOf(tipo.toUpperCase());
            List<Funcionario> funcionarios = funcionarioService.listarPorTipo(tipoFuncionario);
            return ResponseEntity.ok(funcionarios);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ===== ENDPOINTS DE GERENCIAMENTO =====

    @PutMapping("/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bloquearFuncionario(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);
            
            if (!funcionario.isPresent()) {
                response.put("error", "Funcionário não encontrado");
                return ResponseEntity.notFound().build();
            }

            funcionarioService.bloquearFuncionario(id);
            
            response.put("message", "Funcionário bloqueado com sucesso");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}/desbloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> desbloquearFuncionario(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Funcionario> funcionario = funcionarioService.buscarPorId(id);
            
            if (!funcionario.isPresent()) {
                response.put("error", "Funcionário não encontrado");
                return ResponseEntity.notFound().build();
            }

            funcionarioService.desbloquearFuncionario(id);
            
            response.put("message", "Funcionário desbloqueado com sucesso");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== ENDPOINTS DE ESTATÍSTICAS =====

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> estatisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            List<Funcionario> todos = funcionarioService.listarFuncionarios();
            List<Funcionario> ativos = funcionarioService.listarFuncionariosAtivos();
            List<Funcionario> disponiveis = funcionarioService.listarFuncionariosDisponiveis();
            List<Funcionario> admins = funcionarioService.listarPorTipo(TipoFuncionario.ADMIN);
            List<Funcionario> funcionarios = funcionarioService.listarPorTipo(TipoFuncionario.FUNCIONARIO);
            
            stats.put("total", todos.size());
            stats.put("ativos", ativos.size());
            stats.put("disponiveis", disponiveis.size());
            stats.put("admins", admins.size());
            stats.put("funcionarios", funcionarios.size());
            stats.put("inativos", todos.size() - ativos.size());
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}