package com.boxpro.service;

import com.boxpro.entity.Agendamento;
import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.StatusAgendamento;
import com.boxpro.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaxaCancelamentoService {

    private final AgendamentoRepository agendamentoRepository;

    @Value("${cancelamento.prazo.isencao.horas:24}")
    private Integer prazoIsencaoHoras;

    @Value("${cancelamento.taxa.padrao:0.20}")
    private BigDecimal taxaPadrao;

    @Value("${cancelamento.taxa.urgente:0.50}")
    private BigDecimal taxaUrgente;

    @Value("${cancelamento.prazo.urgente.horas:6}")
    private Integer prazoUrgenteHoras;

    @Value("${cancelamento.max.cancelamentos.mes:3}")
    private Integer maxCancelamentosPorMes;

    @Value("${cancelamento.taxa.adicional.excesso:0.10}")
    private BigDecimal taxaAdicionalExcesso;

    /**
     * Calcular taxa de cancelamento
     */
    public BigDecimal calcularTaxa(Agendamento agendamento, Usuario usuario) {
        log.info("Calculando taxa de cancelamento para agendamento ID: {}", agendamento.getId());
        
        // Verificar se já foi pago
        if (agendamento.getStatus() == StatusAgendamento.FINALIZADO) {
            return agendamento.getValorTotal(); // Taxa de 100% se já foi executado
        }
        
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataHoraAgendamento = LocalDateTime.of(
            agendamento.getDataAgendamento(), 
            agendamento.getHoraInicio()
        );
        
        long horasAteAgendamento = Duration.between(agora, dataHoraAgendamento).toHours();
        
        // Calcular taxa base
        BigDecimal taxaBase = calcularTaxaBase(horasAteAgendamento, agendamento.getValorTotal());
        
        // Aplicar taxa adicional por excesso de cancelamentos
        BigDecimal taxaAdicional = calcularTaxaAdicionalPorHistorico(usuario);
        
        // Taxa total
        BigDecimal taxaTotal = taxaBase.add(taxaBase.multiply(taxaAdicional));
        
        // Limitar taxa ao valor total do serviço
        if (taxaTotal.compareTo(agendamento.getValorTotal()) > 0) {
            taxaTotal = agendamento.getValorTotal();
        }
        
        return taxaTotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcular taxa base pelo prazo
     */
    private BigDecimal calcularTaxaBase(long horasAteAgendamento, BigDecimal valorServico) {
        if (horasAteAgendamento >= prazoIsencaoHoras) {
            // Isento de taxa
            return BigDecimal.ZERO;
        } else if (horasAteAgendamento < prazoUrgenteHoras) {
            // Taxa urgente (50%)
            return valorServico.multiply(taxaUrgente);
        } else {
            // Taxa padrão (20%)
            return valorServico.multiply(taxaPadrao);
        }
    }

    /**
     * Calcular taxa adicional baseada no histórico
     */
    private BigDecimal calcularTaxaAdicionalPorHistorico(Usuario usuario) {
        // Contar cancelamentos no mês atual
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        
        List<Agendamento> cancelamentosRecentes = agendamentoRepository
                .findByUsuarioIdAndStatus(usuario.getId(), StatusAgendamento.CANCELADO)
                .stream()
                .filter(a -> a.getDataCancelamento() != null && 
                           a.getDataCancelamento().isAfter(inicioMes))
                .toList();
        
        int totalCancelamentos = cancelamentosRecentes.size();
        
        // Se excedeu o limite, aplica taxa adicional
        if (totalCancelamentos >= maxCancelamentosPorMes) {
            int excesso = totalCancelamentos - maxCancelamentosPorMes + 1;
            return taxaAdicionalExcesso.multiply(BigDecimal.valueOf(excesso));
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Obter política de cancelamento
     */
    public Map<String, Object> obterPoliticaCancelamento() {
        Map<String, Object> politica = new HashMap<>();
        
        politica.put("prazoIsencao", prazoIsencaoHoras + " horas");
        politica.put("taxaPadrao", formatarPercentual(taxaPadrao));
        politica.put("taxaUrgente", formatarPercentual(taxaUrgente));
        politica.put("prazoUrgente", prazoUrgenteHoras + " horas");
        politica.put("limiteMensal", maxCancelamentosPorMes + " cancelamentos");
        politica.put("taxaExcesso", formatarPercentual(taxaAdicionalExcesso) + " por cancelamento extra");
        
        Map<String, String> regras = new HashMap<>();
        regras.put("1", "Cancelamento com " + prazoIsencaoHoras + "h ou mais: SEM TAXA");
        regras.put("2", "Cancelamento entre " + prazoUrgenteHoras + "h e " + prazoIsencaoHoras + "h: " + formatarPercentual(taxaPadrao));
        regras.put("3", "Cancelamento com menos de " + prazoUrgenteHoras + "h: " + formatarPercentual(taxaUrgente));
        regras.put("4", "Mais de " + maxCancelamentosPorMes + " cancelamentos/mês: taxa adicional de " + formatarPercentual(taxaAdicionalExcesso));
        
        politica.put("regras", regras);
        
        return politica;
    }

    /**
     * Simular taxa de cancelamento
     */
    public Map<String, Object> simularTaxa(Agendamento agendamento) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataHoraAgendamento = LocalDateTime.of(
            agendamento.getDataAgendamento(), 
            agendamento.getHoraInicio()
        );
        
        Map<String, Object> simulacao = new HashMap<>();
        
        // Calcular para diferentes momentos
        for (int horas : new int[]{48, 24, 12, 6, 2}) {
            LocalDateTime momentoCancelamento = dataHoraAgendamento.minusHours(horas);
            
            if (momentoCancelamento.isAfter(agora)) {
                long horasAteAgendamento = Duration.between(
                    momentoCancelamento, 
                    dataHoraAgendamento
                ).toHours();
                
                BigDecimal taxa = calcularTaxaBase(horasAteAgendamento, agendamento.getValorTotal());
                
                String chave = "cancelarCom" + horas + "hAntecedencia";
                simulacao.put(chave, formatarValor(taxa));
            }
        }
        
        // Taxa atual
        long horasAtuais = Duration.between(agora, dataHoraAgendamento).toHours();
        BigDecimal taxaAtual = calcularTaxaBase(horasAtuais, agendamento.getValorTotal());
        simulacao.put("taxaAtual", formatarValor(taxaAtual));
        simulacao.put("horasRestantes", horasAtuais);
        
        return simulacao;
    }

    /**
     * Obter histórico de cancelamentos
     */
    public Map<String, Object> obterHistoricoCancelamentos(Usuario usuario) {
        List<Agendamento> todosCancelamentos = agendamentoRepository
                .findByUsuarioIdAndStatus(usuario.getId(), StatusAgendamento.CANCELADO);
        
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        
        List<Agendamento> cancelamentosMesAtual = todosCancelamentos.stream()
                .filter(a -> a.getDataCancelamento() != null && 
                           a.getDataCancelamento().isAfter(inicioMes))
                .toList();
        
        BigDecimal totalTaxasPagas = todosCancelamentos.stream()
                .map(Agendamento::getTaxaCancelamento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> historico = new HashMap<>();
        historico.put("totalCancelamentos", todosCancelamentos.size());
        historico.put("cancelamentosMesAtual", cancelamentosMesAtual.size());
        historico.put("limiteMensal", maxCancelamentosPorMes);
        historico.put("cancelamentosRestantes", 
            Math.max(0, maxCancelamentosPorMes - cancelamentosMesAtual.size()));
        historico.put("totalTaxasPagas", formatarValor(totalTaxasPagas));
        
        return historico;
    }

    private String formatarPercentual(BigDecimal valor) {
        return valor.multiply(BigDecimal.valueOf(100)).intValue() + "%";
    }

    private String formatarValor(BigDecimal valor) {
        return "R$ " + valor.setScale(2, RoundingMode.HALF_UP).toString();
    }
}