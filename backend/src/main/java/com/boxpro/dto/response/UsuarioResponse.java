package com.boxpro.dto.response;

import com.boxpro.entity.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponse {
    
    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private TipoUsuario tipoUsuario;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalVeiculos;
    private Integer totalAgendamentos;
}