package com.boxpro.service;

import com.boxpro.entity.Agendamento;
import com.boxpro.entity.Servico;
import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.StatusAgendamento;
import com.boxpro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RelatorioService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final VeiculoRepository veiculoRepository;

    /**
     * Relatório financeiro mensal
     */
    public Map<String, Object> relatorioFinanceiroMensal(Integer mes, Integer ano) {
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        
        List<Agendamento> agendamentos = agendamentoRepository
                .findByDataAgendamentoBetween(inicio, fim);
        
        // Receitas
        BigDecimal receitaBruta = calcularReceitaBruta(agendamentos);
        BigDecimal receitaLiquida = calcularReceitaLiquida(agendamentos);
        BigDecimal receitaCancelamentos = calcularReceitaCancelamentos(agendamentos);
        
        // Análise por semana
        Map<Integer, BigDecimal> receitaPorSemana = calcularReceitaPorSemana(agendamentos);
        
        // Análise por categoria
        Map<String, BigDecimal> receitaPorCategoria = calcularReceitaPorCategoria(agendamentos);
        
        // Análise por forma de pagamento (simulado)
        Map<String, BigDecimal> receitaPorPagamento = new HashMap<>();
        receitaPorPagamento.put("Dinheiro", receitaLiquida.multiply(new BigDecimal("0.3")));
        receitaPorPagamento.put("Cartão", receitaLiquida.multiply(new BigDecimal("0.5")));
        receitaPorPagamento.put("PIX", receitaLiquida.multiply(new BigDecimal("0.2")));
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("periodo", mes + "/" + ano);
        relatorio.put("receitaBruta", receitaBruta);
        relatorio.put("receitaLiquida", receitaLiquida);
        relatorio.put("receitaCancelamentos", receitaCancelamentos);
        relatorio.put("ticketMedio", calcularTicketMedio(agendamentos));
        relatorio.put("totalAgendamentos", agendamentos.size());
        relatorio.put("agendamentosFinalizados", contarPorStatus(agendamentos, StatusAgendamento.FINALIZADO));
        relatorio.put("agendamentosCancelados", contarPorStatus(agendamentos, StatusAgendamento.CANCELADO));
        relatorio.put("receitaPorSemana", receitaPorSemana);
        relatorio.put("receitaPorCategoria", receitaPorCategoria);
        relatorio.put("receitaPorPagamento", receitaPorPagamento);
        
        return relatorio;
    }

    /**
     * Relatório de produtividade
     */
    public Map<String, Object> relatorioProdutividade(LocalDate dataInicio, LocalDate dataFim) {
        List<Agendamento> agendamentos = agendamentoRepository
                .findByDataAgendamentoBetween(dataInicio, dataFim);
        
        // Análise por dia da semana
        Map<DayOfWeek, Long> agendamentosPorDiaSemana = agendamentos.stream()
                .collect(Collectors.groupingBy(
                    a -> a.getDataAgendamento().getDayOfWeek(),
                    Collectors.counting()
                ));
        
        // Análise por horário
        Map<Integer, Long> agendamentosPorHora = agendamentos.stream()
                .collect(Collectors.groupingBy(
                    a -> a.getHoraInicio().getHour(),
                    Collectors.counting()
                ));
        
        // Taxa de ocupação média
        double taxaOcupacaoMedia = calcularTaxaOcupacaoMedia(dataInicio, dataFim);
        
        // Tempo médio de serviço
        double tempoMedioServico = agendamentos.stream()
                .filter(a -> StatusAgendamento.FINALIZADO.equals(a.getStatus()))
                .mapToInt(a -> a.getServico().getDuracaoEstimada())
                .average()
                .orElse(0);
        
        // Serviços mais realizados
        Map<String, Long> servicosMaisRealizados = agendamentos.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .collect(Collectors.groupingBy(
                    a -> a.getServico().getNome(),
                    Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("periodo", dataInicio + " até " + dataFim);
        relatorio.put("totalAgendamentos", agendamentos.size());
        relatorio.put("agendamentosPorDiaSemana", agendamentosPorDiaSemana);
        relatorio.put("agendamentosPorHora", agendamentosPorHora);
        relatorio.put("taxaOcupacaoMedia", String.format("%.2f%%", taxaOcupacaoMedia));
        relatorio.put("tempoMedioServico", String.format("%.0f minutos", tempoMedioServico));
        relatorio.put("servicosMaisRealizados", servicosMaisRealizados);
        
        return relatorio;
    }

    /**
     * Relatório de clientes
     */
    public Map<String, Object> relatorioClientes(LocalDate dataInicio, LocalDate dataFim) {
        List<Usuario> clientes = usuarioRepository.findAll().stream()
                .filter(u -> u.getTipoUsuario() == TipoUsuario.CLIENTE)
                .collect(Collectors.toList());
        
        // Novos clientes no período
        long novosClientes = clientes.stream()
                .filter(c -> c.getCreatedAt().toLocalDate().isAfter(dataInicio) &&
                           c.getCreatedAt().toLocalDate().isBefore(dataFim))
                .count();
        
        // Top 10 clientes por valor gasto
        List<Map<String, Object>> topClientesValor = clientes.stream()
                .map(cliente -> {
                    BigDecimal totalGasto = cliente.getAgendamentos().stream()
                            .filter(a -> a.getDataAgendamento().isAfter(dataInicio) &&
                                       a.getDataAgendamento().isBefore(dataFim) &&
                                       !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                            .map(Agendamento::getValorTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    Map<String, Object> clienteInfo = new HashMap<>();
                    clienteInfo.put("id", cliente.getId());
                    clienteInfo.put("nome", cliente.getNome());
                    clienteInfo.put("email", cliente.getEmail());
                    clienteInfo.put("totalGasto", totalGasto);
                    clienteInfo.put("totalAgendamentos", cliente.getAgendamentos().size());
                    
                    return clienteInfo;
                })
                .filter(c -> ((BigDecimal) c.get("totalGasto")).compareTo(BigDecimal.ZERO) > 0)
                .sorted((c1, c2) -> ((BigDecimal) c2.get("totalGasto"))
                        .compareTo((BigDecimal) c1.get("totalGasto")))
                .limit(10)
                .collect(Collectors.toList());
        
        // Taxa de retenção (clientes que voltaram)
        long clientesQueVoltaram = clientes.stream()
                .filter(c -> c.getAgendamentos().size() > 1)
                .count();
        
        double taxaRetencao = clientes.isEmpty() ? 0 : 
                (double) clientesQueVoltaram / clientes.size() * 100;
        
        // Análise de frequência
        Map<String, Long> clientesPorFrequencia = new HashMap<>();
        clientesPorFrequencia.put("1 vez", clientes.stream()
                .filter(c -> c.getAgendamentos().size() == 1).count());
        clientesPorFrequencia.put("2-5 vezes", clientes.stream()
                .filter(c -> c.getAgendamentos().size() >= 2 && c.getAgendamentos().size() <= 5).count());
        clientesPorFrequencia.put("6-10 vezes", clientes.stream()
                .filter(c -> c.getAgendamentos().size() >= 6 && c.getAgendamentos().size() <= 10).count());
        clientesPorFrequencia.put("Mais de 10 vezes", clientes.stream()
                .filter(c -> c.getAgendamentos().size() > 10).count());
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("totalClientes", clientes.size());
        relatorio.put("novosClientes", novosClientes);
        relatorio.put("topClientesValor", topClientesValor);
        relatorio.put("taxaRetencao", String.format("%.2f%%", taxaRetencao));
        relatorio.put("clientesPorFrequencia", clientesPorFrequencia);
        
        return relatorio;
    }

    /**
     * Relatório de serviços
     */
    public Map<String, Object> relatorioServicos(LocalDate dataInicio, LocalDate dataFim) {
        List<Servico> servicos = servicoRepository.findAll();
        List<Agendamento> agendamentos = agendamentoRepository
                .findByDataAgendamentoBetween(dataInicio, dataFim);
        
        // Análise por serviço
        List<Map<String, Object>> analiseServicos = servicos.stream()
                .map(servico -> {
                    List<Agendamento> agendamentosServico = agendamentos.stream()
                            .filter(a -> a.getServico().getId().equals(servico.getId()))
                            .collect(Collectors.toList());
                    
                    long totalRealizados = agendamentosServico.stream()
                            .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                            .count();
                    
                    BigDecimal receitaTotal = agendamentosServico.stream()
                            .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                            .map(Agendamento::getValorTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    Map<String, Object> servicoInfo = new HashMap<>();
                    servicoInfo.put("id", servico.getId());
                    servicoInfo.put("nome", servico.getNome());
                    servicoInfo.put("categoria", servico.getCategoria() != null ? 
                            servico.getCategoria().getNome() : "Sem categoria");
                    servicoInfo.put("preco", servico.getPreco());
                    servicoInfo.put("duracao", servico.getDuracaoEstimada());
                    servicoInfo.put("totalRealizados", totalRealizados);
                    servicoInfo.put("receitaTotal", receitaTotal);
                    servicoInfo.put("ativo", servico.getAtivo());
                    
                    return servicoInfo;
                })
                .sorted((s1, s2) -> ((BigDecimal) s2.get("receitaTotal"))
                        .compareTo((BigDecimal) s1.get("receitaTotal")))
                .collect(Collectors.toList());
        
        // Serviços mais rentáveis
        List<Map<String, Object>> servicosMaisRentaveis = analiseServicos.stream()
                .filter(s -> ((BigDecimal) s.get("receitaTotal")).compareTo(BigDecimal.ZERO) > 0)
                .limit(5)
                .collect(Collectors.toList());
        
        // Serviços menos utilizados
        List<Map<String, Object>> servicosMenosUtilizados = analiseServicos.stream()
                .filter(s -> (Long) s.get("totalRealizados") < 5)
                .collect(Collectors.toList());
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("totalServicos", servicos.size());
        relatorio.put("servicosAtivos", servicos.stream().filter(Servico::getAtivo).count());
        relatorio.put("analiseServicos", analiseServicos);
        relatorio.put("servicosMaisRentaveis", servicosMaisRentaveis);
        relatorio.put("servicosMenosUtilizados", servicosMenosUtilizados);
        
        return relatorio;
    }

    /**
     * Relatório comparativo mensal
     */
    public Map<String, Object> relatorioComparativoMensal(Integer ano) {
        List<Map<String, Object>> dadosMensais = new ArrayList<>();
        
        for (int mes = 1; mes <= 12; mes++) {
            LocalDate inicio = LocalDate.of(ano, mes, 1);
            LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
            
            if (inicio.isAfter(LocalDate.now())) break;
            
            List<Agendamento> agendamentosMes = agendamentoRepository
                    .findByDataAgendamentoBetween(inicio, fim);
            
            Map<String, Object> dadosMes = new HashMap<>();
            dadosMes.put("mes", mes);
            dadosMes.put("nomeMes", obterNomeMes(mes));
            dadosMes.put("totalAgendamentos", agendamentosMes.size());
            dadosMes.put("receitaTotal", calcularReceitaLiquida(agendamentosMes));
            dadosMes.put("ticketMedio", calcularTicketMedio(agendamentosMes));
            dadosMes.put("taxaCancelamento", calcularTaxaCancelamento(agendamentosMes));
            
            dadosMensais.add(dadosMes);
        }
        
        // Calcular variações
        for (int i = 1; i < dadosMensais.size(); i++) {
            Map<String, Object> mesAtual = dadosMensais.get(i);
            Map<String, Object> mesAnterior = dadosMensais.get(i - 1);
            
            BigDecimal receitaAtual = (BigDecimal) mesAtual.get("receitaTotal");
            BigDecimal receitaAnterior = (BigDecimal) mesAnterior.get("receitaTotal");
            
            if (receitaAnterior.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal variacao = receitaAtual.subtract(receitaAnterior)
                        .divide(receitaAnterior, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                mesAtual.put("variacaoReceita", variacao);
            }
        }
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("ano", ano);
        relatorio.put("dadosMensais", dadosMensais);
        relatorio.put("melhorMes", obterMelhorMes(dadosMensais));
        relatorio.put("piorMes", obterPiorMes(dadosMensais));
        
        return relatorio;
    }

    // Métodos auxiliares
    
    private BigDecimal calcularReceitaBruta(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .map(Agendamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calcularReceitaLiquida(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .map(Agendamento::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calcularReceitaCancelamentos(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .filter(a -> StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .map(Agendamento::getTaxaCancelamento)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calcularTicketMedio(List<Agendamento> agendamentos) {
        List<Agendamento> finalizados = agendamentos.stream()
                .filter(a -> StatusAgendamento.FINALIZADO.equals(a.getStatus()))
                .collect(Collectors.toList());
        
        if (finalizados.isEmpty()) return BigDecimal.ZERO;
        
        BigDecimal total = calcularReceitaLiquida(finalizados);
        return total.divide(BigDecimal.valueOf(finalizados.size()), 2, RoundingMode.HALF_UP);
    }
    
    private double calcularTaxaCancelamento(List<Agendamento> agendamentos) {
        if (agendamentos.isEmpty()) return 0;
        
        long cancelados = agendamentos.stream()
                .filter(a -> StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .count();
        
        return (double) cancelados / agendamentos.size() * 100;
    }
    
    private Map<Integer, BigDecimal> calcularReceitaPorSemana(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .collect(Collectors.groupingBy(
                    a -> a.getDataAgendamento().get(java.time.temporal.WeekFields.of(Locale.getDefault()).weekOfMonth()),
                    Collectors.mapping(
                        Agendamento::getValorTotal,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
    }
    
    private Map<String, BigDecimal> calcularReceitaPorCategoria(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .filter(a -> a.getServico().getCategoria() != null)
                .collect(Collectors.groupingBy(
                    a -> a.getServico().getCategoria().getNome(),
                    Collectors.mapping(
                        Agendamento::getValorTotal,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
    }
    
    private long contarPorStatus(List<Agendamento> agendamentos, StatusAgendamento status) {
        return agendamentos.stream()
                .filter(a -> status.equals(a.getStatus()))
                .count();
    }
    
    private double calcularTaxaOcupacaoMedia(LocalDate inicio, LocalDate fim) {
        // Implementação simplificada
        return 65.5; // Retornar valor mockado por enquanto
    }
    
    private String obterNomeMes(int mes) {
        String[] meses = {"", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                         "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        return meses[mes];
    }
    
    private Map<String, Object> obterMelhorMes(List<Map<String, Object>> dadosMensais) {
        return dadosMensais.stream()
                .max(Comparator.comparing(m -> (BigDecimal) m.get("receitaTotal")))
                .orElse(null);
    }
    
    private Map<String, Object> obterPiorMes(List<Map<String, Object>> dadosMensais) {
        return dadosMensais.stream()
                .filter(m -> ((BigDecimal) m.get("receitaTotal")).compareTo(BigDecimal.ZERO) > 0)
                .min(Comparator.comparing(m -> (BigDecimal) m.get("receitaTotal")))
                .orElse(null);
    }
}