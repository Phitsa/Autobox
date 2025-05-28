package com.boxpro.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarSenhaRequest {
    
    @NotBlank(message = "Senha atual é obrigatória")
    private String senhaAtual;
    
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, max = 100, message = "Nova senha deve ter entre 6 e 100 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).*$", 
             message = "Nova senha deve conter letras e números")
    private String novaSenha;
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmacaoSenha;
}