package com.boxpro.service;

import com.boxpro.entity.Usuario;
import com.boxpro.entity.Veiculo;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final UsuarioService usuarioService;

    /**
     * Criar novo veículo
     */
    public Veiculo criar(Veiculo veiculo, Integer usuarioId) {
        log.info("Criando novo veículo para usuário ID: {}", usuarioId);
        
        // Buscar usuário
        Usuario usuario = usuarioService.buscarPorId(usuarioId);
        veiculo.setUsuario(usuario);
        
        // Validar placa
        validarPlaca(veiculo.getPlaca());
        
        // Verificar se placa já existe
        if (veiculoRepository.existsByPlaca(veiculo.getPlaca())) {
            throw new BusinessException("Veículo já cadastrado com a placa: " + veiculo.getPlaca());
        }
        
        // Validar ano
        validarAno(veiculo.getAno());
        
        // Padronizar placa (uppercase)
        veiculo.setPlaca(veiculo.getPlaca().toUpperCase());
        
        return veiculoRepository.save(veiculo);
    }

    /**
     * Buscar veículo por ID
     */
    @Transactional(readOnly = true)
    public Veiculo buscarPorId(Integer id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com ID: " + id));
    }

    /**
     * Buscar veículo por placa
     */
    @Transactional(readOnly = true)
    public Veiculo buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com placa: " + placa));
    }

    /**
     * Listar todos os veículos
     */
    @Transactional(readOnly = true)
    public List<Veiculo> listarTodos() {
        return veiculoRepository.findAll();
    }

    /**
     * Listar veículos por usuário
     */
    @Transactional(readOnly = true)
    public List<Veiculo> listarPorUsuario(Integer usuarioId) {
        return veiculoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Buscar veículos por marca
     */
    @Transactional(readOnly = true)
    public List<Veiculo> buscarPorMarca(String marca) {
        return veiculoRepository.findByMarcaIgnoreCase(marca);
    }

    /**
     * Buscar veículos por modelo
     */
    @Transactional(readOnly = true)
    public List<Veiculo> buscarPorModelo(String modelo) {
        return veiculoRepository.findByModeloContainingIgnoreCase(modelo);
    }

    /**
     * Listar marcas distintas
     */
    @Transactional(readOnly = true)
    public List<String> listarMarcasDistintas() {
        return veiculoRepository.findDistinctMarcas();
    }

    /**
     * Atualizar veículo
     */
    public Veiculo atualizar(Integer id, Veiculo veiculoAtualizado) {
        log.info("Atualizando veículo ID: {}", id);
        
        Veiculo veiculo = buscarPorId(id);
        
        // Verificar se placa mudou
        if (!veiculo.getPlaca().equals(veiculoAtualizado.getPlaca().toUpperCase())) {
            validarPlaca(veiculoAtualizado.getPlaca());
            
            if (veiculoRepository.existsByPlaca(veiculoAtualizado.getPlaca())) {
                throw new BusinessException("Veículo já cadastrado com a placa: " + veiculoAtualizado.getPlaca());
            }
            
            veiculo.setPlaca(veiculoAtualizado.getPlaca().toUpperCase());
        }
        
        // Atualizar campos
        veiculo.setModelo(veiculoAtualizado.getModelo());
        veiculo.setMarca(veiculoAtualizado.getMarca());
        veiculo.setCor(veiculoAtualizado.getCor());
        
        // Validar e atualizar ano
        if (!veiculo.getAno().equals(veiculoAtualizado.getAno())) {
            validarAno(veiculoAtualizado.getAno());
            veiculo.setAno(veiculoAtualizado.getAno());
        }
        
        return veiculoRepository.save(veiculo);
    }

    /**
     * Deletar veículo
     */
    public void deletar(Integer id) {
        log.info("Deletando veículo ID: {}", id);
        
        Veiculo veiculo = buscarPorId(id);
        
        // Verificar se veículo tem agendamentos
        if (!veiculo.getAgendamentos().isEmpty()) {
            throw new BusinessException("Veículo possui agendamentos e não pode ser deletado");
        }
        
        veiculoRepository.delete(veiculo);
    }

    /**
     * Verificar se usuário é dono do veículo
     */
    @Transactional(readOnly = true)
    public boolean usuarioEhDono(Integer veiculoId, Integer usuarioId) {
        Veiculo veiculo = buscarPorId(veiculoId);
        return veiculo.getUsuario().getId().equals(usuarioId);
    }

    /**
     * Contar veículos do usuário
     */
    @Transactional(readOnly = true)
    public Long contarVeiculosPorUsuario(Integer usuarioId) {
        return veiculoRepository.countByUsuarioId(usuarioId);
    }

    /**
     * Validar formato da placa
     */
    private void validarPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new BusinessException("Placa é obrigatória");
        }
        
        // Remover espaços e converter para uppercase
        placa = placa.trim().toUpperCase();
        
        // Validar formato brasileiro (ABC1234 ou ABC1D23 - Mercosul)
        if (!placa.matches("[A-Z]{3}[0-9]{1}[A-Z0-9]{1}[0-9]{2}")) {
            throw new BusinessException("Formato de placa inválido. Use o formato ABC1234 ou ABC1D23 (Mercosul)");
        }
    }

    /**
     * Validar ano do veículo
     */
    private void validarAno(Year ano) {
        if (ano == null) {
            throw new BusinessException("Ano é obrigatório");
        }
        
        Year anoAtual = Year.now();
        Year anoMinimo = Year.of(1950);
        Year anoMaximo = anoAtual.plusYears(1); // Permite ano seguinte
        
        if (ano.isBefore(anoMinimo) || ano.isAfter(anoMaximo)) {
            throw new BusinessException("Ano deve estar entre " + anoMinimo + " e " + anoMaximo);
        }
    }
}