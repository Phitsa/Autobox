package com.boxpro.service;

import com.boxpro.dto.request.EmpresaRequestDTO;
import com.boxpro.dto.response.EmpresaResponseDTO;
import com.boxpro.entity.Empresa;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    /**
     * Busca a empresa ativa (deve existir apenas uma)
     */
    public EmpresaResponseDTO getEmpresa() {
        Empresa empresa = empresaRepository.findActiveEmpresa()
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        
        return new EmpresaResponseDTO(empresa);
    }
    
    /**
     * Cria uma nova empresa (apenas se não existir nenhuma ativa)
     */
    @Transactional
    public EmpresaResponseDTO createEmpresa(EmpresaRequestDTO requestDTO) {
        // Verificar se já existe uma empresa ativa
        if (empresaRepository.countActiveEmpresas() > 0) {
            throw new BusinessException("Já existe uma empresa cadastrada. Use a função de atualização.");
        }
        
        // Verificar CNPJ único se fornecido
        if (requestDTO.getCnpj() != null && !requestDTO.getCnpj().trim().isEmpty()) {
            if (empresaRepository.findByCnpj(requestDTO.getCnpj()).isPresent()) {
                throw new BusinessException("CNPJ já cadastrado");
            }
        }
        
        Empresa empresa = createEmpresaFromDTO(requestDTO);
        empresa = empresaRepository.save(empresa);
        
        return new EmpresaResponseDTO(empresa);
    }
    
    /**
     * Atualiza os dados da empresa existente
     */
    @Transactional
    public EmpresaResponseDTO updateEmpresa(Long id, EmpresaRequestDTO requestDTO) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        
        // Verificar CNPJ único se fornecido e diferente do atual
        if (requestDTO.getCnpj() != null && !requestDTO.getCnpj().trim().isEmpty()) {
            if (empresaRepository.existsByCnpjAndIdNot(requestDTO.getCnpj(), id)) {
                throw new BusinessException("CNPJ já cadastrado para outra empresa");
            }
        }
        
        updateEmpresaFromDTO(empresa, requestDTO);
        empresa = empresaRepository.save(empresa);
        
        return new EmpresaResponseDTO(empresa);
    }
    
    /**
     * Verifica se existe empresa cadastrada
     */
    public boolean existeEmpresa() {
        return empresaRepository.countActiveEmpresas() > 0;
    }
    
    /**
     * Método auxiliar para criar empresa a partir do DTO
     */
    private Empresa createEmpresaFromDTO(EmpresaRequestDTO dto) {
        Empresa empresa = new Empresa();
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setDescricao(dto.getDescricao());
        empresa.setCnpj(dto.getCnpj());
        empresa.setInscricaoEstadual(dto.getInscricaoEstadual());
        empresa.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        empresa.setEndereco(dto.getEndereco());
        empresa.setCep(dto.getCep());
        empresa.setCidade(dto.getCidade());
        empresa.setEstado(dto.getEstado());
        empresa.setNumero(dto.getNumero());
        empresa.setComplemento(dto.getComplemento());
        empresa.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        
        return empresa;
    }
    
    /**
     * Método auxiliar para atualizar empresa a partir do DTO
     */
    private void updateEmpresaFromDTO(Empresa empresa, EmpresaRequestDTO dto) {
        empresa.setNomeFantasia(dto.getNomeFantasia());
        empresa.setRazaoSocial(dto.getRazaoSocial());
        empresa.setDescricao(dto.getDescricao());
        empresa.setCnpj(dto.getCnpj());
        empresa.setInscricaoEstadual(dto.getInscricaoEstadual());
        empresa.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        empresa.setEndereco(dto.getEndereco());
        empresa.setCep(dto.getCep());
        empresa.setCidade(dto.getCidade());
        empresa.setEstado(dto.getEstado());
        empresa.setNumero(dto.getNumero());
        empresa.setComplemento(dto.getComplemento());
        
        if (dto.getAtivo() != null) {
            empresa.setAtivo(dto.getAtivo());
        }
    }
}
