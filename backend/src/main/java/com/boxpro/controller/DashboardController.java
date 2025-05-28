package com.boxpro.controller;

import com.boxpro.dto.response.DashboardResponse;
import com.boxpro.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Dashboard", description = "Estatísticas e relatórios do sistema")
@SecurityRequirement(name = "bearer-jwt")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Dashboard completo", description = "Retorna todas as estatísticas do dashboard")
    public ResponseEntity<DashboardResponse> obterDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        DashboardResponse dashboard = dashboardService.obterDashboard(dataInicio, dataFim);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/resumo-geral")
    @Operation(summary = "Resumo geral", description = "Retorna resumo geral do sistema")
    public ResponseEntity<DashboardResponse.ResumoGeral> obterResumoGeral() {
        DashboardResponse.ResumoGeral resumo = dashboardService.obterResumoGeral();
        return ResponseEntity.ok(resumo);
    }

    @GetMapping("/agendamentos-por-dia")
    @Operation(summary = "Agendamentos por dia", description = "Retorna agendamentos agrupados por dia")
    public ResponseEntity<Map<LocalDate, Long>> agendamentosPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Map<LocalDate, Long> agendamentos = dashboardService.agendamentosPorDia(dataInicio, dataFim);
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/servicos-populares")
    @Operation(summary = "Serviços populares", description = "Retorna serviços mais agendados")
    public ResponseEntity<Object> servicosPopulares(
            @RequestParam(defaultValue = "10") Integer limite) {
        var servicos = dashboardService.servicosMaisPopulares(limite);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/clientes-frequentes")
    @Operation(summary = "Clientes frequentes", description = "Retorna clientes mais frequentes")
    public ResponseEntity<Object> clientesFrequentes(
            @RequestParam(defaultValue = "10") Integer limite) {
        var clientes = dashboardService.clientesMaisFrequentes(limite);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/receita-periodo")
    @Operation(summary = "Receita por período", description = "Retorna receita em um período")
    public ResponseEntity<Map<String, Object>> receitaPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Map<String, Object> receita = dashboardService.receitaPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(receita);
    }

    @GetMapping("/taxa-ocupacao")
    @Operation(summary = "Taxa de ocupação", description = "Retorna taxa de ocupação dos horários")
    public ResponseEntity<Map<String, Object>> taxaOcupacao(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        Map<String, Object> taxa = dashboardService.taxaOcupacao(data);
        return ResponseEntity.ok(taxa);
    }

    @GetMapping("/cancelamentos")
    @Operation(summary = "Estatísticas de cancelamentos", description = "Retorna dados sobre cancelamentos")
    public ResponseEntity<Map<String, Object>> estatisticasCancelamentos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Map<String, Object> cancelamentos = dashboardService.estatisticasCancelamentos(dataInicio, dataFim);
        return ResponseEntity.ok(cancelamentos);
    }

    @GetMapping("/horarios-pico")
    @Operation(summary = "Horários de pico", description = "Retorna horários mais movimentados")
    public ResponseEntity<Map<String, Object>> horariosPico() {
        Map<String, Object> horarios = dashboardService.horariosDePico();
        return ResponseEntity.ok(horarios);
    }

    @GetMapping("/relatorio-mensal")
    @Operation(summary = "Relatório mensal", description = "Gera relatório completo do mês")
    public ResponseEntity<Map<String, Object>> relatorioMensal(
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        Map<String, Object> relatorio = dashboardService.relatorioMensal(mes, ano);
        return ResponseEntity.ok(relatorio);
    }
}