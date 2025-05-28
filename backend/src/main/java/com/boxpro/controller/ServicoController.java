package com.boxpro.controller;

import com.boxpro.dto.request.ServicoRequest;
import com.boxpro.dto.response.MessageResponse;
import com.boxpro.dto.response.PageResponse;
import com.boxpro.dto.response.ServicoResponse;
import com.boxpro.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@Tag(name = "Serviços", description = "Gerenciamento de serviços")
@SecurityRequirement(name = "bearer-jwt")
public class ServicoController {

    private final ServicoService servicoService;

    @GetMapping
    @Operation(summary = "Listar serviços", description = "Lista serviços com filtros")
    public ResponseEntity<PageResponse<ServicoResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) BigDecimal precoMin,
            @RequestParam(required = false) BigDecimal precoMax) {
        PageResponse<ServicoResponse> servicos = servicoService.listar(
                pageable, nome, categoriaId, ativo, precoMin, precoMax);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar ativos", description = "Lista apenas serviços ativos")
    public ResponseEntity<List<ServicoResponse>> listarAtivos() {
        List<ServicoResponse> servicos = servicoService.listarAtivos();
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço", description = "Busca serviço por ID")
    public ResponseEntity<ServicoResponse> buscarPorId(@PathVariable Integer id) {
        ServicoResponse servico = servicoService.buscarPorId(id);
        return ResponseEntity.ok(servico);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Por categoria", description = "Lista serviços de uma categoria")
    public ResponseEntity<List<ServicoResponse>> listarPorCategoria(
            @PathVariable Integer categoriaId) {
        List<ServicoResponse> servicos = servicoService.listarPorCategoria(categoriaId);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/mais-utilizados")
    @Operation(summary = "Mais utilizados", description = "Lista serviços mais agendados")
    public ResponseEntity<List<ServicoResponse>> listarMaisUtilizados(
            @RequestParam(defaultValue = "10") Integer limite) {
        List<ServicoResponse> servicos = servicoService.listarMaisUtilizados(limite);
        return ResponseEntity.ok(servicos);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Criar serviço", description = "Cria novo serviço")
    public ResponseEntity<ServicoResponse> criar(@Valid @RequestBody ServicoRequest request) {
        ServicoResponse servico = servicoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(servico);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualizar serviço", description = "Atualiza dados do serviço")
    public ResponseEntity<ServicoResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ServicoRequest request) {
        ServicoResponse servico = servicoService.atualizar(id, request);
        return ResponseEntity.ok(servico);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Alterar status", description = "Ativa ou desativa serviço")
    public ResponseEntity<ServicoResponse> alterarStatus(
            @PathVariable Integer id,
            @RequestParam Boolean ativo) {
        ServicoResponse servico = servicoService.alterarStatus(id, ativo);
        return ResponseEntity.ok(servico);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Deletar serviço", description = "Remove serviço do sistema")
    public ResponseEntity<MessageResponse> deletar(@PathVariable Integer id) {
        servicoService.deletar(id);
        return ResponseEntity.ok(MessageResponse.success("Serviço deletado com sucesso"));
    }
}