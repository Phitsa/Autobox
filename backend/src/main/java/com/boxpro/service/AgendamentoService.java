package com.boxpro.service;

import com.boxpro.entity.Agendamento;
import com.boxpro.entity.HistoricoAgendamento;
import com.boxpro.repository.AgendamentoRepository;
import com.boxpro.repository.HistoricoAgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private HistoricoAgendamentoRepository historicoRepository;

    @PostConstruct
    public void init() {
        System.out.println("✅ AgendamentoService carregado!");
    }

    // Método paginado
    public Page<Agendamento> listarAgendamentosPaginados(Pageable pageable) {
        return agendamentoRepository.findAll(pageable);
    }

    // Método para listar todos
    public List<Agendamento> listarAgendamentos() {
        return agendamentoRepository.findAll();
    }

    public Optional<Agendamento> buscarPorId(Integer id) {
        return agendamentoRepository.findById(id);
    }

    public List<Agendamento> buscarPorDataAgendamento(LocalDate data) {
        return agendamentoRepository.findByDataAgendamentoOrderByHoraInicioAsc(data);
    }

    public List<Agendamento> buscarPorClienteId(Integer clienteId) {
        return agendamentoRepository.findByClienteIdOrderByDataAgendamentoDescHoraInicioDesc(clienteId);
    }

    public List<Agendamento> buscarPorFuncionarioId(Integer funcionarioId) {
        return agendamentoRepository.findByFuncionarioResponsavelIdOrderByDataAgendamentoDescHoraInicioDesc(funcionarioId);
    }

    public List<Agendamento> buscarPorStatus(String status) {
        return agendamentoRepository.findByStatusOrderByDataAgendamentoDescHoraInicioDesc(status);
    }

    public List<Agendamento> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return agendamentoRepository.findByDataAgendamentoBetween(dataInicio, dataFim);
    }

    public List<Agendamento> buscarAgendamentosFuturos() {
        return agendamentoRepository.findAgendamentosFuturos(LocalDate.now(), "agendado");
    }

    public List<Agendamento> buscarAgendamentosAtivos(LocalDate data) {
        return agendamentoRepository.findAgendamentosAtivos(data);
    }

    public Agendamento criarAgendamento(Agendamento agendamento) {
        // Validações básicas
        if (agendamento.getClienteId() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        
        if (agendamento.getVeiculoId() == null) {
            throw new IllegalArgumentException("Veículo é obrigatório");
        }
        
        if (agendamento.getServicoId() == null) {
            throw new IllegalArgumentException("Serviço é obrigatório");
        }
        
        if (agendamento.getDataAgendamento() == null) {
            throw new IllegalArgumentException("Data do agendamento é obrigatória");
        }
        
        if (agendamento.getHoraInicio() == null) {
            throw new IllegalArgumentException("Hora de início é obrigatória");
        }

        // Verificar se data não é no passado
        if (agendamento.getDataAgendamento().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível agendar para datas passadas");
        }

        // Configurar status padrão
        if (agendamento.getStatus() == null || agendamento.getStatus().trim().isEmpty()) {
            agendamento.setStatus("agendado");
        }

        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);
        
        // Registrar no histórico
        registrarHistorico(savedAgendamento, 
                          agendamento.getFuncionarioResponsavelId() != null ? 
                          agendamento.getFuncionarioResponsavelId() : 1, 
                          "CRIADO", 
                          String.format("Agendamento criado para %s às %s", 
                                      agendamento.getDataAgendamento(), 
                                      agendamento.getHoraInicio()));
        
        return savedAgendamento;
    }

    public Agendamento atualizarAgendamento(Agendamento agendamento) {
        // Buscar agendamento existente
        Optional<Agendamento> agendamentoExistente = agendamentoRepository.findById(agendamento.getId());
        
        if (!agendamentoExistente.isPresent()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }

        Agendamento existente = agendamentoExistente.get();

        // Validações básicas
        if (agendamento.getClienteId() == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        
        if (agendamento.getVeiculoId() == null) {
            throw new IllegalArgumentException("Veículo é obrigatório");
        }
        
        if (agendamento.getServicoId() == null) {
            throw new IllegalArgumentException("Serviço é obrigatório");
        }
        
        if (agendamento.getDataAgendamento() == null) {
            throw new IllegalArgumentException("Data do agendamento é obrigatória");
        }
        
        if (agendamento.getHoraInicio() == null) {
            throw new IllegalArgumentException("Hora de início é obrigatória");
        }

        // Atualizar campos
        existente.setClienteId(agendamento.getClienteId());
        existente.setVeiculoId(agendamento.getVeiculoId());
        existente.setServicoId(agendamento.getServicoId());
        existente.setFuncionarioResponsavelId(agendamento.getFuncionarioResponsavelId());
        existente.setDataAgendamento(agendamento.getDataAgendamento());
        existente.setHoraInicio(agendamento.getHoraInicio());
        existente.setHoraFim(agendamento.getHoraFim());
        existente.setObservacoes(agendamento.getObservacoes());
        existente.setValorTotal(agendamento.getValorTotal());

        Agendamento savedAgendamento = agendamentoRepository.save(existente);

        // Registrar no histórico
        registrarHistorico(savedAgendamento, 
                          agendamento.getFuncionarioResponsavelId() != null ? 
                          agendamento.getFuncionarioResponsavelId() : 1, 
                          "ATUALIZADO", 
                          "Agendamento atualizado");

        return savedAgendamento;
    }

    public Agendamento atualizarStatus(Integer id, String novoStatus, Integer funcionarioId, String motivo) {
        Optional<Agendamento> optionalAgendamento = agendamentoRepository.findById(id);
        if (!optionalAgendamento.isPresent()) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }

        Agendamento agendamento = optionalAgendamento.get();
        String statusAnterior = agendamento.getStatus();
        agendamento.setStatus(novoStatus);
        
        if ("cancelado".equals(novoStatus)) {
            agendamento.setDataCancelamento(LocalDate.now());
            if (motivo != null && !motivo.trim().isEmpty()) {
                agendamento.setMotivoCancelamento(motivo);
            }
        }

        Agendamento savedAgendamento = agendamentoRepository.save(agendamento);

        // Registrar no histórico
        registrarHistorico(savedAgendamento, funcionarioId, "STATUS_ALTERADO", 
                          String.format("Status alterado de '%s' para '%s'%s", 
                                      statusAnterior, novoStatus,
                                      motivo != null ? ". Motivo: " + motivo : ""));

        return savedAgendamento;
    }

    public void deletarAgendamento(Integer id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new IllegalArgumentException("Agendamento não encontrado");
        }
        agendamentoRepository.deleteById(id);
    }

    private void registrarHistorico(Agendamento agendamento, Integer funcionarioId, String acao, String detalhes) {
        HistoricoAgendamento historico = new HistoricoAgendamento();
        historico.setAgendamento(agendamento);
        historico.setFuncionarioId(funcionarioId);
        historico.setAcao(acao);
        historico.setDetalhes(detalhes);
        
        historicoRepository.save(historico);
    }
}