package com.boxpro.controller;

import com.boxpro.dto.request.AlterarSenhaRequest;
import com.boxpro.dto.request.UsuarioCreateRequest;
import com.boxpro.dto.request.UsuarioUpdateRequest;
import com.boxpro.dto.response.MessageResponse;
import com.boxpro.dto.response.PageResponse;
import com.boxpro.dto.response.UsuarioResponse;
import com.boxpro.entity.enums.TipoUsuario;
import com.boxpro.service.UsuarioService;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
@SecurityRequirement(name = "bearer-jwt")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar usuários", description = "Lista todos os usuários com paginação")
    public ResponseEntity<PageResponse<UsuarioResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoUsuario tipo) {
        PageResponse<UsuarioResponse> usuarios = usuarioService.listar(pageable, nome, tipo);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.id")
    @Operation(summary = "Buscar usuário", description = "Busca usuário por ID")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Integer id) {
        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/me")
    @Operation(summary = "Meu perfil", description = "Retorna dados do usuário autenticado")
    public ResponseEntity<UsuarioResponse> meuPerfil(Principal principal) {
        UsuarioResponse usuario = usuarioService.buscarPorEmail(principal.getName());
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Criar usuário", description = "Cria novo usuário (admin)")
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse usuario = usuarioService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or #id == authentication.principal.id")
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados do usuário")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioUpdateRequest request,
            Principal principal) {
        // Se não for admin, não pode alterar o tipo de usuário
        if (!isAdmin(principal) && request.getTipoUsuario() != null) {
            request.setTipoUsuario(null);
        }
        UsuarioResponse usuario = usuarioService.atualizar(id, request);
        return ResponseEntity.ok(usuario);
    }

    @PatchMapping("/{id}/senha")
    @PreAuthorize("#id == authentication.principal.id")
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário")
    public ResponseEntity<MessageResponse> alterarSenha(
            @PathVariable Integer id,
            @Valid @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(id, request);
        return ResponseEntity.ok(MessageResponse.success("Senha alterada com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Deletar usuário", description = "Remove usuário do sistema")
    public ResponseEntity<MessageResponse> deletar(@PathVariable Integer id) {
        usuarioService.deletar(id);
        return ResponseEntity.ok(MessageResponse.success("Usuário deletado com sucesso"));
    }

    @GetMapping("/estatisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Estatísticas", description = "Retorna estatísticas dos usuários")
    public ResponseEntity<Object> estatisticas() {
        var stats = usuarioService.obterEstatisticas();
        return ResponseEntity.ok(stats);
    }

    private boolean isAdmin(Principal principal) {
        // Implementar verificação se é admin
        return false;
    }
}