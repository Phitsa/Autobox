package com.boxpro.repository;

import com.boxpro.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    List<Agendamento> findByDataAgendamentoOrderByHoraInicioAsc(LocalDate dataAgendamento);
    
    List<Agendamento> findByClienteIdOrderByDataAgendamentoDescHoraInicioDesc(Integer clienteId);
    
    List<Agendamento> findByFuncionarioResponsavelIdOrderByDataAgendamentoDescHoraInicioDesc(Integer funcionarioId);
    
    List<Agendamento> findByStatusOrderByDataAgendamentoDescHoraInicioDesc(String status);
    
    @Query("SELECT a FROM Agendamento a WHERE a.dataAgendamento BETWEEN :dataInicio AND :dataFim ORDER BY a.dataAgendamento DESC, a.horaInicio DESC")
    List<Agendamento> findByDataAgendamentoBetween(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
    
    @Query("SELECT a FROM Agendamento a WHERE a.dataAgendamento >= :data AND a.status = :status ORDER BY a.dataAgendamento ASC, a.horaInicio ASC")
    List<Agendamento> findAgendamentosFuturos(@Param("data") LocalDate data, @Param("status") String status);
    
    @Query("SELECT a FROM Agendamento a WHERE a.dataAgendamento = :data AND a.status IN ('agendado', 'em_andamento') ORDER BY a.horaInicio ASC")
    List<Agendamento> findAgendamentosAtivos(@Param("data") LocalDate data);
    
    boolean existsByDataAgendamentoAndHoraInicioAndStatus(LocalDate dataAgendamento, LocalDate horaInicio, String status);
}