package com.boxpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaServicoResponse {
    
    private Integer id;
    private String nome;
    private String descricao;
    private Integer totalServicos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}