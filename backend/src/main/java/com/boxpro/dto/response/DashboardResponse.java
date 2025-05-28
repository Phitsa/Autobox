package com.boxpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {
    
    private ResumoGeral resumoGeral;
    private List<AgendamentosDia> agendamentosPorDia;
    private List<ServicoPopular> servicosMaisPopulares;
    private List<ClienteFrequente> clientesMaisFrequentes;
    private EstatisticasPeriodo estatisticasPeriodo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResumoGeral {
        private Long totalClientes;
        private Long totalAgendamentosHoje;
        private Long totalAgendamentosMes;
        private BigDecimal receitaMes;
        private BigDecimal receitaHoje;
        private Long servicosAtivos;
        private Long veiculosCadastrados;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgendamentosDia {
        private LocalDate data;
        private Long total;
        private Long concluidos;
        private Long cancelados;
        private BigDecimal receita;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicoPopular {
        private Integer servicoId;
        private String nome;
        private Long totalAgendamentos;
        private BigDecimal receitaTotal;
        private Integer duracaoMedia;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClienteFrequente {
        private Integer clienteId;
        private String nome;
        private String email;
        private Long totalAgendamentos;
        private BigDecimal totalGasto;
        private LocalDate ultimoAgendamento;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstatisticasPeriodo {
        private LocalDate dataInicio;
        private LocalDate dataFim;
        private Map<String, Long> agendamentosPorStatus;
        private BigDecimal receitaTotal;
        private BigDecimal ticketMedio;
        private Long totalCancelamentos;
        private BigDecimal totalTaxasCancelamento;
    }
}