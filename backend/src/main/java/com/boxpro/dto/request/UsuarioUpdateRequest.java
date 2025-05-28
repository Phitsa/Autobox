package com.boxpro.dto.request;

import com.boxpro.entity.enums.TipoUsuario;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String nome;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 255, message = "Email não pode ter mais de 255 caracteres")
    private String email;
    
    @Pattern(regexp = "^\\(?[1-9]{2}\\)?\\s?9?[0-9]{4}-?[0-9]{4}$", 
             message = "Telefone inválido. Use formato: (11) 91234-5678 ou 11912345678")
    private String telefone;
    
    private TipoUsuario tipoUsuario;
}