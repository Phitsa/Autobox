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
public class RemarcarAgendamentoRequest {
    
    @NotNull(message = "Nova data é obrigatória")
    @Future(message = "Nova data deve ser futura")
    private LocalDate novaData;
    
    @NotNull(message = "Novo horário é obrigatório")
    private LocalTime novoHorario;
    
    @Size(max = 500, message = "Motivo não pode ter mais de 500 caracteres")
    private String motivo;
    
    @AssertTrue(message = "Nova data e horário devem ser futuros")
    private boolean isDataHorarioFuturo() {
        if (novaData == null || novoHorario == null) return true;
        
        LocalDate hoje = LocalDate.now();
        LocalTime agora = LocalTime.now();
        
        if (novaData.isAfter(hoje)) return true;
        if (novaData.equals(hoje) && novoHorario.isAfter(agora.plusMinutes(30))) return true;
        
        return false;
    }
}