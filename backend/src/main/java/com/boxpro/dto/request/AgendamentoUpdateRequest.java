package com.boxpro.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendamentoUpdateRequest {
    
    @Size(max = 1000, message = "Observações não podem ter mais de 1000 caracteres")
    private String observacoes;
}