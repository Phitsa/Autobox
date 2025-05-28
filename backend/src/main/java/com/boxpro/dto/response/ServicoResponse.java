package com.boxpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicoResponse {
    
    private Integer id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer duracaoEstimada;
    private String duracaoFormatada;
    private Boolean ativo;
    private CategoriaInfo categoria;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoriaInfo {
        private Integer id;
        private String nome;
    }
    
    public String getDuracaoFormatada() {
        if (duracaoEstimada == null) return "";
        int horas = duracaoEstimada / 60;
        int minutos = duracaoEstimada % 60;
        if (horas > 0) {
            return String.format("%dh %dmin", horas, minutos);
        }
        return String.format("%dmin", minutos);
    }
}