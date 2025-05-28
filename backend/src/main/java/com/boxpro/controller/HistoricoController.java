package com.boxpro.controller;

import com.boxpro.dto.response.HistoricoAgendamentoResponse;
import com.boxpro.entity.enums.AcaoHistorico;
import com.boxpro.service.HistoricoAgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/historico")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Histórico", description = "Auditoria e histórico de ações")
@SecurityRequirement(name = "bearer-jwt")
public class HistoricoController {

    private final HistoricoAgendamentoService historicoService;

    @GetMapping
    @Operation(summary = "Listar histórico", description = "Lista todo histórico com filtros")
    public ResponseEntity<List<HistoricoAgendamentoResponse>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) AcaoHistorico acao,
            @RequestParam(required = false) Integer usuarioId) {
        List<HistoricoAgendamentoResponse> historico = historicoService.listar(dataInicio, dataFim, acao, usuarioId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/agendamento/{agendamentoId}")
    @Operation(summary = "Por agendamento", description = "Busca histórico de um agendamento")
    public ResponseEntity<List<HistoricoAgendamentoResponse>> porAgendamento(
            @PathVariable Integer agendamentoId) {
        List<HistoricoAgendamentoResponse> historico = historicoService.buscarPorAgendamento(agendamentoId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Por usuário", description = "Busca ações realizadas por um usuário")
    public ResponseEntity<List<HistoricoAgendamentoResponse>> porUsuario(
            @PathVariable Integer usuarioId) {
        List<HistoricoAgendamentoResponse> historico = historicoService.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/acao/{acao}")
    @Operation(summary = "Por tipo de ação", description = "Busca histórico por tipo de ação")
    public ResponseEntity<List<HistoricoAgendamentoResponse>> porAcao(
            @PathVariable AcaoHistorico acao) {
        List<HistoricoAgendamentoResponse> historico = historicoService.buscarPorAcao(acao);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/cancelamentos")
    @Operation(summary = "Cancelamentos", description = "Lista histórico de cancelamentos")
    public ResponseEntity<List<HistoricoAgendamentoResponse>> cancelamentos() {
        List<HistoricoAgendamentoResponse> historico = historicoService.buscarCancelamentos();
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/auditoria/hoje")
    @Operation(summary = "Auditoria hoje", description = "Lista todas as ações do dia")
    public ResponseEntity<List<HistoricoAgendamentoResponse>> auditoriaHoje() {
        List<HistoricoAgendamentoResponse> historico = historicoService.buscarAcoesHoje();
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/estatisticas")
    @Operation(summary = "Estatísticas", description = "Retorna estatísticas do histórico")
    public ResponseEntity<Object> estatisticas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        var stats = historicoService.obterEstatisticas(dataInicio, dataFim);
        return ResponseEntity.ok(stats);
    }
}