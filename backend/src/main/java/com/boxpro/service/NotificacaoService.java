package com.boxpro.service;

import com.boxpro.entity.Agendamento;
import com.boxpro.entity.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço de notificações
 * Em produção, pode integrar com WhatsApp, SMS ou push notifications
 * Por enquanto, apenas loga as notificações
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {

    private final AgendamentoService agendamentoService;
    
    @Value("${notificacao.lembrete.horas:24}")
    private Integer horasAntecedenciaLembrete;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    // Lista para simular armazenamento de notificações
    private final List<Map<String, Object>> notificacoesEnviadas = new ArrayList<>();

    /**
     * Registrar notificação de confirmação de agendamento
     */
    @Async
    public void notificarConfirmacaoAgendamento(Agendamento agendamento) {
        log.info("Notificação: Confirmação de agendamento ID: {}", agendamento.getId());
        
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("tipo", "CONFIRMACAO_AGENDAMENTO");
        notificacao.put("destinatario", agendamento.getUsuario().getNome());
        notificacao.put("telefone", agendamento.getUsuario().getTelefone());
        notificacao.put("mensagem", String.format(
            "Agendamento confirmado para %s às %s. Serviço: %s. Veículo: %s",
            agendamento.getDataAgendamento().format(DATE_FORMAT),
            agendamento.getHoraInicio().format(TIME_FORMAT),
            agendamento.getServico().getNome(),
            agendamento.getVeiculo().getPlaca()
        ));
        notificacao.put("dataEnvio", LocalDateTime.now());
        
        notificacoesEnviadas.add(notificacao);
        log.info("Notificação registrada: {}", notificacao);
    }

    /**
     * Registrar notificação de cancelamento
     */
    @Async
    public void notificarCancelamento(Agendamento agendamento) {
        log.info("Notificação: Cancelamento de agendamento ID: {}", agendamento.getId());
        
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("tipo", "CANCELAMENTO_AGENDAMENTO");
        notificacao.put("destinatario", agendamento.getUsuario().getNome());
        notificacao.put("telefone", agendamento.getUsuario().getTelefone());
        notificacao.put("mensagem", String.format(
            "Agendamento cancelado. Data: %s. Motivo: %s. Taxa: R$ %s",
            agendamento.getDataAgendamento().format(DATE_FORMAT),
            agendamento.getMotivoCancelamento(),
            agendamento.getTaxaCancelamento() != null ? agendamento.getTaxaCancelamento() : "0,00"
        ));
        notificacao.put("dataEnvio", LocalDateTime.now());
        
        notificacoesEnviadas.add(notificacao);
        log.info("Notificação registrada: {}", notificacao);
    }

    /**
     * Registrar notificação de remarcação
     */
    @Async
    public void notificarRemarcacao(Agendamento agendamento, 
                                   LocalDate dataAnterior, 
                                   LocalDateTime horaAnterior) {
        log.info("Notificação: Remarcação de agendamento ID: {}", agendamento.getId());
        
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("tipo", "REMARCACAO_AGENDAMENTO");
        notificacao.put("destinatario", agendamento.getUsuario().getNome());
        notificacao.put("telefone", agendamento.getUsuario().getTelefone());
        notificacao.put("mensagem", String.format(
            "Agendamento remarcado de %s para %s às %s",
            dataAnterior.format(DATE_FORMAT),
            agendamento.getDataAgendamento().format(DATE_FORMAT),
            agendamento.getHoraInicio().format(TIME_FORMAT)
        ));
        notificacao.put("dataEnvio", LocalDateTime.now());
        
        notificacoesEnviadas.add(notificacao);
        log.info("Notificação registrada: {}", notificacao);
    }

    /**
     * Processar lembretes de agendamentos (executado via scheduler)
     */
    @Scheduled(cron = "0 0 9 * * *") // Executa todos os dias às 9h
    public void processarLembretesAgendamentos() {
        log.info("Processando lembretes de agendamentos");
        
        LocalDateTime dataHoraLimite = LocalDateTime.now().plusHours(horasAntecedenciaLembrete);
        
        List<Agendamento> agendamentosProximos = agendamentoService
                .buscarAgendamentosParaLembrete(dataHoraLimite);
        
        for (Agendamento agendamento : agendamentosProximos) {
            registrarLembreteAgendamento(agendamento);
        }
        
        log.info("Processados {} lembretes", agendamentosProximos.size());
    }

    /**
     * Registrar lembrete individual
     */
    @Async
    public void registrarLembreteAgendamento(Agendamento agendamento) {
        log.info("Lembrete para agendamento ID: {}", agendamento.getId());
        
        Map<String, Object> notificacao = new HashMap<>();
        notificacao.put("tipo", "LEMBRETE_AGENDAMENTO");
        notificacao.put("destinatario", agendamento.getUsuario().getNome());
        notificacao.put("telefone", agendamento.getUsuario().getTelefone());
        notificacao.put("mensagem", String.format(
            "Lembrete: Você tem um agendamento amanhã às %s. Serviço: %s",
            agendamento.getHoraInicio().format(TIME_FORMAT),
            agendamento.getServico().getNome()
        ));
        notificacao.put("dataEnvio", LocalDateTime.now());
        
        notificacoesEnviadas.add(notificacao);
        log.info("Lembrete registrado: {}", notificacao);
    }

    /**
     * Registrar alerta para administradores sobre novo agendamento
     */
    @Async
    public void alertarNovoAgendamento(Agendamento agendamento) {
        log.info("Alerta admin: Novo agendamento ID: {}", agendamento.getId());
        
        Map<String, Object> alerta = new HashMap<>();
        alerta.put("tipo", "NOVO_AGENDAMENTO");
        alerta.put("cliente", agendamento.getUsuario().getNome());
        alerta.put("data", agendamento.getDataAgendamento().format(DATE_FORMAT));
        alerta.put("horario", agendamento.getHoraInicio().format(TIME_FORMAT));
        alerta.put("servico", agendamento.getServico().getNome());
        alerta.put("valor", "R$ " + agendamento.getValorTotal());
        
        log.info("Alerta para administradores: {}", alerta);
    }

    /**
     * Gerar resumo diário (executado via scheduler)
     */
    @Scheduled(cron = "0 0 7 * * *") // Executa todos os dias às 7h
    public void gerarResumoDiario() {
        log.info("Gerando resumo diário");
        
        LocalDate hoje = LocalDate.now();
        List<Agendamento> agendamentosHoje = agendamentoService.listarPorData(hoje);
        
        log.info("=== RESUMO DO DIA {} ===", hoje.format(DATE_FORMAT));
        log.info("Total de agendamentos: {}", agendamentosHoje.size());
        
        for (Agendamento agendamento : agendamentosHoje) {
            log.info("- {} | {} | {} | {}",
                agendamento.getHoraInicio().format(TIME_FORMAT),
                agendamento.getUsuario().getNome(),
                agendamento.getServico().getNome(),
                agendamento.getVeiculo().getPlaca()
            );
        }
        log.info("========================");
    }

    /**
     * Verificar agendamentos não comparecidos
     */
    @Scheduled(cron = "0 0 19 * * *") // Executa todos os dias às 19h
    public void verificarNaoComparecimentos() {
        log.info("Verificando agendamentos não comparecidos");
        
        LocalDate hoje = LocalDate.now();
        List<Agendamento> agendamentosHoje = agendamentoService
                .buscarAgendamentosNaoFinalizados(hoje);
        
        int naoComparecidos = 0;
        for (Agendamento agendamento : agendamentosHoje) {
            if (LocalDateTime.now().isAfter(
                    LocalDateTime.of(agendamento.getDataAgendamento(), 
                                   agendamento.getHoraFim()))) {
                log.warn("Cliente não compareceu: {} - Agendamento ID: {}", 
                    agendamento.getUsuario().getNome(), 
                    agendamento.getId()
                );
                naoComparecidos++;
            }
        }
        
        if (naoComparecidos > 0) {
            log.warn("Total de não comparecimentos hoje: {}", naoComparecidos);
        }
    }

    /**
     * Obter histórico de notificações (para debug/admin)
     */
    public List<Map<String, Object>> obterHistoricoNotificacoes() {
        return new ArrayList<>(notificacoesEnviadas);
    }

    /**
     * Limpar histórico de notificações antigas
     */
    @Scheduled(cron = "0 0 0 * * *") // Executa todo dia à meia-noite
    public void limparNotificacoesAntigas() {
        LocalDateTime limite = LocalDateTime.now().minusDays(7);
        
        notificacoesEnviadas.removeIf(notificacao -> {
            LocalDateTime dataEnvio = (LocalDateTime) notificacao.get("dataEnvio");
            return dataEnvio.isBefore(limite);
        });
        
        log.info("Notificações antigas removidas. Total atual: {}", notificacoesEnviadas.size());
    }
}