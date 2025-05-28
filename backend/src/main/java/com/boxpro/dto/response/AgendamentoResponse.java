package com.boxpro.dto.response;

import com.boxpro.entity.enums.StatusAgendamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendamentoResponse {
    
    private Integer id;
    private LocalDate dataAgendamento;
    private LocalTime horaInicio;
    private LocalTime horaFim;
    private StatusAgendamento status;
    private String observacoes;
    private BigDecimal valorTotal;
    private UsuarioInfo usuario;
    private VeiculoInfo veiculo;
    private ServicoInfo servico;
    private CancelamentoInfo cancelamento;
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
        private String telefone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VeiculoInfo {
        private Integer id;
        private String modelo;
        private String marca;
        private String placa;
        private String cor;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicoInfo {
        private Integer id;
        private String nome;
        private BigDecimal preco;
        private Integer duracaoEstimada;
        private String categoria;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CancelamentoInfo {
        private LocalDateTime dataCancelamento;
        private String motivo;
        private BigDecimal taxa;
    }
}