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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpresaContatosService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmpresaContatosService.class);
    
    @Autowired
    private EmpresaContatosRepository contatosRepository;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    /**
     * Buscar todos os contatos de uma empresa
     */
    public List<EmpresaContatosResponseDTO> getContatosByEmpresa(Long empresaId) {
        logger.info("🔍 Buscando contatos para empresa ID: {}", empresaId);
        
        if (!empresaRepository.existsById(empresaId)) {
            logger.warn("⚠️ Empresa com ID {} não encontrada", empresaId);
            throw new ResourceNotFoundException("Empresa não encontrada com ID: " + empresaId);
        }
        
        List<EmpresaContatos> contatos = contatosRepository.findByEmpresaIdAndAtivoTrue(empresaId);
        logger.info("✅ Encontrados {} contatos para empresa ID: {}", contatos.size(), empresaId);
        
        return contatos.stream()
                .map(EmpresaContatosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar contato específico por ID
     */
    public EmpresaContatosResponseDTO getContatoById(Long id) {
        logger.info("🔍 Buscando contato com ID: {}", id);
        
        EmpresaContatos contato = contatosRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("⚠️ Contato com ID {} não encontrado", id);
                    return new ResourceNotFoundException("Contato não encontrado com ID: " + id);
                });
        
        logger.info("✅ Contato encontrado: {}", contato);
        return new EmpresaContatosResponseDTO(contato);
    }
    
    /**
     * Buscar contatos por tipo
     */
    public List<EmpresaContatosResponseDTO> getContatosByEmpresaAndTipo(Long empresaId, TipoContato tipoContato) {
        logger.info("🔍 Buscando contatos tipo {} para empresa ID: {}", tipoContato, empresaId);
        
        if (!empresaRepository.existsById(empresaId)) {
            logger.warn("⚠️ Empresa com ID {} não encontrada", empresaId);
            throw new ResourceNotFoundException("Empresa não encontrada com ID: " + empresaId);
        }
        
        List<EmpresaContatos> contatos = contatosRepository.findByEmpresaIdAndTipoContatoAndAtivoTrue(empresaId, tipoContato);
        logger.info("✅ Encontrados {} contatos tipo {} para empresa ID: {}", contatos.size(), tipoContato, empresaId);
        
        return contatos.stream()
                .map(EmpresaContatosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar contatos principais de uma empresa
     */
    public List<EmpresaContatosResponseDTO> getContatosPrincipais(Long empresaId) {
        logger.info("🔍 Buscando contatos principais para empresa ID: {}", empresaId);
        
        if (!empresaRepository.existsById(empresaId)) {
            logger.warn("⚠️ Empresa com ID {} não encontrada", empresaId);
            throw new ResourceNotFoundException("Empresa não encontrada com ID: " + empresaId);
        }
        
        List<EmpresaContatos> contatos = contatosRepository.findContatosPrincipaisByEmpresaId(empresaId);
        logger.info("✅ Encontrados {} contatos principais para empresa ID: {}", contatos.size(), empresaId);
        
        return contatos.stream()
                .map(EmpresaContatosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Criar novo contato
     */
    @Transactional
    public EmpresaContatosResponseDTO createContato(EmpresaContatosRequestDTO requestDTO) {
        logger.info("📝 Criando novo contato: {}", requestDTO);
        
        try {
            // Verificar se empresa existe
            Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                    .orElseThrow(() -> {
                        logger.warn("⚠️ Empresa com ID {} não encontrada", requestDTO.getEmpresaId());
                        return new ResourceNotFoundException("Empresa não encontrada com ID: " + requestDTO.getEmpresaId());
                    });
            
            // Validar formato do contato
            validarFormatoContato(requestDTO);
            
            // Verificar se já existe contato idêntico
            boolean contatoExiste = contatosRepository.existsByEmpresaIdAndTipoContatoAndValorAndAtivoTrue(
                    requestDTO.getEmpresaId(), requestDTO.getTipoContato(), requestDTO.getValor().trim());
            
            if (contatoExiste) {
                String erro = "Já existe um contato " + requestDTO.getTipoContato().getNome() + 
                             " com este valor cadastrado para esta empresa";
                logger.warn("⚠️ {}", erro);
                throw new BusinessException(erro);
            }
            
            EmpresaContatos contato = createContatoFromDTO(empresa, requestDTO);
            
            // Se for definido como principal, remover principal dos outros do mesmo tipo
            if (requestDTO.getPrincipal() != null && requestDTO.getPrincipal()) {
                logger.info("🌟 Definindo contato como principal - removendo outros principais do tipo {}", requestDTO.getTipoContato());
                contatosRepository.removerTodosPrincipaisPorTipo(empresa.getId(), requestDTO.getTipoContato());
            }
            
            contato = contatosRepository.save(contato);
            logger.info("✅ Contato criado com sucesso: {}", contato);
            
            return new EmpresaContatosResponseDTO(contato);
            
        } catch (ResourceNotFoundException | BusinessException e) {
            logger.error("❌ Erro de negócio ao criar contato: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao criar contato: {}", e.getMessage(), e);
            throw new BusinessException("Erro interno ao criar contato: " + e.getMessage());
        }
    }
    
    /**
     * Atualizar contato existente
     */
    @Transactional
    public EmpresaContatosResponseDTO updateContato(Long id, EmpresaContatosRequestDTO requestDTO) {
        logger.info("✏️ Atualizando contato ID {}: {}", id, requestDTO);
        
        try {
            EmpresaContatos contato = contatosRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("⚠️ Contato com ID {} não encontrado", id);
                        return new ResourceNotFoundException("Contato não encontrado com ID: " + id);
                    });
            
            // Verificar se empresa existe
            if (!empresaRepository.existsById(requestDTO.getEmpresaId())) {
                logger.warn("⚠️ Empresa com ID {} não encontrada", requestDTO.getEmpresaId());
                throw new ResourceNotFoundException("Empresa não encontrada com ID: " + requestDTO.getEmpresaId());
            }
            
            // Validar formato do contato
            validarFormatoContato(requestDTO);
            
            // Verificar se já existe contato idêntico (exceto o atual)
            boolean contatoExiste = contatosRepository.existsByEmpresaIdAndTipoContatoAndValorAndIdNotAndAtivoTrue(
                    requestDTO.getEmpresaId(), requestDTO.getTipoContato(), requestDTO.getValor().trim(), id);
            
            if (contatoExiste) {
                String erro = "Já existe outro contato " + requestDTO.getTipoContato().getNome() + 
                             " com este valor cadastrado para esta empresa";
                logger.warn("⚠️ {}", erro);
                throw new BusinessException(erro);
            }
            
            // Se for definido como principal, remover principal dos outros do mesmo tipo
            if (requestDTO.getPrincipal() != null && requestDTO.getPrincipal()) {
                logger.info("🌟 Definindo contato como principal - removendo outros principais do tipo {}", requestDTO.getTipoContato());
                contatosRepository.removerPrincipalPorTipo(
                    contato.getEmpresa().getId(), requestDTO.getTipoContato(), id);
            }
            
            updateContatoFromDTO(contato, requestDTO);
            contato = contatosRepository.save(contato);
            logger.info("✅ Contato atualizado com sucesso: {}", contato);
            
            return new EmpresaContatosResponseDTO(contato);
            
        } catch (ResourceNotFoundException | BusinessException e) {
            logger.error("❌ Erro de negócio ao atualizar contato: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao atualizar contato: {}", e.getMessage(), e);
            throw new BusinessException("Erro interno ao atualizar contato: " + e.getMessage());
        }
    }
    
    /**
     * Deletar contato (soft delete)
     */
    @Transactional
    public void deleteContato(Long id) {
        logger.info("🗑️ Deletando contato ID: {}", id);
        
        try {
            EmpresaContatos contato = contatosRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("⚠️ Contato com ID {} não encontrado", id);
                        return new ResourceNotFoundException("Contato não encontrado com ID: " + id);
                    });
            
            contato.setAtivo(false);
            contatosRepository.save(contato);
            logger.info("✅ Contato deletado (soft delete) com sucesso: {}", contato);
            
        } catch (ResourceNotFoundException e) {
            logger.error("❌ Erro ao deletar contato: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao deletar contato: {}", e.getMessage(), e);
            throw new BusinessException("Erro interno ao deletar contato: " + e.getMessage());
        }
    }
    
    /**
     * Definir contato como principal
     */
    @Transactional
    public EmpresaContatosResponseDTO definirComoPrincipal(Long id) {
        logger.info("🌟 Definindo contato ID {} como principal", id);
        
        try {
            EmpresaContatos contato = contatosRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("⚠️ Contato com ID {} não encontrado", id);
                        return new ResourceNotFoundException("Contato não encontrado com ID: " + id);
                    });
            
            // Remover principal dos outros do mesmo tipo
            logger.info("🔄 Removendo outros principais do tipo {} para empresa ID {}", 
                       contato.getTipoContato(), contato.getEmpresa().getId());
            contatosRepository.removerPrincipalPorTipo(
                contato.getEmpresa().getId(), contato.getTipoContato(), id);
            
            // Definir este como principal
            contato.setPrincipal(true);
            contato = contatosRepository.save(contato);
            logger.info("✅ Contato definido como principal: {}", contato);
            
            return new EmpresaContatosResponseDTO(contato);
            
        } catch (ResourceNotFoundException e) {
            logger.error("❌ Erro ao definir contato como principal: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao definir contato como principal: {}", e.getMessage(), e);
            throw new BusinessException("Erro interno ao definir contato como principal: " + e.getMessage());
        }
    }
    
    /**
     * Listar todos os contatos ativos
     */
    public List<EmpresaContatosResponseDTO> getAllContatos() {
        logger.info("📋 Buscando todos os contatos ativos");
        
        try {
            List<EmpresaContatos> contatos = contatosRepository.findAllAtivos();
            logger.info("✅ Encontrados {} contatos ativos no total", contatos.size());
            
            return contatos.stream()
                    .map(EmpresaContatosResponseDTO::new)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.error("❌ Erro ao buscar todos os contatos: {}", e.getMessage(), e);
            throw new BusinessException("Erro interno ao buscar contatos: " + e.getMessage());
        }
    }
    
    /**
     * Validar formato do contato conforme o tipo
     */
    private void validarFormatoContato(EmpresaContatosRequestDTO dto) {
        String valor = dto.getValor();
        TipoContato tipo = dto.getTipoContato();
        
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessException("Valor do contato é obrigatório");
        }
        
        valor = valor.trim();
        
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
        
        logger.debug("✅ Formato do contato {} validado com sucesso", tipo);
    }
    
    /**
     * Criar entidade a partir do DTO
     */
    private EmpresaContatos createContatoFromDTO(Empresa empresa, EmpresaContatosRequestDTO dto) {
        EmpresaContatos contato = new EmpresaContatos();
        contato.setEmpresa(empresa);
        contato.setTipoContato(dto.getTipoContato());
        contato.setValor(dto.getValor().trim());
        contato.setDescricao(dto.getDescricao() != null ? dto.getDescricao().trim() : null);
        contato.setPrincipal(dto.getPrincipal() != null ? dto.getPrincipal() : false);
        contato.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        
        return contato;
    }
    
    /**
     * Atualizar entidade a partir do DTO
     */
    private void updateContatoFromDTO(EmpresaContatos contato, EmpresaContatosRequestDTO dto) {
        contato.setTipoContato(dto.getTipoContato());
        contato.setValor(dto.getValor().trim());
        contato.setDescricao(dto.getDescricao() != null ? dto.getDescricao().trim() : null);
        contato.setPrincipal(dto.getPrincipal() != null ? dto.getPrincipal() : false);
        
        if (dto.getAtivo() != null) {
            contato.setAtivo(dto.getAtivo());
        }
    }
}