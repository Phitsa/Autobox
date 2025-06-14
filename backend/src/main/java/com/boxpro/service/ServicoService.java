package com.boxpro.service;

import com.boxpro.entity.Servico;
import com.boxpro.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {
    
    @Autowired
    private ServicoRepository servicoRepository;
    
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
        return servicoRepository.save(servico);
    }
    
    public Servico atualizarServico(Long id, Servico servico) {
        return servicoRepository.findById(id)
            .map(servicoExistente -> {
                servicoExistente.setCategoriaId(servico.getCategoriaId());
                servicoExistente.setNome(servico.getNome());
                servicoExistente.setDescricao(servico.getDescricao());
                servicoExistente.setPreco(servico.getPreco());
                servicoExistente.setDuracaoEstimada(servico.getDuracaoEstimada());
                servicoExistente.setAtivo(servico.getAtivo());
                return servicoRepository.save(servicoExistente);
            })
            .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }
    
    public void deletarServico(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado");
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