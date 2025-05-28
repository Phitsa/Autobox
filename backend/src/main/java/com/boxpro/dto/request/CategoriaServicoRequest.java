package com.boxpro.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaServicoRequest {
    
    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Size(max = 1000, message = "Descrição não pode ter mais de 1000 caracteres")
    private String descricao;
}