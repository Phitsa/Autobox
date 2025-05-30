package com.boxpro.service;

import com.boxpro.entity.Agendamento;
import com.boxpro.entity.Servico;
import com.boxpro.entity.enums.StatusAgendamento;
import com.boxpro.exception.BusinessException;
import com.boxpro.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DisponibilidadeService {

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoService servicoService;

    @Value("${agendamento.horario.inicio:08:00}")
    private String horarioAbertura;

    @Value("${agendamento.horario.fim:18:00}")
    private String horarioFechamento;

    @Value("${agendamento.intervalo.minutos:30}")
    private Integer intervaloMinutos;

    @Value("${agendamento.dias.antecedencia.max:30}")
    private Integer diasAntecedenciaMaxima;

    @Value("${agendamento.buffer.minutos:15}")
    private Integer bufferEntreAgendamentos;

    /**
     * Verificar disponibilidade de horários para uma data e serviço
     */
    public List<LocalTime> obterHorariosDisponiveis(LocalDate data, Integer servicoId) {
        log.info("Verificando horários disponíveis para data: {} e serviço: {}", data, servicoId);
        
        // Validar data
        validarDataAgendamento(data);
        
        // Buscar serviço
        Servico servico = servicoService.buscarPorId(servicoId);
        
        // Obter todos os horários possíveis do dia
        List<LocalTime> todosHorarios = gerarHorariosDoDia();
        
        // Obter agendamentos do dia
        List<Agendamento> agendamentosDoDia = agendamentoRepository
                .findByDataAgendamento(data).stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .collect(Collectors.toList());
        
        // Filtrar horários disponíveis
        return todosHorarios.stream()
                .filter(horario -> isHorarioDisponivel(horario, data, servico, agendamentosDoDia))
                .collect(Collectors.toList());
    }

    /**
     * Verificar se um horário específico está disponível
     */
    public boolean verificarDisponibilidade(LocalDate data, LocalTime horaInicio, 
                                          LocalTime horaFim, Integer agendamentoIdIgnorar) {
        List<Agendamento> conflitos = agendamentoRepository
                .findConflitosHorario(data, horaInicio, horaFim);
        
        // Se estiver editando, ignorar o próprio agendamento
        if (agendamentoIdIgnorar != null) {
            conflitos = conflitos.stream()
                    .filter(a -> !a.getId().equals(agendamentoIdIgnorar))
                    .collect(Collectors.toList());
        }
        
        return conflitos.isEmpty();
    }

    /**
     * Obter próximo horário disponível
     */
    public LocalTime obterProximoHorarioDisponivel(LocalDate data, Integer servicoId) {
        List<LocalTime> horariosDisponiveis = obterHorariosDisponiveis(data, servicoId);
        
        if (horariosDisponiveis.isEmpty()) {
            return null;
        }
        
        LocalTime agora = LocalTime.now();
        
        // Se for hoje, filtrar horários passados
        if (data.equals(LocalDate.now())) {
            horariosDisponiveis = horariosDisponiveis.stream()
                    .filter(h -> h.isAfter(agora.plusMinutes(30))) // Mínimo 30 min de antecedência
                    .collect(Collectors.toList());
        }
        
        return horariosDisponiveis.isEmpty() ? null : horariosDisponiveis.get(0);
    }

    /**
     * Obter dias com disponibilidade
     */
    public Map<LocalDate, Integer> obterDisponibilidadePorDia(
            LocalDate dataInicio, LocalDate dataFim, Integer servicoId) {
        
        Map<LocalDate, Integer> disponibilidadePorDia = new LinkedHashMap<>();
        LocalDate data = dataInicio;
        
        while (!data.isAfter(dataFim)) {
            if (!isDomingo(data)) {
                List<LocalTime> horariosDisponiveis = obterHorariosDisponiveis(data, servicoId);
                disponibilidadePorDia.put(data, horariosDisponiveis.size());
            }
            data = data.plusDays(1);
        }
        
        return disponibilidadePorDia;
    }

    /**
     * Sugerir melhor horário baseado em preferências
     */
    public LocalTime sugerirMelhorHorario(LocalDate data, Integer servicoId, 
                                         String preferencia) {
        List<LocalTime> horariosDisponiveis = obterHorariosDisponiveis(data, servicoId);
        
        if (horariosDisponiveis.isEmpty()) {
            return null;
        }
        
        switch (preferencia.toLowerCase()) {
            case "manha":
                return horariosDisponiveis.stream()
                        .filter(h -> h.isBefore(LocalTime.NOON))
                        .findFirst()
                        .orElse(horariosDisponiveis.get(0));
                        
            case "tarde":
                return horariosDisponiveis.stream()
                        .filter(h -> h.isAfter(LocalTime.NOON))
                        .findFirst()
                        .orElse(horariosDisponiveis.get(horariosDisponiveis.size() - 1));
                        
            case "primeiro":
                return horariosDisponiveis.get(0);
                
            case "ultimo":
                return horariosDisponiveis.get(horariosDisponiveis.size() - 1);
                
            default:
                // Retornar horário do meio do dia se disponível
                LocalTime meiodia = LocalTime.of(12, 0);
                return horariosDisponiveis.stream()
                        .min(Comparator.comparingLong(h -> 
                            Math.abs(ChronoUnit.MINUTES.between(h, meiodia))))
                        .orElse(horariosDisponiveis.get(0));
        }
    }

    /**
     * Calcular taxa de ocupação para um período
     */
    public Map<String, Object> calcularTaxaOcupacao(LocalDate dataInicio, LocalDate dataFim) {
        long totalSlots = 0;
        long slotsOcupados = 0;
        
        LocalDate data = dataInicio;
        while (!data.isAfter(dataFim)) {
            if (!isDomingo(data)) {
                List<LocalTime> todosHorarios = gerarHorariosDoDia();
                totalSlots += todosHorarios.size();
                
                List<Agendamento> agendamentos = agendamentoRepository
                        .findByDataAgendamento(data).stream()
                        .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                        .collect(Collectors.toList());
                
                for (Agendamento agendamento : agendamentos) {
                    long duracao = ChronoUnit.MINUTES.between(
                        agendamento.getHoraInicio(), 
                        agendamento.getHoraFim()
                    );
                    slotsOcupados += duracao / intervaloMinutos;
                }
            }
            data = data.plusDays(1);
        }
        
        double taxaOcupacao = totalSlots > 0 ? 
            (double) slotsOcupados / totalSlots * 100 : 0;
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalSlots", totalSlots);
        resultado.put("slotsOcupados", slotsOcupados);
        resultado.put("slotsLivres", totalSlots - slotsOcupados);
        resultado.put("taxaOcupacao", String.format("%.2f%%", taxaOcupacao));
        
        return resultado;
    }

    /**
     * Gerar todos os horários possíveis do dia
     */
    private List<LocalTime> gerarHorariosDoDia() {
        List<LocalTime> horarios = new ArrayList<>();
        LocalTime horario = LocalTime.parse(horarioAbertura);
        LocalTime fim = LocalTime.parse(horarioFechamento);
        
        while (horario.isBefore(fim)) {
            horarios.add(horario);
            horario = horario.plusMinutes(intervaloMinutos);
        }
        
        return horarios;
    }

    /**
     * Verificar se horário está disponível
     */
    private boolean isHorarioDisponivel(LocalTime horario, LocalDate data, 
                                       Servico servico, List<Agendamento> agendamentos) {
        LocalTime horaFim = horario.plusMinutes(servico.getDuracaoEstimada());
        
        // Verificar se não ultrapassa horário de fechamento
        if (horaFim.isAfter(LocalTime.parse(horarioFechamento))) {
            return false;
        }
        
        // Verificar conflitos com outros agendamentos
        for (Agendamento agendamento : agendamentos) {
            LocalTime inicioComBuffer = agendamento.getHoraInicio().minusMinutes(bufferEntreAgendamentos);
            LocalTime fimComBuffer = agendamento.getHoraFim().plusMinutes(bufferEntreAgendamentos);
            
            // Verificar sobreposição
            if (!(horaFim.isBefore(inicioComBuffer) || horario.isAfter(fimComBuffer))) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Validar data de agendamento
     */
    private void validarDataAgendamento(LocalDate data) {
        if (data.isBefore(LocalDate.now())) {
            throw new BusinessException("Data não pode ser no passado");
        }
        
        if (data.isAfter(LocalDate.now().plusDays(diasAntecedenciaMaxima))) {
            throw new BusinessException(
                "Agendamentos podem ser feitos com no máximo " + 
                diasAntecedenciaMaxima + " dias de antecedência"
            );
        }
        
        if (isDomingo(data)) {
            throw new BusinessException("Não atendemos aos domingos");
        }
    }

    /**
     * Verificar se é domingo
     */
    private boolean isDomingo(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.SUNDAY;
    }
}