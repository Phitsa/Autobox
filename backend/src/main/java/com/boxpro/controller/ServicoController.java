package com.boxpro.controller;

import com.boxpro.entity.Servico;
import com.boxpro.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {
    
    @Autowired
    private ServicoService servicoService;
    
    @GetMapping("/teste")
    public ResponseEntity<String> teste() {
        return ResponseEntity.ok("OK - Serviços funcionando!");
    }
    
    @GetMapping
    public ResponseEntity<List<Servico>> listarServicos() {
        try {
            List<Servico> servicos = servicoService.listarServicos();
            return ResponseEntity.ok(servicos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/ativos")
    public ResponseEntity<List<Servico>> listarServicosAtivos() {
        try {
            List<Servico> servicos = servicoService.listarServicosAtivos();
            return ResponseEntity.ok(servicos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        return servicoService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> criarServico(@RequestBody Servico servico) {
        try {
            Servico novoServico = servicoService.criarServico(servico);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoServico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro interno do servidor");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarServico(@PathVariable Long id, @RequestBody Servico servico) {
        try {
            Servico servicoAtualizado = servicoService.atualizarServico(id, servico);
            return ResponseEntity.ok(servicoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro interno do servidor");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarServico(@PathVariable Long id) {
        try {
            servicoService.deletarServico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro interno do servidor");
        }
    }
    
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Servico>> buscarPorNome(@PathVariable String nome) {
        try {
            List<Servico> servicos = servicoService.buscarPorNome(nome);
            return ResponseEntity.ok(servicos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Servico>> buscarPorCategoria(@PathVariable Long categoriaId) {
        try {
            List<Servico> servicos = servicoService.buscarPorCategoria(categoriaId);
            return ResponseEntity.ok(servicos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}