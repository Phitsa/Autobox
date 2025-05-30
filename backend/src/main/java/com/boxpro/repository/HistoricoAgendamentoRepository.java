package com.boxpro.repository;

import com.boxpro.entity.HistoricoAgendamento;
import com.boxpro.entity.enums.AcaoHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HistoricoAgendamentoRepository extends JpaRepository<HistoricoAgendamento, Integer> {
    
    // Buscar histórico por agendamento
    List<HistoricoAgendamento> findByAgendamentoIdOrderByDataAcaoDesc(Integer agendamentoId);
    
    // Buscar histórico por usuário que realizou a ação
    List<HistoricoAgendamento> findByUsuarioIdOrderByDataAcaoDesc(Integer usuarioId);
    
    // Buscar por tipo de ação
    List<HistoricoAgendamento> findByAcaoOrderByDataAcaoDesc(AcaoHistorico acao);
    
    // Buscar histórico por período
    List<HistoricoAgendamento> findByDataAcaoBetweenOrderByDataAcaoDesc(
            LocalDateTime dataInicio, LocalDateTime dataFim);
    
    // Buscar última ação de um agendamento
    Optional<HistoricoAgendamento> findTopByAgendamentoIdOrderByDataAcaoDesc(Integer agendamentoId);
    
    // Buscar histórico de cancelamentos
    @Query("SELECT h FROM HistoricoAgendamento h WHERE h.acao = 'CANCELADO' " +
           "ORDER BY h.dataAcao DESC")
    List<HistoricoAgendamento> findHistoricoCancelamentos();
    
    // Buscar ações por agendamento e tipo
    List<HistoricoAgendamento> findByAgendamentoIdAndAcao(Integer agendamentoId, AcaoHistorico acao);
    
    // Contar ações por tipo em um período
    @Query("SELECT h.acao, COUNT(h) FROM HistoricoAgendamento h " +
           "WHERE h.dataAcao BETWEEN :dataInicio AND :dataFim " +
           "GROUP BY h.acao")
    List<Object[]> countAcoesPorTipoNoPeriodo(@Param("dataInicio") LocalDateTime dataInicio,
                                              @Param("dataFim") LocalDateTime dataFim);
    
    // Buscar histórico com detalhes específicos
    @Query("SELECT h FROM HistoricoAgendamento h " +
           "WHERE h.agendamento.id = :agendamentoId " +
           "AND JSON_EXTRACT(h.detalhes, '$.campo') = :valor")
    List<HistoricoAgendamento> findByAgendamentoAndDetalhe(@Param("agendamentoId") Integer agendamentoId,
                                                           @Param("valor") String valor);
    
    // Auditoria: buscar todas as ações de um usuário em um dia
    @Query("SELECT h FROM HistoricoAgendamento h " +
           "WHERE h.usuario.id = :usuarioId " +
           "AND DATE(h.dataAcao) = :data " +
           "ORDER BY h.dataAcao DESC")
    List<HistoricoAgendamento> findAcoesPorUsuarioNoDia(@Param("usuarioId") Integer usuarioId,
                                                        @Param("data") LocalDate data);
}