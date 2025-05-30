package com.boxpro.service;

import com.boxpro.dto.response.DashboardResponse;
import com.boxpro.entity.Agendamento;
import com.boxpro.entity.Usuario;
import com.boxpro.entity.enums.StatusAgendamento;
import com.boxpro.entity.enums.TipoUsuario;
import com.boxpro.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final VeiculoRepository veiculoRepository;
    private final CategoriaServicoRepository categoriaServicoRepository;
    private final HistoricoAgendamentoRepository historicoAgendamentoRepository;

    /**
     * Obter dashboard completo
     */
    public DashboardResponse obterDashboard(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null) dataInicio = LocalDate.now().withDayOfMonth(1);
        if (dataFim == null) dataFim = LocalDate.now();
        
        return DashboardResponse.builder()
                .resumoGeral(obterResumoGeral())
                .agendamentosPorDia(obterAgendamentosPorDia(dataInicio, dataFim))
                .servicosMaisPopulares(obterServicosMaisPopulares(10))
                .clientesMaisFrequentes(obterClientesMaisFrequentes(10))
                .estatisticasPeriodo(obterEstatisticasPeriodo(dataInicio, dataFim))
                .build();
    }

    /**
     * Obter resumo geral
     */
    public DashboardResponse.ResumoGeral obterResumoGeral() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        
        // Contar dados gerais
        Long totalClientes = usuarioRepository.countByTipo(TipoUsuario.CLIENTE);
        Long servicosAtivos = servicoRepository.countByAtivoTrue();
        Long veiculosCadastrados = veiculoRepository.count();
        
        // Agendamentos hoje
        List<Agendamento> agendamentosHoje = agendamentoRepository.findAgendamentosHoje();
        Long totalAgendamentosHoje = (long) agendamentosHoje.size();
        
        // Agendamentos do mês
        List<Agendamento> agendamentosMes = agendamentoRepository
                .findByDataAgendamentoBetween(inicioMes, hoje);
        Long totalAgendamentosMes = (long) agendamentosMes.size();
        
        // Calcular receitas
        BigDecimal receitaHoje = calcularReceita(agendamentosHoje);
        BigDecimal receitaMes = calcularReceita(agendamentosMes);
        
        return DashboardResponse.ResumoGeral.builder()
                .totalClientes(totalClientes)
                .totalAgendamentosHoje(totalAgendamentosHoje)
                .totalAgendamentosMes(totalAgendamentosMes)
                .receitaHoje(receitaHoje)
                .receitaMes(receitaMes)
                .servicosAtivos(servicosAtivos)
                .veiculosCadastrados(veiculosCadastrados)
                .build();
    }

    /**
     * Obter agendamentos por dia
     */
    public List<DashboardResponse.AgendamentosDia> obterAgendamentosPorDia(
            LocalDate dataInicio, LocalDate dataFim) {
        
        List<DashboardResponse.AgendamentosDia> resultado = new ArrayList<>();
        
        LocalDate data = dataInicio;
        while (!data.isAfter(dataFim)) {
            List<Agendamento> agendamentosDia = agendamentoRepository.findByDataAgendamento(data);
            
            Long total = (long) agendamentosDia.size();
            Long concluidos = agendamentosDia.stream()
                    .filter(a -> StatusAgendamento.FINALIZADO.equals(a.getStatus()))
                    .count();
            Long cancelados = agendamentosDia.stream()
                    .filter(a -> StatusAgendamento.CANCELADO.equals(a.getStatus()))
                    .count();
            BigDecimal receita = calcularReceita(agendamentosDia);
            
            resultado.add(DashboardResponse.AgendamentosDia.builder()
                    .data(data)
                    .total(total)
                    .concluidos(concluidos)
                    .cancelados(cancelados)
                    .receita(receita)
                    .build());
            
            data = data.plusDays(1);
        }
        
        return resultado;
    }

    /**
     * Obter serviços mais populares
     */
    public List<DashboardResponse.ServicoPopular> obterServicosMaisPopulares(Integer limite) {
        return servicoRepository.findServicosMaisUtilizados().stream()
                .limit(limite)
                .map(servico -> {
                    List<Agendamento> agendamentos = servico.getAgendamentos().stream()
                            .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                            .collect(Collectors.toList());
                    
                    Long totalAgendamentos = (long) agendamentos.size();
                    BigDecimal receitaTotal = calcularReceita(agendamentos);
                    
                    return DashboardResponse.ServicoPopular.builder()
                            .servicoId(servico.getId())
                            .nome(servico.getNome())
                            .totalAgendamentos(totalAgendamentos)
                            .receitaTotal(receitaTotal)
                            .duracaoMedia(servico.getDuracaoEstimada())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Obter clientes mais frequentes
     */
    public List<DashboardResponse.ClienteFrequente> obterClientesMaisFrequentes(Integer limite) {
        List<Usuario> clientes = usuarioRepository.findByTipoUsuario(TipoUsuario.CLIENTE);
        
        return clientes.stream()
                .map(cliente -> {
                    List<Agendamento> agendamentos = cliente.getAgendamentos();
                    Long totalAgendamentos = (long) agendamentos.size();
                    BigDecimal totalGasto = calcularReceita(agendamentos);
                    
                    LocalDate ultimoAgendamento = agendamentos.stream()
                            .map(Agendamento::getDataAgendamento)
                            .max(LocalDate::compareTo)
                            .orElse(null);
                    
                    return DashboardResponse.ClienteFrequente.builder()
                            .clienteId(cliente.getId())
                            .nome(cliente.getNome())
                            .email(cliente.getEmail())
                            .totalAgendamentos(totalAgendamentos)
                            .totalGasto(totalGasto)
                            .ultimoAgendamento(ultimoAgendamento)
                            .build();
                })
                .filter(c -> c.getTotalAgendamentos() > 0)
                .sorted(Comparator.comparing(DashboardResponse.ClienteFrequente::getTotalAgendamentos).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    /**
     * Obter estatísticas do período
     */
    public DashboardResponse.EstatisticasPeriodo obterEstatisticasPeriodo(
            LocalDate dataInicio, LocalDate dataFim) {
        
        List<Agendamento> agendamentosPeriodo = agendamentoRepository
                .findByDataAgendamentoBetween(dataInicio, dataFim);
        
        // Agrupar por status
        Map<String, Long> agendamentosPorStatus = agendamentosPeriodo.stream()
                .collect(Collectors.groupingBy(
                    a -> a.getStatus().name(),
                    Collectors.counting()
                ));
        
        // Calcular métricas financeiras
        BigDecimal receitaTotal = calcularReceita(agendamentosPeriodo);
        BigDecimal ticketMedio = calcularTicketMedio(agendamentosPeriodo);
        
        // Cancelamentos
        List<Agendamento> cancelamentos = agendamentosPeriodo.stream()
                .filter(a -> StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .collect(Collectors.toList());
        
        Long totalCancelamentos = (long) cancelamentos.size();
        BigDecimal totalTaxasCancelamento = cancelamentos.stream()
                .map(Agendamento::getTaxaCancelamento)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return DashboardResponse.EstatisticasPeriodo.builder()
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .agendamentosPorStatus(agendamentosPorStatus)
                .receitaTotal(receitaTotal)
                .ticketMedio(ticketMedio)
                .totalCancelamentos(totalCancelamentos)
                .totalTaxasCancelamento(totalTaxasCancelamento)
                .build();
    }

    /**
     * Obter agendamentos por dia da semana
     */
    public Map<LocalDate, Long> agendamentosPorDia(LocalDate dataInicio, LocalDate dataFim) {
        Map<LocalDate, Long> resultado = new LinkedHashMap<>();
        
        LocalDate data = dataInicio;
        while (!data.isAfter(dataFim)) {
            Long total = agendamentoRepository.countByStatusAndPeriodo(
                StatusAgendamento.FINALIZADO, data, data
            );
            resultado.put(data, total);
            data = data.plusDays(1);
        }
        
        return resultado;
    }

    /**
     * Obter serviços mais populares (simplificado)
     */
    public List<Map<String, Object>> servicosMaisPopulares(Integer limite) {
        return servicoRepository.findServicosMaisUtilizados().stream()
                .limit(limite)
                .map(servico -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", servico.getId());
                    map.put("nome", servico.getNome());
                    map.put("categoria", servico.getCategoria() != null ? 
                        servico.getCategoria().getNome() : "Sem categoria");
                    map.put("totalAgendamentos", servico.getAgendamentos().size());
                    map.put("preco", servico.getPreco());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obter clientes mais frequentes (simplificado)
     */
    public List<Map<String, Object>> clientesMaisFrequentes(Integer limite) {
        return usuarioRepository.findAllWithAgendamentos().stream()
                .filter(u -> TipoUsuario.CLIENTE.equals(u.getTipoUsuario()))
                .sorted((u1, u2) -> Integer.compare(
                    u2.getAgendamentos().size(), 
                    u1.getAgendamentos().size()
                ))
                .limit(limite)
                .map(usuario -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", usuario.getId());
                    map.put("nome", usuario.getNome());
                    map.put("email", usuario.getEmail());
                    map.put("totalAgendamentos", usuario.getAgendamentos().size());
                    map.put("totalVeiculos", usuario.getVeiculos().size());
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obter receita por período
     */
    public Map<String, Object> receitaPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        List<Agendamento> agendamentos = agendamentoRepository
                .findByDataAgendamentoBetween(dataInicio, dataFim);
        
        // Receita total
        BigDecimal receitaTotal = calcularReceita(agendamentos);
        
        // Receita por status
        Map<String, BigDecimal> receitaPorStatus = agendamentos.stream()
                .collect(Collectors.groupingBy(
                    a -> a.getStatus().name(),
                    Collectors.mapping(
                        Agendamento::getValorTotal,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
        
        // Receita por serviço
        Map<String, BigDecimal> receitaPorServico = agendamentos.stream()
                .filter(a -> a.getServico() != null)
                .collect(Collectors.groupingBy(
                    a -> a.getServico().getNome(),
                    Collectors.mapping(
                        Agendamento::getValorTotal,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                    )
                ));
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("receitaTotal", receitaTotal);
        resultado.put("receitaPorStatus", receitaPorStatus);
        resultado.put("receitaPorServico", receitaPorServico);
        resultado.put("ticketMedio", calcularTicketMedio(agendamentos));
        resultado.put("totalAgendamentos", agendamentos.size());
        
        return resultado;
    }

    /**
     * Obter taxa de ocupação
     */
    public Map<String, Object> taxaOcupacao(LocalDate data) {
        List<Agendamento> agendamentos = agendamentoRepository.findByDataAgendamento(data);
        
        // Assumindo 10 horas de funcionamento (8h às 18h) com slots de 30 minutos
        int totalSlots = 20; 
        
        // Calcular slots ocupados
        long minutosOcupados = agendamentos.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .mapToLong(a -> ChronoUnit.MINUTES.between(a.getHoraInicio(), a.getHoraFim()))
                .sum();
        
        int slotsOcupados = (int) (minutosOcupados / 30);
        double taxaOcupacao = (double) slotsOcupados / totalSlots * 100;
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("data", data);
        resultado.put("totalSlots", totalSlots);
        resultado.put("slotsOcupados", slotsOcupados);
        resultado.put("slotsLivres", totalSlots - slotsOcupados);
        resultado.put("taxaOcupacao", String.format("%.2f%%", taxaOcupacao));
        resultado.put("agendamentos", agendamentos.size());
        
        return resultado;
    }

    /**
     * Obter estatísticas de cancelamentos
     */
    public Map<String, Object> estatisticasCancelamentos(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null) dataInicio = LocalDate.now().minusMonths(1);
        if (dataFim == null) dataFim = LocalDate.now();
        
        List<Agendamento> cancelamentos = agendamentoRepository
                .findByDataAgendamentoBetween(dataInicio, dataFim).stream()
                .filter(a -> StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .collect(Collectors.toList());
        
        // Agrupar por motivo (simplificado - primeiras palavras)
        Map<String, Long> porMotivo = cancelamentos.stream()
                .filter(a -> a.getMotivoCancelamento() != null)
                .collect(Collectors.groupingBy(
                    a -> extrairMotivoSimplificado(a.getMotivoCancelamento()),
                    Collectors.counting()
                ));
        
        // Taxas cobradas
        BigDecimal totalTaxas = cancelamentos.stream()
                .map(Agendamento::getTaxaCancelamento)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Taxa média de cancelamento
        double taxaCancelamento = 0;
        if (!agendamentoRepository.findByDataAgendamentoBetween(dataInicio, dataFim).isEmpty()) {
            taxaCancelamento = (double) cancelamentos.size() / 
                agendamentoRepository.findByDataAgendamentoBetween(dataInicio, dataFim).size() * 100;
        }
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("totalCancelamentos", cancelamentos.size());
        resultado.put("porMotivo", porMotivo);
        resultado.put("totalTaxasCobradas", totalTaxas);
        resultado.put("taxaCancelamento", String.format("%.2f%%", taxaCancelamento));
        
        return resultado;
    }

    /**
     * Obter horários de pico
     */
    public Map<String, Object> horariosDePico() {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        List<Agendamento> agendamentosMes = agendamentoRepository
                .findByDataAgendamentoBetween(inicioMes, LocalDate.now());
        
        // Agrupar por hora do dia
        Map<Integer, Long> porHora = agendamentosMes.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .collect(Collectors.groupingBy(
                    a -> a.getHoraInicio().getHour(),
                    Collectors.counting()
                ));
        
        // Encontrar horário mais movimentado
        Map.Entry<Integer, Long> horarioPico = porHora.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("distribuicaoPorHora", porHora);
        resultado.put("horarioPico", horarioPico != null ? 
            horarioPico.getKey() + ":00" : "N/A");
        resultado.put("agendamentosNoPico", horarioPico != null ? 
            horarioPico.getValue() : 0);
        
        return resultado;
    }

    /**
     * Gerar relatório mensal
     */
    public Map<String, Object> relatorioMensal(Integer mes, Integer ano) {
        LocalDate inicio = LocalDate.of(ano, mes, 1);
        LocalDate fim = inicio.withDayOfMonth(inicio.lengthOfMonth());
        
        Map<String, Object> relatorio = new HashMap<>();
        
        relatorio.put("periodo", mes + "/" + ano);
        relatorio.put("resumoGeral", obterResumoGeral());
        relatorio.put("estatisticasPeriodo", obterEstatisticasPeriodo(inicio, fim));
        relatorio.put("servicosMaisPopulares", servicosMaisPopulares(5));
        relatorio.put("clientesMaisFrequentes", clientesMaisFrequentes(5));
        relatorio.put("taxaOcupacaoMedia", calcularTaxaOcupacaoMedia(inicio, fim));
        relatorio.put("receitaPorSemana", calcularReceitaPorSemana(inicio, fim));
        
        return relatorio;
    }

    // Métodos auxiliares
    
    private BigDecimal calcularReceita(List<Agendamento> agendamentos) {
        return agendamentos.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .map(Agendamento::getValorTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calcularTicketMedio(List<Agendamento> agendamentos) {
        List<Agendamento> agendamentosValidos = agendamentos.stream()
                .filter(a -> !StatusAgendamento.CANCELADO.equals(a.getStatus()))
                .filter(a -> a.getValorTotal() != null)
                .collect(Collectors.toList());
        
        if (agendamentosValidos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = calcularReceita(agendamentosValidos);
        return total.divide(
            BigDecimal.valueOf(agendamentosValidos.size()), 
            2, 
            RoundingMode.HALF_UP
        );
    }
    
    private String extrairMotivoSimplificado(String motivo) {
        if (motivo == null || motivo.isEmpty()) return "Não informado";
        
        String[] palavras = motivo.toLowerCase().split("\\s+");
        if (palavras.length > 0) {
            if (palavras[0].contains("pessoal")) return "Motivo pessoal";
            if (palavras[0].contains("financ")) return "Motivo financeiro";
            if (palavras[0].contains("tempo") || palavras[0].contains("clima")) return "Condições climáticas";
            if (palavras[0].contains("saúde") || palavras[0].contains("médic")) return "Questões de saúde";
        }
        
        return "Outros";
    }
    
    private double calcularTaxaOcupacaoMedia(LocalDate inicio, LocalDate fim) {
        long dias = ChronoUnit.DAYS.between(inicio, fim) + 1;
        double somaOcupacao = 0;
        
        LocalDate data = inicio;
        while (!data.isAfter(fim)) {
            Map<String, Object> ocupacao = taxaOcupacao(data);
            String taxaStr = (String) ocupacao.get("taxaOcupacao");
            double taxa = Double.parseDouble(taxaStr.replace("%", ""));
            somaOcupacao += taxa;
            data = data.plusDays(1);
        }
        
        return somaOcupacao / dias;
    }
    
    private Map<Integer, BigDecimal> calcularReceitaPorSemana(LocalDate inicio, LocalDate fim) {
        Map<Integer, BigDecimal> receitaPorSemana = new HashMap<>();
        
        LocalDate data = inicio;
        int semana = 1;
        
        while (!data.isAfter(fim)) {
            LocalDate fimSemana = data.plusDays(6);
            if (fimSemana.isAfter(fim)) fimSemana = fim;
            
            List<Agendamento> agendamentosSemana = agendamentoRepository
                    .findByDataAgendamentoBetween(data, fimSemana);
            
            BigDecimal receita = calcularReceita(agendamentosSemana);
            receitaPorSemana.put(semana, receita);
            
            data = fimSemana.plusDays(1);
            semana++;
        }
        
        return receitaPorSemana;
    }
}