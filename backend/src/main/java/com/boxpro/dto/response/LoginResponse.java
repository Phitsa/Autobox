package com.boxpro.dto.response;

import com.boxpro.entity.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private String token;
    private String tipo;
    private Long expiresIn;
    private UsuarioInfo usuario;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioInfo {
        private Integer id;
        private String nome;
        private String email;
        private TipoUsuario tipoUsuario;
    }
}