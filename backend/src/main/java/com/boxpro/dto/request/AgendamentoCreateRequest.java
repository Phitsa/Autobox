package com.boxpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoCreateRequest {
    
    @NotNull(message = "Veículo é obrigatório")
    @Positive(message = "ID do veículo deve ser positivo")
    private Integer veiculoId;
    
    @NotNull(message = "Serviço é obrigatório")
    @Positive(message = "ID do serviço deve ser positivo")
    private Integer servicoId;
    
    @NotNull(message = "Data do agendamento é obrigatória")
    @Future(message = "Data deve ser futura")
    private LocalDate dataAgendamento;
    
    @NotNull(message = "Horário de início é obrigatório")
    private LocalTime horaInicio;
    
    @Size(max = 1000, message = "Observações não podem ter mais de 1000 caracteres")
    private String observacoes;
    
    @AssertTrue(message = "Data e horário devem ser futuros")
    private boolean isDataHorarioFuturo() {
        if (dataAgendamento == null || horaInicio == null) return true;
        
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();
        
        if (dataAgendamento.isAfter(hoje)) return true;
        if (dataAgendamento.equals(hoje) && horaInicio.isAfter(agora.plusMinutes(30))) return true;
        
        return false;
    }
}