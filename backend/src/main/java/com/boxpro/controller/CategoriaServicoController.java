package com.boxpro.controller;

import com.boxpro.dto.request.CategoriaServicoRequest;
import com.boxpro.dto.response.CategoriaServicoResponse;
import com.boxpro.dto.response.MessageResponse;
import com.boxpro.service.CategoriaServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias de Serviço", description = "Gerenciamento de categorias")
@SecurityRequirement(name = "bearer-jwt")
public class CategoriaServicoController {

    private final CategoriaServicoService categoriaServicoService;

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Lista todas as categorias de serviço")
    public ResponseEntity<List<CategoriaServicoResponse>> listar(
            @RequestParam(required = false) String nome) {
        List<CategoriaServicoResponse> categorias = categoriaServicoService.listar(nome);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria", description = "Busca categoria por ID")
    public ResponseEntity<CategoriaServicoResponse> buscarPorId(@PathVariable Integer id) {
        CategoriaServicoResponse categoria = categoriaServicoService.buscarPorId(id);
        return ResponseEntity.ok(categoria);
    }

    @GetMapping("/com-servicos")
    @Operation(summary = "Listar com serviços", description = "Lista categorias com seus serviços")
    public ResponseEntity<List<CategoriaServicoResponse>> listarComServicos() {
        List<CategoriaServicoResponse> categorias = categoriaServicoService.listarComServicos();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Criar categoria", description = "Cria nova categoria de serviço")
    public ResponseEntity<CategoriaServicoResponse> criar(
            @Valid @RequestBody CategoriaServicoRequest request) {
        CategoriaServicoResponse categoria = categoriaServicoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualizar categoria", description = "Atualiza dados da categoria")
    public ResponseEntity<CategoriaServicoResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaServicoRequest request) {
        CategoriaServicoResponse categoria = categoriaServicoService.atualizar(id, request);
        return ResponseEntity.ok(categoria);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Deletar categoria", description = "Remove categoria do sistema")
    public ResponseEntity<MessageResponse> deletar(@PathVariable Integer id) {
        categoriaServicoService.deletar(id);
        return ResponseEntity.ok(MessageResponse.success("Categoria deletada com sucesso"));
    }
}