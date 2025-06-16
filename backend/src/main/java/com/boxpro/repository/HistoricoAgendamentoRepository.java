package com.boxpro.repository;

import com.boxpro.entity.HistoricoAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoAgendamentoRepository extends JpaRepository<HistoricoAgendamento, Integer> {

    List<HistoricoAgendamento> findByAgendamento_IdOrderByDataAcaoDesc(Integer agendamentoId);
    
    List<HistoricoAgendamento> findByFuncionarioIdOrderByDataAcaoDesc(Integer funcionarioId);
    
    List<HistoricoAgendamento> findByAcaoOrderByDataAcaoDesc(String acao);
}