package com.boxpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelarAgendamentoRequest {
    
    @NotBlank(message = "Motivo do cancelamento é obrigatório")
    @Size(min = 10, max = 500, message = "Motivo deve ter entre 10 e 500 caracteres")
    private String motivo;
}