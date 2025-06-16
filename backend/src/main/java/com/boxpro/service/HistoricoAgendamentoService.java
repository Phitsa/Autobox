package com.boxpro.service;

import com.boxpro.entity.HistoricoAgendamento;
import com.boxpro.repository.HistoricoAgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HistoricoAgendamentoService {

    @Autowired
    private HistoricoAgendamentoRepository historicoRepository;

    @PostConstruct
    public void init() {
        System.out.println("✅ HistoricoAgendamentoService carregado!");
    }

    // Método paginado
    public Page<HistoricoAgendamento> listarHistoricosPaginados(Pageable pageable) {
        return historicoRepository.findAll(pageable);
    }

    // Método para listar todos
    public List<HistoricoAgendamento> listarHistoricos() {
        return historicoRepository.findAll();
    }

    public Optional<HistoricoAgendamento> buscarPorId(Integer id) {
        return historicoRepository.findById(id);
    }

    public List<HistoricoAgendamento> buscarPorAgendamentoId(Integer agendamentoId) {
        return historicoRepository.findByAgendamento_IdOrderByDataAcaoDesc(agendamentoId);
    }

    public List<HistoricoAgendamento> buscarPorFuncionarioId(Integer funcionarioId) {
        return historicoRepository.findByFuncionarioIdOrderByDataAcaoDesc(funcionarioId);
    }

    public List<HistoricoAgendamento> buscarPorAcao(String acao) {
        return historicoRepository.findByAcaoOrderByDataAcaoDesc(acao);
    }

    public HistoricoAgendamento criarHistorico(HistoricoAgendamento historico) {
        // Validações básicas
        if (historico.getAgendamento() == null) {
            throw new IllegalArgumentException("Agendamento é obrigatório");
        }
        
        if (historico.getFuncionarioId() == null) {
            throw new IllegalArgumentException("Funcionário é obrigatório");
        }
        
        if (historico.getAcao() == null || historico.getAcao().trim().isEmpty()) {
            throw new IllegalArgumentException("Ação é obrigatória");
        }

        return historicoRepository.save(historico);
    }

    public void deletarHistorico(Integer id) {
        if (!historicoRepository.existsById(id)) {
            throw new IllegalArgumentException("Histórico não encontrado");
        }
        historicoRepository.deleteById(id);
    }
}