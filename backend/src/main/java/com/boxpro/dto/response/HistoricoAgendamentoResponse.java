package com.boxpro.dto.response;

import com.boxpro.entity.enums.AcaoHistorico;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoAgendamentoResponse {
    
    private Integer id;
    private Integer agendamentoId;
    private UsuarioInfo usuario;
    private AcaoHistorico acao;
    private Map<String, Object> detalhes;
    private LocalDateTime dataAcao;
    
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