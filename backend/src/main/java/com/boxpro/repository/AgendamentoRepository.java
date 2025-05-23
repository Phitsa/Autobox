package com.boxpro.repository;

import com.boxpro.entity.Agendamento;
import com.boxpro.entity.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
    
    // Buscar por status
    List<Agendamento> findByStatus(StatusAgendamento status);
    
    // Buscar por usuário
    List<Agendamento> findByUsuarioId(Integer usuarioId);
    
    // Buscar por veículo
    List<Agendamento> findByVeiculoId(Integer veiculoId);
    
    // Buscar por serviço
    List<Agendamento> findByServicoId(Integer servicoId);
    
    // Buscar por data
    List<Agendamento> findByDataAgendamento(LocalDate data);
    
    // Buscar por período
    List<Agendamento> findByDataAgendamentoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    // Buscar agendamentos futuros de um usuário
    List<Agendamento> findByUsuarioIdAndDataAgendamentoGreaterThanEqualOrderByDataAgendamentoAscHoraInicioAsc(
            Integer usuarioId, LocalDate data);
    
    // Verificar disponibilidade de horário
    @Query("SELECT a FROM Agendamento a WHERE a.dataAgendamento = :data " +
           "AND a.status != 'CANCELADO' " +
           "AND ((a.horaInicio <= :horaInicio AND a.horaFim > :horaInicio) " +
           "OR (a.horaInicio < :horaFim AND a.horaFim >= :horaFim) " +
           "OR (a.horaInicio >= :horaInicio AND a.horaFim <= :horaFim))")
    List<Agendamento> findConflitosHorario(@Param("data") LocalDate data,
                                          @Param("horaInicio") LocalTime horaInicio,
                                          @Param("horaFim") LocalTime horaFim);
    
    // Buscar agendamentos do dia
    @Query("SELECT a FROM Agendamento a WHERE a.dataAgendamento = CURRENT_DATE " +
           "ORDER BY a.horaInicio")
    List<Agendamento> findAgendamentosHoje();
    
    // Buscar agendamentos cancelados com taxa
    @Query("SELECT a FROM Agendamento a WHERE a.status = 'CANCELADO' " +
           "AND a.taxaCancelamento > 0 ORDER BY a.dataCancelamento DESC")
    List<Agendamento> findAgendamentosCanceladosComTaxa();
    
    // Estatísticas por período
    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.status = :status " +
           "AND a.dataAgendamento BETWEEN :dataInicio AND :dataFim")
    Long countByStatusAndPeriodo(@Param("status") StatusAgendamento status,
                                @Param("dataInicio") LocalDate dataInicio,
                                @Param("dataFim") LocalDate dataFim);
    
    // Buscar próximo agendamento de um usuário
    @Query("SELECT a FROM Agendamento a WHERE a.usuario.id = :usuarioId " +
           "AND a.dataAgendamento >= CURRENT_DATE " +
           "AND a.status = 'AGENDADO' " +
           "ORDER BY a.dataAgendamento, a.horaInicio")
    Optional<Agendamento> findProximoAgendamento(@Param("usuarioId") Integer usuarioId);
}