package com.boxpro.controller;

import com.boxpro.dto.request.VeiculoRequest;
import com.boxpro.dto.response.MessageResponse;
import com.boxpro.dto.response.VeiculoResponse;
import com.boxpro.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
@Tag(name = "Veículos", description = "Gerenciamento de veículos")
@SecurityRequirement(name = "bearer-jwt")
public class VeiculoController {

    private final VeiculoService veiculoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar todos", description = "Lista todos os veículos (admin)")
    public ResponseEntity<List<VeiculoResponse>> listarTodos() {
        List<VeiculoResponse> veiculos = veiculoService.listarTodos();
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/meus")
    @Operation(summary = "Meus veículos", description = "Lista veículos do usuário autenticado")
    public ResponseEntity<List<VeiculoResponse>> meusVeiculos(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<VeiculoResponse> veiculos = veiculoService.listarPorUsuario(userDetails.getUsername());
        return ResponseEntity.ok(veiculos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo", description = "Busca veículo por ID")
    public ResponseEntity<VeiculoResponse> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        VeiculoResponse veiculo = veiculoService.buscarPorId(id, userDetails.getUsername());
        return ResponseEntity.ok(veiculo);
    }

    @GetMapping("/placa/{placa}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Buscar por placa", description = "Busca veículo pela placa")
    public ResponseEntity<VeiculoResponse> buscarPorPlaca(@PathVariable String placa) {
        VeiculoResponse veiculo = veiculoService.buscarPorPlaca(placa);
        return ResponseEntity.ok(veiculo);
    }

    @GetMapping("/marcas")
    @Operation(summary = "Listar marcas", description = "Lista todas as marcas cadastradas")
    public ResponseEntity<List<String>> listarMarcas() {
        List<String> marcas = veiculoService.listarMarcasDistintas();
        return ResponseEntity.ok(marcas);
    }

    @PostMapping
    @Operation(summary = "Cadastrar veículo", description = "Cadastra novo veículo")
    public ResponseEntity<VeiculoResponse> criar(
            @Valid @RequestBody VeiculoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        VeiculoResponse veiculo = veiculoService.criar(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veículo", description = "Atualiza dados do veículo")
    public ResponseEntity<VeiculoResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody VeiculoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        VeiculoResponse veiculo = veiculoService.atualizar(id, request, userDetails.getUsername());
        return ResponseEntity.ok(veiculo);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar veículo", description = "Remove veículo do sistema")
    public ResponseEntity<MessageResponse> deletar(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        veiculoService.deletar(id, userDetails.getUsername());
        return ResponseEntity.ok(MessageResponse.success("Veículo deletado com sucesso"));
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Por usuário", description = "Lista veículos de um usuário específico")
    public ResponseEntity<List<VeiculoResponse>> listarPorUsuario(@PathVariable Integer usuarioId) {
        List<VeiculoResponse> veiculos = veiculoService.listarPorUsuarioId(usuarioId);
        return ResponseEntity.ok(veiculos);
    }
}