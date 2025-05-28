package com.boxpro.dto.request;

import com.boxpro.entity.enums.TipoUsuario;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255, message = "Email não pode ter mais de 255 caracteres")
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).*$", 
             message = "Senha deve conter letras e números")
    private String senha;
    
    @Pattern(regexp = "^\\(?[1-9]{2}\\)?\\s?9?[0-9]{4}-?[0-9]{4}$", 
             message = "Telefone inválido. Use formato: (11) 91234-5678 ou 11912345678")
    private String telefone;
    
    private TipoUsuario tipoUsuario = TipoUsuario.CLIENTE;
}