package com.boxpro.service;

import com.boxpro.entity.CategoriaServico;
import com.boxpro.repository.CategoriaServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServicoService {
    
    @Autowired
    private CategoriaServicoRepository categoriaRepository;
    
    // Método paginado
    public Page<CategoriaServico> listarCategoriasPaginadas(Pageable pageable) {
        return categoriaRepository.findAll(pageable);
    }
    
    // Método para listar todas (para estatísticas)
    public List<CategoriaServico> listarCategorias() {
        return categoriaRepository.findAll();
    }
    
    public Optional<CategoriaServico> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }
    
    public CategoriaServico criarCategoria(CategoriaServico categoria) {
        // Validações
        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome da categoria é obrigatório");
        }
        
        if (categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())) {
            throw new RuntimeException("Já existe uma categoria com este nome");
        }
        return categoriaRepository.save(categoria);
    }
    
    public CategoriaServico atualizarCategoria(Long id, CategoriaServico categoria) {
        return categoriaRepository.findById(id)
            .map(categoriaExistente -> {
                // Validações
                if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
                    throw new RuntimeException("Nome da categoria é obrigatório");
                }
                
                // Verifica se o nome já existe em outra categoria
                if (!categoriaExistente.getNome().equalsIgnoreCase(categoria.getNome()) && 
                    categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())) {
                    throw new RuntimeException("Já existe uma categoria com este nome");
                }
                
                categoriaExistente.setNome(categoria.getNome());
                categoriaExistente.setDescricao(categoria.getDescricao());
                return categoriaRepository.save(categoriaExistente);
            })
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }
    
    public void deletarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada");
        }
        categoriaRepository.deleteById(id);
    }
    
    public List<CategoriaServico> buscarPorNome(String nome) {
        return categoriaRepository.findByNomeContainingIgnoreCase(nome);
    }
}