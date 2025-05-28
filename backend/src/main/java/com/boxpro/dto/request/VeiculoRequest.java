package com.boxpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeiculoRequest {
    
    @NotBlank(message = "Modelo é obrigatório")
    @Size(min = 2, max = 255, message = "Modelo deve ter entre 2 e 255 caracteres")
    private String modelo;
    
    @NotBlank(message = "Marca é obrigatória")
    @Size(min = 2, max = 255, message = "Marca deve ter entre 2 e 255 caracteres")
    private String marca;
    
    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1950, message = "Ano não pode ser anterior a 1950")
    private Year ano;
    
    @NotBlank(message = "Placa é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}[0-9]{1}[A-Z0-9]{1}[0-9]{2}$", 
             message = "Placa inválida. Use formato ABC1234 ou ABC1D23 (Mercosul)")
    private String placa;
    
    @Size(max = 50, message = "Cor não pode ter mais de 50 caracteres")
    private String cor;
    
    @AssertTrue(message = "Ano não pode ser futuro além do próximo ano")
    private boolean isAnoValido() {
        if (ano == null) return true;
        return !ano.isAfter(Year.now().plusYears(1));
    }
}