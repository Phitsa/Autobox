package com.boxpro.controller;

import com.boxpro.dto.request.EmpresaHorariosRequestDTO;
import com.boxpro.dto.response.EmpresaHorariosResponseDTO;
import com.boxpro.service.EmpresaHorariosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresa-horarios")
@CrossOrigin(origins = "*")
public class EmpresaHorariosController {
    
    @Autowired
    private EmpresaHorariosService horariosService;
    
    /**
     * Endpoint para teste de conexão
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "online");
        response.put("service", "empresa-horarios");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Buscar todos os horários de uma empresa
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<EmpresaHorariosResponseDTO>> getHorariosByEmpresa(@PathVariable Long empresaId) {
        try {
            List<EmpresaHorariosResponseDTO> horarios = horariosService.getHorariosByEmpresa(empresaId);
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Buscar horário específico por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaHorariosResponseDTO> getHorarioById(@PathVariable Long id) {
        try {
            EmpresaHorariosResponseDTO horario = horariosService.getHorarioById(id);
            return ResponseEntity.ok(horario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Buscar horário por empresa e dia da semana
     */
    @GetMapping("/empresa/{empresaId}/dia/{diaSemana}")
    public ResponseEntity<EmpresaHorariosResponseDTO> getHorarioByEmpresaAndDia(
            @PathVariable Long empresaId, 
            @PathVariable Integer diaSemana) {
        try {
            EmpresaHorariosResponseDTO horario = horariosService.getHorarioByEmpresaAndDia(empresaId, diaSemana);
            return ResponseEntity.ok(horario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Criar novo horário
     */
    @PostMapping
    public ResponseEntity<?> createHorario(@Valid @RequestBody EmpresaHorariosRequestDTO requestDTO) {
        try {
            EmpresaHorariosResponseDTO horario = horariosService.createHorario(requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Horário criado com sucesso");
            response.put("horario", horario);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Atualizar horário existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHorario(@PathVariable Long id, 
                                         @Valid @RequestBody EmpresaHorariosRequestDTO requestDTO) {
        try {
            EmpresaHorariosResponseDTO horario = horariosService.updateHorario(id, requestDTO);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Horário atualizado com sucesso");
            response.put("horario", horario);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Deletar horário (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHorario(@PathVariable Long id) {
        try {
            horariosService.deleteHorario(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Horário deletado com sucesso");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Listar todos os horários
     */
    @GetMapping
    public ResponseEntity<List<EmpresaHorariosResponseDTO>> getAllHorarios() {
        try {
            List<EmpresaHorariosResponseDTO> horarios = horariosService.getAllHorarios();
            return ResponseEntity.ok(horarios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Inicializar horários padrão para uma empresa (todos os dias da semana)
     */
    @PostMapping("/empresa/{empresaId}/inicializar")
    public ResponseEntity<?> inicializarHorarios(@PathVariable Long empresaId) {
        try {
            Map<String, Object> response = new HashMap<>();
            List<EmpresaHorariosResponseDTO> horariosExistentes = horariosService.getHorariosByEmpresa(empresaId);
            
            // Se já tem horários, retorna os existentes
            if (!horariosExistentes.isEmpty()) {
                response.put("message", "Horários já existem para esta empresa");
                response.put("horarios", horariosExistentes);
                return ResponseEntity.ok(response);
            }
            
            // Criar horários padrão para todos os dias da semana
            List<EmpresaHorariosResponseDTO> novosHorarios = new java.util.ArrayList<>();
            
            for (int dia = 0; dia <= 6; dia++) {
                EmpresaHorariosRequestDTO dto = new EmpresaHorariosRequestDTO();
                dto.setEmpresaId(empresaId);
                dto.setDiaSemana(dia);
                
                // Horário padrão: 8:00-12:00 e 13:00-18:00 (segunda a sexta)
                // Sábado: 8:00-12:00
                // Domingo: fechado
                if (dia >= 1 && dia <= 5) { // Segunda a sexta
                    dto.setHorarioAbertura(java.time.LocalTime.of(8, 0));
                    dto.setHorarioFechamento(java.time.LocalTime.of(12, 0));
                    dto.setHorarioAberturaTarde(java.time.LocalTime.of(13, 0));
                    dto.setHorarioFechamentoTarde(java.time.LocalTime.of(18, 0));
                    dto.setFechado(false);
                } else if (dia == 6) { // Sábado
                    dto.setHorarioAbertura(java.time.LocalTime.of(8, 0));
                    dto.setHorarioFechamento(java.time.LocalTime.of(12, 0));
                    dto.setFechado(false);
                } else { // Domingo
                    dto.setFechado(true);
                }
                
                dto.setAtivo(true);
                
                EmpresaHorariosResponseDTO novoHorario = horariosService.createHorario(dto);
                novosHorarios.add(novoHorario);
            }
            
            response.put("message", "Horários padrão criados com sucesso");
            response.put("horarios", novosHorarios);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}