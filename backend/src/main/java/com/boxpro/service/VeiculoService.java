package com.boxpro.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.boxpro.dto.request.VeiculoRequestDTO;
import com.boxpro.dto.response.VeiculoResponseDTO;
import com.boxpro.entity.Usuario;
import com.boxpro.entity.Veiculo;
import com.boxpro.mapper.VeiculoMapper;
import com.boxpro.repository.UsuarioRepository;
import com.boxpro.repository.VeiculoRepository;

@Service
public class VeiculoService {
    
    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public VeiculoResponseDTO adicionarVeiculo(VeiculoRequestDTO dto) {
        System.out.println("DTO completo: " + dto);
        System.out.println("Cliente recebido aqui ó:" + dto.getClienteId());
        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Veiculo veiculo = VeiculoMapper.toEntity(dto, cliente);
        Veiculo salvo = veiculoRepository.save(veiculo);
        return VeiculoMapper.toDTO(salvo);
    }

    public VeiculoResponseDTO buscarVeiculoPorPlaca(String placa) {
        Veiculo veiculo = veiculoRepository.findByPlaca(placa)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado com a placa: " + placa));
        return VeiculoMapper.toDTO(veiculo);
    }

    public List<VeiculoResponseDTO> listarVeiculosPorCliente(Long clienteId) {
        List<Veiculo> veiculos = veiculoRepository.findByClienteId(clienteId);
        return veiculos.stream()
            .map(VeiculoMapper::toDTO)
            .collect(Collectors.toList());
    }

    public List<VeiculoResponseDTO> listarTodosVeiculos() {
        return veiculoRepository.findAll().stream()
            .map(VeiculoMapper::toDTO)
            .collect(Collectors.toList());
    }

    public boolean placaExiste(String placa) {
        return veiculoRepository.findByPlaca(placa).isPresent();
    }

    public void removerVeiculo(Long id) {
        if (!veiculoRepository.existsById(id)) {
            throw new RuntimeException("Veículo não encontrado para remoção");
        }
        veiculoRepository.deleteById(id);
    }

    public Page<VeiculoResponseDTO> listarVeiculosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Veiculo> veiculosPage = veiculoRepository.findAll(pageable);
        return veiculosPage.map(VeiculoMapper::toDTO);
    }

    public Page<VeiculoResponseDTO> listarVeiculosPorClientePaginados(Long clienteId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Veiculo> veiculosPage = veiculoRepository.findByClienteId(clienteId, pageable);
        return veiculosPage.map(VeiculoMapper::toDTO);
    }

    public VeiculoResponseDTO editarVeiculo(Long id, VeiculoRequestDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado para edição"));

        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        veiculo.setMarca(dto.getMarca());
        veiculo.setModelo(dto.getModelo());
        veiculo.setAno(dto.getAno());
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setCor(dto.getCor());
        veiculo.setCliente(cliente);

        Veiculo salvo = veiculoRepository.save(veiculo);
        return VeiculoMapper.toDTO(salvo);
    }
}
