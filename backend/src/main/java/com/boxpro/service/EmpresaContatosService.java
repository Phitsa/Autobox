package com.boxpro.service;

import com.boxpro.dto.request.EmpresaContatosRequestDTO;
import com.boxpro.dto.response.EmpresaContatosResponseDTO;
import com.boxpro.entity.Empresa;
import com.boxpro.entity.EmpresaContatos;
import com.boxpro.entity.EmpresaContatos.TipoContato;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.EmpresaContatosRepository;
import com.boxpro.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaContatosService {
    
    @Autowired
    private EmpresaContatosRepository contatosRepository;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    /**
     * Buscar todos os contatos de uma empresa
     */
    public List<EmpresaContatosResponseDTO> getContatosByEmpresa(Long empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }
        
        List<EmpresaContatos> contatos = contatosRepository.findByEmpresaIdAndAtivoTrue(empresaId);
        return contatos.stream()
                .map(EmpresaContatosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar contato específico por ID
     */
    public EmpresaContatosResponseDTO getContatoById(Long id) {
        EmpresaContatos contato = contatosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));
        
        return new EmpresaContatosResponseDTO(contato);
    }
    
    /**
     * Buscar contatos por tipo
     */
    public List<EmpresaContatosResponseDTO> getContatosByEmpresaAndTipo(Long empresaId, TipoContato tipoContato) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }
        
        List<EmpresaContatos> contatos = contatosRepository.findByEmpresaIdAndTipoContatoAndAtivoTrue(empresaId, tipoContato);
        return contatos.stream()
                .map(EmpresaContatosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar contatos principais de uma empresa
     */
    public List<EmpresaContatosResponseDTO> getContatosPrincipais(Long empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }
        
        List<EmpresaContatos> contatos = contatosRepository.findContatosPrincipaisByEmpresaId(empresaId);
        return contatos.stream()
                .map(EmpresaContatosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Criar novo contato
     */
    @Transactional
    public EmpresaContatosResponseDTO createContato(EmpresaContatosRequestDTO requestDTO) {
        // Verificar se empresa existe
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        
        // Validar formato do contato
        validarFormatoContato(requestDTO);
        
        // Verificar se já existe contato idêntico
        if (contatosRepository.existsByEmpresaIdAndTipoContatoAndValorAndAtivoTrue(
                requestDTO.getEmpresaId(), requestDTO.getTipoContato(), requestDTO.getValor())) {
            throw new BusinessException("Já existe um contato " + requestDTO.getTipoContato().getNome() + 
                                      " com este valor cadastrado");
        }
        
        EmpresaContatos contato = createContatoFromDTO(empresa, requestDTO);
        
        // Se for definido como principal, remover principal dos outros do mesmo tipo
        if (requestDTO.getPrincipal() != null && requestDTO.getPrincipal()) {
            contatosRepository.removerTodosPrincipaisPorTipo(empresa.getId(), requestDTO.getTipoContato());
        }
        
        contato = contatosRepository.save(contato);
        
        return new EmpresaContatosResponseDTO(contato);
    }
    
    /**
     * Atualizar contato existente
     */
    @Transactional
    public EmpresaContatosResponseDTO updateContato(Long id, EmpresaContatosRequestDTO requestDTO) {
        EmpresaContatos contato = contatosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));
        
        // Verificar se empresa existe
        if (!empresaRepository.existsById(requestDTO.getEmpresaId())) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }
        
        // Validar formato do contato
        validarFormatoContato(requestDTO);
        
        // Verificar se já existe contato idêntico (exceto o atual)
        if (contatosRepository.existsByEmpresaIdAndTipoContatoAndValorAndIdNotAndAtivoTrue(
                requestDTO.getEmpresaId(), requestDTO.getTipoContato(), requestDTO.getValor(), id)) {
            throw new BusinessException("Já existe um contato " + requestDTO.getTipoContato().getNome() + 
                                      " com este valor cadastrado");
        }
        
        // Se for definido como principal, remover principal dos outros do mesmo tipo
        if (requestDTO.getPrincipal() != null && requestDTO.getPrincipal()) {
            contatosRepository.removerPrincipalPorTipo(
                contato.getEmpresa().getId(), requestDTO.getTipoContato(), id);
        }
        
        updateContatoFromDTO(contato, requestDTO);
        contato = contatosRepository.save(contato);
        
        return new EmpresaContatosResponseDTO(contato);
    }
    
    /**
     * Deletar contato (soft delete)
     */
    @Transactional
    public void deleteContato(Long id) {
        EmpresaContatos contato = contatosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));
        
        contato.setAtivo(false);
        contatosRepository.save(contato);
    }
    
    /**
     * Definir contato como principal
     */
    @Transactional
    public EmpresaContatosResponseDTO definirComoPrincipal(Long id) {
        EmpresaContatos contato = contatosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado"));
        
        // Remover principal dos outros do mesmo tipo
        contatosRepository.removerPrincipalPorTipo(
            contato.getEmpresa().getId(), contato.getTipoContato(), id);
        
        // Definir este como principal
        contato.setPrincipal(true);
        contato = contatosRepository.save(contato);
        
        return new EmpresaContatosResponseDTO(contato);
    }
    
    /**
     * Listar todos os contatos ativos
     */
    public List<EmpresaContatosResponseDTO> getAllContatos() {
        List<EmpresaContatos> contatos = contatosRepository.findAllAtivos();
        return contatos.stream()
                .map(EmpresaContatosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Validar formato do contato conforme o tipo
     */
    private void validarFormatoContato(EmpresaContatosRequestDTO dto) {
        String valor = dto.getValor();
        TipoContato tipo = dto.getTipoContato();
        
        switch (tipo) {
            case EMAIL:
                if (!valor.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
                    throw new BusinessException("Formato de e-mail inválido");
                }
                break;
            case TELEFONE:
            case CELULAR:
            case WHATSAPP:
            case FAX:
                String numbers = valor.replaceAll("\\D", "");
                if (numbers.length() < 10 || numbers.length() > 11) {
                    throw new BusinessException("Telefone deve conter entre 10 e 11 dígitos");
                }
                break;
        }
    }
    
    /**
     * Criar entidade a partir do DTO
     */
    private EmpresaContatos createContatoFromDTO(Empresa empresa, EmpresaContatosRequestDTO dto) {
        EmpresaContatos contato = new EmpresaContatos();
        contato.setEmpresa(empresa);
        contato.setTipoContato(dto.getTipoContato());
        contato.setValor(dto.getValor());
        contato.setDescricao(dto.getDescricao());
        contato.setPrincipal(dto.getPrincipal() != null ? dto.getPrincipal() : false);
        contato.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        
        return contato;
    }
    
    /**
     * Atualizar entidade a partir do DTO
     */
    private void updateContatoFromDTO(EmpresaContatos contato, EmpresaContatosRequestDTO dto) {
        contato.setTipoContato(dto.getTipoContato());
        contato.setValor(dto.getValor());
        contato.setDescricao(dto.getDescricao());
        contato.setPrincipal(dto.getPrincipal() != null ? dto.getPrincipal() : false);
        
        if (dto.getAtivo() != null) {
            contato.setAtivo(dto.getAtivo());
        }
    }
}
