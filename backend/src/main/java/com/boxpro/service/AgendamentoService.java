package com.boxpro.service;

import com.boxpro.entity.*;
import com.boxpro.entity.enums.AcaoHistorico;
import com.boxpro.entity.enums.StatusAgendamento;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioService usuarioService;
    private final VeiculoService veiculoService;
    private final ServicoService servicoService;
    private final HistoricoAgendamentoService historicoAgendamentoService;

    @Value("${agendamento.horario.inicio:08:00}")
    private String horarioInicio;

    @Value("${agendamento.horario.fim:18:00}")
    private String horarioFim;

    @Value("${agendamento.cancelamento.horas:24}")
    private Integer horasAntecedenciaCancelamento;

    @Value("${agendamento.cancelamento.taxa:0.2}")
    private BigDecimal taxaCancelamento;

    /**
     * Criar novo agendamento
     */
    public Agendamento criar(Agendamento agendamento, Integer usuarioId) {
        log.info("Criando novo agendamento para usuário ID: {}", usuarioId);
        
        // Buscar e validar entidades
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        Veiculo veiculo = veiculoService.buscarPorId(agendamento.getVeiculo().getId());
        Servico servico = servicoService.buscarPorId(agendamento.getServico().getId());
        
        // Verificar se veículo pertence ao usuário
        if (!veiculoService.usuarioEhDono(veiculo.getId(), usuarioId)) {
            throw new BusinessException("Veículo não pertence ao usuário");
        }
        
        // Verificar se serviço está ativo
        if (!servico.getAtivo()) {
            throw new BusinessException("Serviço não está disponível");
        }
        
        // Validar data e horário
        validarDataHorario(agendamento.getDataAgendamento(), agendamento.getHoraInicio());
        
        // Calcular hora fim baseado na duração do serviço
        LocalTime horaFim = agendamento.getHoraInicio().plusMinutes(servico.getDuracaoEstimada());
        agendamento.setHoraFim(horaFim);
        
        // Verificar disponibilidade
        verificarDisponibilidade(agendamento.getDataAgendamento(), agendamento.getHoraInicio(), horaFim);
        
        // Configurar agendamento
        agendamento.setUsuario(usuario);
        agendamento.setVeiculo(veiculo);
        agendamento.setServico(servico);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setValorTotal(servico.getPreco());
        
        // Salvar agendamento
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
        
        // Registrar no histórico
        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("servico", servico.getNome());
        detalhes.put("valor", servico.getPreco());
        detalhes.put("veiculo", veiculo.getModelo() + " - " + veiculo.getPlaca());
        
        historicoAgendamentoService.registrarAcao(
            agendamentoSalvo, usuario, AcaoHistorico.CRIADO, detalhes
        );
        
        return agendamentoSalvo;
    }

    /**
     * Buscar agendamento por ID
     */
    @Transactional(readOnly = true)
    public Agendamento buscarPorId(Integer id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado com ID: " + id));
    }

    /**
     * Listar agendamentos por usuário
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorUsuario(Integer usuarioId) {
        return agendamentoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Listar agendamentos por data
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorData(LocalDate data) {
        return agendamentoRepository.findByDataAgendamento(data);
    }

    /**
     * Listar agendamentos do dia
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarAgendamentosHoje() {
        return agendamentoRepository.findAgendamentosHoje();
    }

    /**
     * Listar agendamentos por período
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return agendamentoRepository.findByDataAgendamentoBetween(dataInicio, dataFim);
    }

    /**
     * Cancelar agendamento
     */
    public Agendamento cancelar(Integer id, Integer usuarioId, String motivo) {
        log.info("Cancelando agendamento ID: {}", id);
        
        Agendamento agendamento = buscarPorId(id);
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        
        // Verificar se pode cancelar
        if (!StatusAgendamento.AGENDADO.equals(agendamento.getStatus())) {
            throw new BusinessException("Apenas agendamentos com status AGENDADO podem ser cancelados");
        }
        
        // Verificar se é o dono ou admin
        if (!agendamento.getUsuario().getId().equals(usuarioId) && !usuarioService.isAdministrador(usuarioId)) {
            throw new BusinessException("Sem permissão para cancelar este agendamento");
        }
        
        // Calcular taxa de cancelamento
        BigDecimal taxa = calcularTaxaCancelamento(agendamento);
        
        // Atualizar agendamento
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamento.setDataCancelamento(LocalDateTime.now());
        agendamento.setMotivoCancelamento(motivo);
        agendamento.setTaxaCancelamento(taxa);
        
        Agendamento agendamentoCancelado = agendamentoRepository.save(agendamento);
        
        // Registrar no histórico
        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("motivo", motivo);
        detalhes.put("taxa", taxa);
        
        historicoAgendamentoService.registrarAcao(
            agendamentoCancelado, usuario, AcaoHistorico.CANCELADO, detalhes
        );
        
        return agendamentoCancelado;
    }

    /**
     * Remarcar agendamento
     */
    public Agendamento remarcar(Integer id, Integer usuarioId, LocalDate novaData, LocalTime novoHorario) {
        log.info("Remarcando agendamento ID: {}", id);
        
        Agendamento agendamento = buscarPorId(id);
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        
        // Verificar se pode remarcar
        if (!StatusAgendamento.AGENDADO.equals(agendamento.getStatus())) {
            throw new BusinessException("Apenas agendamentos com status AGENDADO podem ser remarcados");
        }
        
        // Verificar se é o dono ou admin
        if (!agendamento.getUsuario().getId().equals(usuarioId) && !usuarioService.isAdministrador(usuarioId)) {
            throw new BusinessException("Sem permissão para remarcar este agendamento");
        }
        
        // Validar nova data e horário
        validarDataHorario(novaData, novoHorario);
        
        // Calcular nova hora fim
        LocalTime novaHoraFim = novoHorario.plusMinutes(agendamento.getServico().getDuracaoEstimada());
        
        // Verificar disponibilidade
        verificarDisponibilidade(novaData, novoHorario, novaHoraFim);
        
        // Guardar dados antigos para histórico
        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("dataAnterior", agendamento.getDataAgendamento());
        detalhes.put("horarioAnterior", agendamento.getHoraInicio());
        detalhes.put("novaData", novaData);
        detalhes.put("novoHorario", novoHorario);
        
        // Atualizar agendamento
        agendamento.setDataAgendamento(novaData);
        agendamento.setHoraInicio(novoHorario);
        agendamento.setHoraFim(novaHoraFim);
        
        Agendamento agendamentoRemarcado = agendamentoRepository.save(agendamento);
        
        // Registrar no histórico
        historicoAgendamentoService.registrarAcao(
            agendamentoRemarcado, usuario, AcaoHistorico.REMARCADO, detalhes
        );
        
        return agendamentoRemarcado;
    }

    /**
     * Atualizar status do agendamento
     */
    public Agendamento atualizarStatus(Integer id, StatusAgendamento novoStatus, Integer usuarioId) {
        log.info("Atualizando status do agendamento ID: {} para: {}", id, novoStatus);
        
        Agendamento agendamento = buscarPorId(id);
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        
        // Verificar se é admin
        if (!usuarioService.isAdministrador(usuarioId)) {
            throw new BusinessException("Apenas administradores podem alterar o status");
        }
        
        // Validar transição de status
        validarTransicaoStatus(agendamento.getStatus(), novoStatus);
        
        StatusAgendamento statusAnterior = agendamento.getStatus();
        agendamento.setStatus(novoStatus);
        
        Agendamento agendamentoAtualizado = agendamentoRepository.save(agendamento);
        
        // Registrar no histórico
        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("statusAnterior", statusAnterior);
        detalhes.put("novoStatus", novoStatus);
        
        AcaoHistorico acao = novoStatus == StatusAgendamento.FINALIZADO ? 
            AcaoHistorico.FINALIZADO : AcaoHistorico.EDITADO;
        
        historicoAgendamentoService.registrarAcao(
            agendamentoAtualizado, usuario, acao, detalhes
        );
        
        return agendamentoAtualizado;
    }

    /**
     * Buscar próximo agendamento do usuário
     */
    @Transactional(readOnly = true)
    public Agendamento buscarProximoAgendamento(Integer usuarioId) {
        return agendamentoRepository.findProximoAgendamento(usuarioId)
                .orElse(null);
    }

    /**
     * Validar data e horário do agendamento
     */
    private void validarDataHorario(LocalDate data, LocalTime horario) {
        // Verificar se data é futura
        if (data.isBefore(LocalDate.now())) {
            throw new BusinessException("Data do agendamento deve ser futura");
        }
        
        // Verificar se é hoje e horário já passou
        if (data.equals(LocalDate.now()) && horario.isBefore(LocalTime.now())) {
            throw new BusinessException("Horário já passou");
        }
        
        // Verificar horário de funcionamento
        LocalTime inicio = LocalTime.parse(horarioInicio);
        LocalTime fim = LocalTime.parse(horarioFim);
        
        if (horario.isBefore(inicio) || horario.isAfter(fim)) {
            throw new BusinessException("Horário fora do expediente: " + inicio + " às " + fim);
        }
        
        // Verificar se é domingo (opcional)
        if (data.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            throw new BusinessException("Não atendemos aos domingos");
        }
    }

    /**
     * Verificar disponibilidade de horário
     */
    private void verificarDisponibilidade(LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        List<Agendamento> conflitos = agendamentoRepository.findConflitosHorario(data, horaInicio, horaFim);
        
        if (!conflitos.isEmpty()) {
            throw new BusinessException("Horário não disponível. Já existe agendamento neste período");
        }
    }

    /**
     * Calcular taxa de cancelamento
     */
    private BigDecimal calcularTaxaCancelamento(Agendamento agendamento) {
        LocalDateTime dataHoraAgendamento = LocalDateTime.of(
            agendamento.getDataAgendamento(), 
            agendamento.getHoraInicio()
        );
        
        LocalDateTime agora = LocalDateTime.now();
        long horasAteAgendamento = java.time.Duration.between(agora, dataHoraAgendamento).toHours();
        
        // Se cancelar com menos horas de antecedência que o configurado, cobra taxa
        if (horasAteAgendamento < horasAntecedenciaCancelamento) {
            return agendamento.getValorTotal().multiply(taxaCancelamento);
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Validar transição de status
     */
    private void validarTransicaoStatus(StatusAgendamento statusAtual, StatusAgendamento novoStatus) {
        // Definir transições válidas
        boolean transicaoValida = switch (statusAtual) {
            case AGENDADO -> novoStatus == StatusAgendamento.EM_ANDAMENTO || 
                           novoStatus == StatusAgendamento.CANCELADO;
            case EM_ANDAMENTO -> novoStatus == StatusAgendamento.FINALIZADO;
            case FINALIZADO, CANCELADO -> false; // Estados finais
        };
        
        if (!transicaoValida) {
            throw new BusinessException(
                "Transição inválida de " + statusAtual + " para " + novoStatus
            );
        }
    }
}