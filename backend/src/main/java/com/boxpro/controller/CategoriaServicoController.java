package com.boxpro.controller;

import com.boxpro.entity.CategoriaServico;
import com.boxpro.service.CategoriaServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaServicoController {
    
    @Autowired
    private CategoriaServicoService categoriaService;
    
    @GetMapping("/teste")
    public String teste() {
        return "OK - Categorias";
    }
    
    @GetMapping
    public ResponseEntity<List<CategoriaServico>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaServico> buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> criarCategoria(@RequestBody CategoriaServico categoria) {
        try {
            CategoriaServico novaCategoria = categoriaService.criarCategoria(categoria);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCategoria(@PathVariable Long id, @RequestBody CategoriaServico categoria) {
        try {
            CategoriaServico categoriaAtualizada = categoriaService.atualizarCategoria(id, categoria);
            return ResponseEntity.ok(categoriaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCategoria(@PathVariable Long id) {
        try {
            categoriaService.deletarCategoria(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<CategoriaServico>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(categoriaService.buscarPorNome(nome));
    }
}