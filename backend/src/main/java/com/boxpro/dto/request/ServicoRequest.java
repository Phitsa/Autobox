package com.boxpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoRequest {
    
    @NotBlank(message = "Nome do serviço é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @Size(max = 1000, message = "Descrição não pode ter mais de 1000 caracteres")
    private String descricao;
    
    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @DecimalMax(value = "99999.99", message = "Preço não pode ser maior que 99999.99")
    @Digits(integer = 5, fraction = 2, message = "Preço deve ter no máximo 5 dígitos inteiros e 2 decimais")
    private BigDecimal preco;
    
    @NotNull(message = "Duração estimada é obrigatória")
    @Min(value = 15, message = "Duração mínima é 15 minutos")
    @Max(value = 480, message = "Duração máxima é 480 minutos (8 horas)")
    private Integer duracaoEstimada;
    
    private Integer categoriaId;
    
    private Boolean ativo = true;
}