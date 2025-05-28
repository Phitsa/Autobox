package com.boxpro.controller;

import com.boxpro.dto.request.AgendamentoCreateRequest;
import com.boxpro.dto.request.AgendamentoUpdateRequest;
import com.boxpro.dto.request.CancelarAgendamentoRequest;
import com.boxpro.dto.request.RemarcarAgendamentoRequest;
import com.boxpro.dto.response.AgendamentoResponse;
import com.boxpro.dto.response.HistoricoAgendamentoResponse;
import com.boxpro.dto.response.MessageResponse;
import com.boxpro.dto.response.PageResponse;
import com.boxpro.entity.enums.StatusAgendamento;
import com.boxpro.service.AgendamentoService;
import com.boxpro.service.HistoricoAgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@Tag(name = "Agendamentos", description = "Gerenciamento de agendamentos")
@SecurityRequirement(name = "bearer-jwt")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final HistoricoAgendamentoService historicoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar todos", description = "Lista todos agendamentos com filtros")
    public ResponseEntity<PageResponse<AgendamentoResponse>> listar(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) StatusAgendamento status,
            @RequestParam(required = false) Integer usuarioId,
            @RequestParam(required = false) Integer servicoId) {
        PageResponse<AgendamentoResponse> agendamentos = agendamentoService.listar(
                pageable, dataInicio, dataFim, status, usuarioId, servicoId);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/meus")
    @Operation(summary = "Meus agendamentos", description = "Lista agendamentos do usuário autenticado")
    public ResponseEntity<List<AgendamentoResponse>> meusAgendamentos(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) StatusAgendamento status) {
        List<AgendamentoResponse> agendamentos = agendamentoService.listarPorUsuario(
                userDetails.getUsername(), status);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/hoje")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Agendamentos hoje", description = "Lista agendamentos do dia")
    public ResponseEntity<List<AgendamentoResponse>> agendamentosHoje() {
        List<AgendamentoResponse> agendamentos = agendamentoService.listarHoje();
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento", description = "Busca agendamento por ID")
    public ResponseEntity<AgendamentoResponse> buscarPorId(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        AgendamentoResponse agendamento = agendamentoService.buscarPorId(id, userDetails.getUsername());
        return ResponseEntity.ok(agendamento);
    }

    @GetMapping("/disponibilidade")
    @Operation(summary = "Verificar disponibilidade", description = "Verifica horários disponíveis")
    public ResponseEntity<List<String>> verificarDisponibilidade(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam Integer servicoId) {
        List<String> horariosDisponiveis = agendamentoService.verificarDisponibilidade(data, servicoId);
        return ResponseEntity.ok(horariosDisponiveis);
    }

    @PostMapping
    @Operation(summary = "Criar agendamento", description = "Cria novo agendamento")
    public ResponseEntity<AgendamentoResponse> criar(
            @Valid @RequestBody AgendamentoCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AgendamentoResponse agendamento = agendamentoService.criar(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamento);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar observações", description = "Atualiza observações do agendamento")
    public ResponseEntity<AgendamentoResponse> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody AgendamentoUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AgendamentoResponse agendamento = agendamentoService.atualizar(id, request, userDetails.getUsername());
        return ResponseEntity.ok(agendamento);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Alterar status", description = "Altera status do agendamento")
    public ResponseEntity<AgendamentoResponse> alterarStatus(
            @PathVariable Integer id,
            @RequestParam StatusAgendamento status,
            @AuthenticationPrincipal UserDetails userDetails) {
        AgendamentoResponse agendamento = agendamentoService.alterarStatus(id, status, userDetails.getUsername());
        return ResponseEntity.ok(agendamento);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar agendamento", description = "Cancela um agendamento")
    public ResponseEntity<AgendamentoResponse> cancelar(
            @PathVariable Integer id,
            @Valid @RequestBody CancelarAgendamentoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AgendamentoResponse agendamento = agendamentoService.cancelar(id, request, userDetails.getUsername());
        return ResponseEntity.ok(agendamento);
    }

    @PostMapping("/{id}/remarcar")
    @Operation(summary = "Remarcar agendamento", description = "Remarca um agendamento")
    public ResponseEntity<AgendamentoResponse> remarcar(
            @PathVariable Integer id,
            @Valid @RequestBody RemarcarAgendamentoRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AgendamentoResponse agendamento = agendamentoService.remarcar(id, request, userDetails.getUsername());
        return ResponseEntity.ok(agendamento);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Deletar agendamento", description = "Remove agendamento do sistema")
    public ResponseEntity<MessageResponse> deletar(@PathVariable Integer id) {
        agendamentoService.deletar(id);
        return ResponseEntity.ok(MessageResponse.success("Agendamento deletado com sucesso"));
    }

    @GetMapping("/{id}/historico")
    @Operation(summary = "Histórico", description = "Busca histórico de ações do agendamento")
    public ResponseEntity<List<HistoricoAgendamentoResponse>> buscarHistorico(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<HistoricoAgendamentoResponse> historico = historicoService.buscarPorAgendamento(id, userDetails.getUsername());
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/proximos")
    @Operation(summary = "Próximos agendamentos", description = "Lista próximos agendamentos do usuário")
    public ResponseEntity<List<AgendamentoResponse>> proximosAgendamentos(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "5") Integer limite) {
        List<AgendamentoResponse> agendamentos = agendamentoService.listarProximos(userDetails.getUsername(), limite);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/periodo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Por período", description = "Lista agendamentos em um período específico")
    public ResponseEntity<List<AgendamentoResponse>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<AgendamentoResponse> agendamentos = agendamentoService.listarPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(agendamentos);
    }
}