package com.boxpro.service;

import com.boxpro.entity.Agendamento;
import com.boxpro.entity.HistoricoAgendamento;
import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.AcaoHistorico;
import com.boxpro.repository.HistoricoAgendamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HistoricoAgendamentoService {

    private final HistoricoAgendamentoRepository historicoAgendamentoRepository;

    /**
     * Registrar ação no histórico
     */
    public HistoricoAgendamento registrarAcao(Agendamento agendamento, Usuario usuario, 
                                              AcaoHistorico acao, Map<String, Object> detalhes) {
        log.info("Registrando ação {} para agendamento ID: {}", acao, agendamento.getId());
        
        HistoricoAgendamento historico = new HistoricoAgendamento();
        historico.setAgendamento(agendamento);
        historico.setUsuario(usuario);
        historico.setAcao(acao);
        historico.setDetalhes(detalhes);
        
        return historicoAgendamentoRepository.save(historico);
    }

    /**
     * Buscar histórico por agendamento
     */
    @Transactional(readOnly = true)
    public List<HistoricoAgendamento> buscarPorAgendamento(Integer agendamentoId) {
        return historicoAgendamentoRepository.findByAgendamentoIdOrderByDataAcaoDesc(agendamentoId);
    }

    /**
     * Buscar histórico por usuário
     */
    @Transactional(readOnly = true)
    public List<HistoricoAgendamento> buscarPorUsuario(Integer usuarioId) {
        return historicoAgendamentoRepository.findByUsuarioIdOrderByDataAcaoDesc(usuarioId);
    }

    /**
     * Buscar histórico por tipo de ação
     */
    @Transactional(readOnly = true)
    public List<HistoricoAgendamento> buscarPorAcao(AcaoHistorico acao) {
        return historicoAgendamentoRepository.findByAcaoOrderByDataAcaoDesc(acao);
    }

    /**
     * Buscar histórico por período
     */
    @Transactional(readOnly = true)
    public List<HistoricoAgendamento> buscarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return historicoAgendamentoRepository.findByDataAcaoBetweenOrderByDataAcaoDesc(dataInicio, dataFim);
    }

    /**
     * Buscar última ação de um agendamento
     */
    @Transactional(readOnly = true)
    public HistoricoAgendamento buscarUltimaAcao(Integer agendamentoId) {
        return historicoAgendamentoRepository.findTopByAgendamentoIdOrderByDataAcaoDesc(agendamentoId)
                .orElse(null);
    }

    /**
     * Buscar histórico de cancelamentos
     */
    @Transactional(readOnly = true)
    public List<HistoricoAgendamento> buscarCancelamentos() {
        return historicoAgendamentoRepository.findHistoricoCancelamentos();
    }

    /**
     * Buscar ações do usuário no dia
     */
    @Transactional(readOnly = true)
    public List<HistoricoAgendamento> buscarAcoesUsuarioHoje(Integer usuarioId) {
        return historicoAgendamentoRepository.findAcoesPorUsuarioNoDia(usuarioId, LocalDate.now());
    }

    /**
     * Contar ações por tipo no período
     */
    @Transactional(readOnly = true)
    public List<Object[]> contarAcoesPorTipo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return historicoAgendamentoRepository.countAcoesPorTipoNoPeriodo(dataInicio, dataFim);
    }

    /**
     * Registrar criação de agendamento
     */
    public void registrarCriacao(Agendamento agendamento, Usuario usuario) {
        Map<String, Object> detalhes = Map.of(
            "servico", agendamento.getServico().getNome(),
            "data", agendamento.getDataAgendamento(),
            "horario", agendamento.getHoraInicio(),
            "valor", agendamento.getValorTotal()
        );
        
        registrarAcao(agendamento, usuario, AcaoHistorico.CRIADO, detalhes);
    }

    /**
     * Registrar edição de agendamento
     */
    public void registrarEdicao(Agendamento agendamento, Usuario usuario, String campoAlterado, 
                               Object valorAnterior, Object valorNovo) {
        Map<String, Object> detalhes = Map.of(
            "campo", campoAlterado,
            "valorAnterior", valorAnterior,
            "valorNovo", valorNovo
        );
        
        registrarAcao(agendamento, usuario, AcaoHistorico.EDITADO, detalhes);
    }

    /**
     * Registrar cancelamento
     */
    public void registrarCancelamento(Agendamento agendamento, Usuario usuario, String motivo, BigDecimal taxa) {
        Map<String, Object> detalhes = Map.of(
            "motivo", motivo,
            "taxaCancelamento", taxa,
            "dataCancelamento", LocalDateTime.now()
        );
        
        registrarAcao(agendamento, usuario, AcaoHistorico.CANCELADO, detalhes);
    }

    /**
     * Registrar remarcação
     */
    public void registrarRemarcacao(Agendamento agendamento, Usuario usuario,
                                   LocalDate dataAnterior, LocalTime horarioAnterior,
                                   LocalDate novaData, LocalTime novoHorario) {
        Map<String, Object> detalhes = Map.of(
            "dataAnterior", dataAnterior,
            "horarioAnterior", horarioAnterior,
            "novaData", novaData,
            "novoHorario", novoHorario
        );
        
        registrarAcao(agendamento, usuario, AcaoHistorico.REMARCADO, detalhes);
    }

    /**
     * Registrar finalização
     */
    public void registrarFinalizacao(Agendamento agendamento, Usuario usuario) {
        Map<String, Object> detalhes = Map.of(
            "dataFinalizacao", LocalDateTime.now(),
            "duracao", agendamento.getServico().getDuracaoEstimada(),
            "valorFinal", agendamento.getValorTotal()
        );
        
        registrarAcao(agendamento, usuario, AcaoHistorico.FINALIZADO, detalhes);
    }
}