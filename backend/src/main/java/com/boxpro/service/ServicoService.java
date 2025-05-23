package com.boxpro.service;

import com.boxpro.entity.CategoriaServico;
import com.boxpro.entity.Servico;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final CategoriaServicoService categoriaServicoService;

    /**
     * Criar novo serviço
     */
    public Servico criar(Servico servico) {
        log.info("Criando novo serviço: {}", servico.getNome());
        
        // Validar se nome já existe
        if (servicoRepository.findByNome(servico.getNome()).isPresent()) {
            throw new BusinessException("Serviço já existe com o nome: " + servico.getNome());
        }
        
        // Validar categoria
        if (servico.getCategoria() != null && servico.getCategoria().getId() != null) {
            CategoriaServico categoria = categoriaServicoService.buscarPorId(servico.getCategoria().getId());
            servico.setCategoria(categoria);
        }
        
        // Validações de negócio
        validarServico(servico);
        
        // Definir como ativo por padrão
        if (servico.getAtivo() == null) {
            servico.setAtivo(true);
        }
        
        return servicoRepository.save(servico);
    }

    /**
     * Buscar serviço por ID
     */
    @Transactional(readOnly = true)
    public Servico buscarPorId(Integer id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Serviço não encontrado com ID: " + id));
    }

    /**
     * Listar todos os serviços
     */
    @Transactional(readOnly = true)
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    /**
     * Listar serviços ativos
     */
    @Transactional(readOnly = true)
    public List<Servico> listarAtivos() {
        return servicoRepository.findByAtivoTrue();
    }

    /**
     * Listar serviços por categoria
     */
    @Transactional(readOnly = true)
    public List<Servico> listarPorCategoria(Integer categoriaId) {
        return servicoRepository.findByCategoriaId(categoriaId);
    }

    /**
     * Buscar serviços por nome
     */
    @Transactional(readOnly = true)
    public List<Servico> buscarPorNome(String nome, Boolean apenasAtivos) {
        if (apenasAtivos) {
            return servicoRepository.findByNomeContainingIgnoreCaseAndAtivo(nome, true);
        }
        return servicoRepository.findByNomeContainingIgnoreCaseAndAtivo(nome, null);
    }

    /**
     * Buscar serviços por faixa de preço
     */
    @Transactional(readOnly = true)
    public List<Servico> buscarPorFaixaPreco(BigDecimal precoMin, BigDecimal precoMax) {
        return servicoRepository.findByPrecoBetweenAndAtivoTrue(precoMin, precoMax);
    }

    /**
     * Buscar serviços rápidos (duração menor que X minutos)
     */
    @Transactional(readOnly = true)
    public List<Servico> buscarServicosRapidos(Integer duracaoMaxima) {
        return servicoRepository.findByDuracaoEstimadaLessThanEqualAndAtivoTrue(duracaoMaxima);
    }

    /**
     * Listar serviços mais utilizados
     */
    @Transactional(readOnly = true)
    public List<Servico> listarMaisUtilizados() {
        return servicoRepository.findServicosMaisUtilizados();
    }

    /**
     * Atualizar serviço
     */
    public Servico atualizar(Integer id, Servico servicoAtualizado) {
        log.info("Atualizando serviço ID: {}", id);
        
        Servico servico = buscarPorId(id);
        
        // Verificar se nome mudou e já existe
        if (!servico.getNome().equals(servicoAtualizado.getNome()) 
            && servicoRepository.findByNome(servicoAtualizado.getNome()).isPresent()) {
            throw new BusinessException("Serviço já existe com o nome: " + servicoAtualizado.getNome());
        }
        
        // Atualizar campos
        servico.setNome(servicoAtualizado.getNome());
        servico.setDescricao(servicoAtualizado.getDescricao());
        servico.setPreco(servicoAtualizado.getPreco());
        servico.setDuracaoEstimada(servicoAtualizado.getDuracaoEstimada());
        
        // Atualizar categoria se fornecida
        if (servicoAtualizado.getCategoria() != null && servicoAtualizado.getCategoria().getId() != null) {
            CategoriaServico categoria = categoriaServicoService.buscarPorId(servicoAtualizado.getCategoria().getId());
            servico.setCategoria(categoria);
        }
        
        // Validações
        validarServico(servico);
        
        return servicoRepository.save(servico);
    }

    /**
     * Ativar/Desativar serviço
     */
    public Servico alterarStatus(Integer id, boolean ativo) {
        log.info("Alterando status do serviço ID: {} para: {}", id, ativo);
        
        Servico servico = buscarPorId(id);
        
        // Verificar se pode desativar
        if (!ativo && !servico.getAgendamentos().isEmpty()) {
            // Verificar se tem agendamentos futuros
            boolean temAgendamentosFuturos = servico.getAgendamentos().stream()
                    .anyMatch(a -> a.getDataAgendamento().isAfter(java.time.LocalDate.now()));
            
            if (temAgendamentosFuturos) {
                throw new BusinessException("Serviço possui agendamentos futuros e não pode ser desativado");
            }
        }
        
        servico.setAtivo(ativo);
        return servicoRepository.save(servico);
    }

    /**
     * Deletar serviço
     */
    public void deletar(Integer id) {
        log.info("Deletando serviço ID: {}", id);
        
        Servico servico = buscarPorId(id);
        
        // Verificar se serviço tem agendamentos
        if (!servico.getAgendamentos().isEmpty()) {
            throw new BusinessException("Serviço possui agendamentos e não pode ser deletado");
        }
        
        servicoRepository.delete(servico);
    }

    /**
     * Validar dados do serviço
     */
    private void validarServico(Servico servico) {
        if (servico.getPreco() == null || servico.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço deve ser maior que zero");
        }
        
        if (servico.getDuracaoEstimada() == null || servico.getDuracaoEstimada() <= 0) {
            throw new BusinessException("Duração estimada deve ser maior que zero");
        }
        
        if (servico.getDuracaoEstimada() > 480) { // 8 horas
            throw new BusinessException("Duração estimada não pode ser maior que 8 horas");
        }
    }
}