package com.boxpro.service;

import com.boxpro.entity.CategoriaServico;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.CategoriaServicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoriaServicoService {

    private final CategoriaServicoRepository categoriaServicoRepository;

    /**
     * Criar nova categoria
     */
    public CategoriaServico criar(CategoriaServico categoria) {
        log.info("Criando nova categoria: {}", categoria.getNome());
        
        // Validar se nome já existe
        if (categoriaServicoRepository.existsByNome(categoria.getNome())) {
            throw new BusinessException("Categoria já existe com o nome: " + categoria.getNome());
        }
        
        return categoriaServicoRepository.save(categoria);
    }

    /**
     * Buscar categoria por ID
     */
    @Transactional(readOnly = true)
    public CategoriaServico buscarPorId(Integer id) {
        return categoriaServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com ID: " + id));
    }

    /**
     * Buscar categoria por nome
     */
    @Transactional(readOnly = true)
    public CategoriaServico buscarPorNome(String nome) {
        return categoriaServicoRepository.findByNome(nome)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + nome));
    }

    /**
     * Listar todas as categorias
     */
    @Transactional(readOnly = true)
    public List<CategoriaServico> listarTodas() {
        return categoriaServicoRepository.findAllByOrderByNomeAsc();
    }

    /**
     * Listar categorias com serviços
     */
    @Transactional(readOnly = true)
    public List<CategoriaServico> listarComServicos() {
        return categoriaServicoRepository.findAllWithServicos();
    }

    /**
     * Buscar categorias por nome (contém)
     */
    @Transactional(readOnly = true)
    public List<CategoriaServico> buscarPorNomeContendo(String nome) {
        return categoriaServicoRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Atualizar categoria
     */
    public CategoriaServico atualizar(Integer id, CategoriaServico categoriaAtualizada) {
        log.info("Atualizando categoria ID: {}", id);
        
        CategoriaServico categoria = buscarPorId(id);
        
        // Verificar se nome mudou e já existe
        if (!categoria.getNome().equals(categoriaAtualizada.getNome()) 
            && categoriaServicoRepository.existsByNome(categoriaAtualizada.getNome())) {
            throw new BusinessException("Categoria já existe com o nome: " + categoriaAtualizada.getNome());
        }
        
        // Atualizar campos
        categoria.setNome(categoriaAtualizada.getNome());
        categoria.setDescricao(categoriaAtualizada.getDescricao());
        
        return categoriaServicoRepository.save(categoria);
    }

    /**
     * Deletar categoria
     */
    public void deletar(Integer id) {
        log.info("Deletando categoria ID: {}", id);
        
        CategoriaServico categoria = buscarPorId(id);
        
        // Verificar se categoria tem serviços
        if (!categoria.getServicos().isEmpty()) {
            throw new BusinessException("Categoria possui serviços vinculados e não pode ser deletada");
        }
        
        categoriaServicoRepository.delete(categoria);
    }

    /**
     * Contar categorias com serviços ativos
     */
    @Transactional(readOnly = true)
    public Long contarCategoriasComServicosAtivos() {
        return categoriaServicoRepository.countCategoriasComServicosAtivos();
    }

    /**
     * Verificar se categoria tem serviços
     */
    @Transactional(readOnly = true)
    public boolean temServicos(Integer categoriaId) {
        CategoriaServico categoria = buscarPorId(categoriaId);
        return !categoria.getServicos().isEmpty();
    }
}