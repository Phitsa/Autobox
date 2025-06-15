package com.boxpro.service;

import com.boxpro.entity.Servico;
import com.boxpro.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {
    
    @Autowired
    private ServicoRepository servicoRepository;
    
    // Método paginado
    public Page<Servico> listarServicosPaginados(Pageable pageable) {
        return servicoRepository.findAll(pageable);
    }
    
    // Método para listar todos (para estatísticas)
    public List<Servico> listarServicos() {
        return servicoRepository.findAll();
    }
    
    public List<Servico> listarServicosAtivos() {
        return servicoRepository.findByAtivoTrue();
    }
    
    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepository.findById(id);
    }
    
    public Servico criarServico(Servico servico) {
        // Validações podem ser adicionadas aqui
        if (servico.getNome() == null || servico.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do serviço é obrigatório");
        }
        if (servico.getPreco() == null) {
            throw new RuntimeException("Preço do serviço é obrigatório");
        }
        if (servico.getCategoriaId() == null) {
            throw new RuntimeException("Categoria do serviço é obrigatória");
        }
        
        return servicoRepository.save(servico);
    }
    
    public Servico atualizarServico(Long id, Servico servicoAtualizado) {
        Optional<Servico> servicoExistente = servicoRepository.findById(id);
        
        if (servicoExistente.isEmpty()) {
            throw new RuntimeException("Serviço não encontrado com ID: " + id);
        }
        
        Servico servico = servicoExistente.get();
        
        // Validações
        if (servicoAtualizado.getNome() == null || servicoAtualizado.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do serviço é obrigatório");
        }
        if (servicoAtualizado.getPreco() == null) {
            throw new RuntimeException("Preço do serviço é obrigatório");
        }
        if (servicoAtualizado.getCategoriaId() == null) {
            throw new RuntimeException("Categoria do serviço é obrigatória");
        }
        
        // Atualizar campos
        servico.setNome(servicoAtualizado.getNome());
        servico.setDescricao(servicoAtualizado.getDescricao());
        servico.setPreco(servicoAtualizado.getPreco());
        servico.setDuracaoEstimada(servicoAtualizado.getDuracaoEstimada());
        servico.setCategoriaId(servicoAtualizado.getCategoriaId());
        servico.setAtivo(servicoAtualizado.getAtivo());
        
        return servicoRepository.save(servico);
    }
    
    public void deletarServico(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado com ID: " + id);
        }
        servicoRepository.deleteById(id);
    }
    
    public List<Servico> buscarPorNome(String nome) {
        return servicoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<Servico> buscarPorCategoria(Long categoriaId) {
        return servicoRepository.findByCategoriaId(categoriaId);
    }
}