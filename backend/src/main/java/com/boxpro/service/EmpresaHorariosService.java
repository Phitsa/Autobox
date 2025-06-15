package com.boxpro.service;

import com.boxpro.dto.request.EmpresaHorariosRequestDTO;
import com.boxpro.dto.response.EmpresaHorariosResponseDTO;
import com.boxpro.entity.Empresa;
import com.boxpro.entity.EmpresaHorarios;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.repository.EmpresaHorariosRepository;
import com.boxpro.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpresaHorariosService {
    
    @Autowired
    private EmpresaHorariosRepository horariosRepository;
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    /**
     * Buscar todos os horários de uma empresa
     */
    public List<EmpresaHorariosResponseDTO> getHorariosByEmpresa(Long empresaId) {
        if (!empresaRepository.existsById(empresaId)) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }
        
        List<EmpresaHorarios> horarios = horariosRepository.findByEmpresaIdAndAtivoTrue(empresaId);
        return horarios.stream()
                .map(EmpresaHorariosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Buscar horário específico por ID
     */
    public EmpresaHorariosResponseDTO getHorarioById(Long id) {
        EmpresaHorarios horario = horariosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado"));
        
        return new EmpresaHorariosResponseDTO(horario);
    }
    
    /**
     * Buscar horário por empresa e dia da semana
     */
    public EmpresaHorariosResponseDTO getHorarioByEmpresaAndDia(Long empresaId, Integer diaSemana) {
        EmpresaHorarios horario = horariosRepository.findByEmpresaIdAndDiaSemanaAndAtivoTrue(empresaId, diaSemana)
                .orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado para este dia"));
        
        return new EmpresaHorariosResponseDTO(horario);
    }
    
    /**
     * Criar novo horário
     */
    @Transactional
    public EmpresaHorariosResponseDTO createHorario(EmpresaHorariosRequestDTO requestDTO) {
        // Verificar se empresa existe
        Empresa empresa = empresaRepository.findById(requestDTO.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        
        // Verificar se já existe horário para este dia
        if (horariosRepository.existsByEmpresaIdAndDiaSemanaAndAtivoTrue(
                requestDTO.getEmpresaId(), requestDTO.getDiaSemana())) {
            throw new BusinessException("Já existe horário cadastrado para este dia da semana");
        }
        
        // Validar horários
        validarHorarios(requestDTO);
        
        EmpresaHorarios horario = createHorarioFromDTO(empresa, requestDTO);
        horario = horariosRepository.save(horario);
        
        return new EmpresaHorariosResponseDTO(horario);
    }
    
    /**
     * Atualizar horário existente
     */
    @Transactional
    public EmpresaHorariosResponseDTO updateHorario(Long id, EmpresaHorariosRequestDTO requestDTO) {
        EmpresaHorarios horario = horariosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado"));
        
        // Verificar se empresa existe
        if (!empresaRepository.existsById(requestDTO.getEmpresaId())) {
            throw new ResourceNotFoundException("Empresa não encontrada");
        }
        
        // Verificar conflito de dia da semana (se mudou o dia)
        if (!horario.getDiaSemana().equals(requestDTO.getDiaSemana())) {
            if (horariosRepository.existsByEmpresaIdAndDiaSemanaAndIdNotAndAtivoTrue(
                    requestDTO.getEmpresaId(), requestDTO.getDiaSemana(), id)) {
                throw new BusinessException("Já existe horário cadastrado para este dia da semana");
            }
        }
        
        // Validar horários
        validarHorarios(requestDTO);
        
        updateHorarioFromDTO(horario, requestDTO);
        horario = horariosRepository.save(horario);
        
        return new EmpresaHorariosResponseDTO(horario);
    }
    
    /**
     * Deletar horário (soft delete)
     */
    @Transactional
    public void deleteHorario(Long id) {
        EmpresaHorarios horario = horariosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horário não encontrado"));
        
        horario.setAtivo(false);
        horariosRepository.save(horario);
    }
    
    /**
     * Listar todos os horários ativos
     */
    public List<EmpresaHorariosResponseDTO> getAllHorarios() {
        List<EmpresaHorarios> horarios = horariosRepository.findAllAtivos();
        return horarios.stream()
                .map(EmpresaHorariosResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Validar consistência dos horários
     */
    private void validarHorarios(EmpresaHorariosRequestDTO dto) {
        if (dto.getFechado() != null && dto.getFechado()) {
            // Se está fechado, não precisa validar horários
            return;
        }
        
        // Validar horário manhã
        if (dto.getHorarioAbertura() != null && dto.getHorarioFechamento() != null) {
            if (dto.getHorarioAbertura().isAfter(dto.getHorarioFechamento())) {
                throw new BusinessException("Horário de abertura deve ser anterior ao horário de fechamento");
            }
        }
        
        // Validar horário tarde
        if (dto.getHorarioAberturaTarde() != null && dto.getHorarioFechamentoTarde() != null) {
            if (dto.getHorarioAberturaTarde().isAfter(dto.getHorarioFechamentoTarde())) {
                throw new BusinessException("Horário de abertura da tarde deve ser anterior ao horário de fechamento da tarde");
            }
        }
        
        // Validar sequência manhã -> tarde
        if (dto.getHorarioFechamento() != null && dto.getHorarioAberturaTarde() != null) {
            if (dto.getHorarioFechamento().isAfter(dto.getHorarioAberturaTarde())) {
                throw new BusinessException("Horário de fechamento da manhã deve ser anterior ao horário de abertura da tarde");
            }
        }
    }
    
    /**
     * Criar entidade a partir do DTO
     */
    private EmpresaHorarios createHorarioFromDTO(Empresa empresa, EmpresaHorariosRequestDTO dto) {
        EmpresaHorarios horario = new EmpresaHorarios();
        horario.setEmpresa(empresa);
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHorarioAbertura(dto.getHorarioAbertura());
        horario.setHorarioFechamento(dto.getHorarioFechamento());
        horario.setHorarioAberturaTarde(dto.getHorarioAberturaTarde());
        horario.setHorarioFechamentoTarde(dto.getHorarioFechamentoTarde());
        horario.setFechado(dto.getFechado() != null ? dto.getFechado() : false);
        horario.setObservacoes(dto.getObservacoes());
        horario.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : true);
        
        return horario;
    }
    
    /**
     * Atualizar entidade a partir do DTO
     */
    private void updateHorarioFromDTO(EmpresaHorarios horario, EmpresaHorariosRequestDTO dto) {
        horario.setDiaSemana(dto.getDiaSemana());
        horario.setHorarioAbertura(dto.getHorarioAbertura());
        horario.setHorarioFechamento(dto.getHorarioFechamento());
        horario.setHorarioAberturaTarde(dto.getHorarioAberturaTarde());
        horario.setHorarioFechamentoTarde(dto.getHorarioFechamentoTarde());
        horario.setFechado(dto.getFechado() != null ? dto.getFechado() : false);
        horario.setObservacoes(dto.getObservacoes());
        
        if (dto.getAtivo() != null) {
            horario.setAtivo(dto.getAtivo());
        }
    }
}
