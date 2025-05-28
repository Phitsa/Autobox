package com.boxpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoResponse {
    
    private Integer id;
    private String modelo;
    private String marca;
    private Year ano;
    private String placa;
    private String cor;
    private UsuarioInfo proprietario;
    private Integer totalAgendamentos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UsuarioInfo {
        private Integer id;
        private String nome;
        private String email;
    }
}