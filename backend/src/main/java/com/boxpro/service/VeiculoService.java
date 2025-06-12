package com.boxpro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boxpro.entity.Veiculo;
import com.boxpro.repository.VeiculoRepository;

@Service
public class VeiculoService {
    
    @Autowired
    private VeiculoRepository veiculoRepository;

    public Veiculo adicionarVeiculo(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public Veiculo buscarVeiculoPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com a placa: " + placa));
    }

    public List<Veiculo> listarVeiculosPorCliente(Long clienteId) {
        return veiculoRepository.findByClienteId(clienteId);
    }
    
    
    public void removerVeiculo(Long id) {
        if (!veiculoRepository.existsById(id)) {
            throw new RuntimeException("Veículo não encontrado para remoção");
        }
        veiculoRepository.deleteById(id);
    }

    public List<Veiculo> listarTodosVeiculos() {
        return veiculoRepository.findAll();
    }

    public boolean placaExiste(String placa) {
        return veiculoRepository.findByPlaca(placa).isPresent();
    }
}
