package com.boxpro.dto.response;

import com.boxpro.entity.EmpresaHorarios;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class EmpresaHorariosResponseDTO {
    
    private Long id;
    private Long empresaId;
    private String nomeEmpresa;
    private Integer diaSemana;
    private String nomeDiaSemana;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioAbertura;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFechamento;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioAberturaTarde;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horarioFechamentoTarde;
    
    private Boolean fechado;
    private String observacoes;
    private Boolean ativo;
    private Boolean aberto;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Construtores
    public EmpresaHorariosResponseDTO() {}
    
    public EmpresaHorariosResponseDTO(EmpresaHorarios horario) {
        this.id = horario.getId();
        this.empresaId = horario.getEmpresa().getId();
        this.nomeEmpresa = horario.getEmpresa().getNomeFantasia();
        this.diaSemana = horario.getDiaSemana();
        this.nomeDiaSemana = horario.getNomeDiaSemana();
        this.horarioAbertura = horario.getHorarioAbertura();
        this.horarioFechamento = horario.getHorarioFechamento();
        this.horarioAberturaTarde = horario.getHorarioAberturaTarde();
        this.horarioFechamentoTarde = horario.getHorarioFechamentoTarde();
        this.fechado = horario.getFechado();
        this.observacoes = horario.getObservacoes();
        this.ativo = horario.getAtivo();
        this.aberto = horario.isAberto();
        this.createdAt = horario.getCreatedAt();
        this.updatedAt = horario.getUpdatedAt();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEmpresaId() {
        return empresaId;
    }
    
    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }
    
    public String getNomeEmpresa() {
        return nomeEmpresa;
    }
    
    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }
    
    public Integer getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }
    
    public String getNomeDiaSemana() {
        return nomeDiaSemana;
    }
    
    public void setNomeDiaSemana(String nomeDiaSemana) {
        this.nomeDiaSemana = nomeDiaSemana;
    }
    
    public LocalTime getHorarioAbertura() {
        return horarioAbertura;
    }
    
    public void setHorarioAbertura(LocalTime horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }
    
    public LocalTime getHorarioFechamento() {
        return horarioFechamento;
    }
    
    public void setHorarioFechamento(LocalTime horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }
    
    public LocalTime getHorarioAberturaTarde() {
        return horarioAberturaTarde;
    }
    
    public void setHorarioAberturaTarde(LocalTime horarioAberturaTarde) {
        this.horarioAberturaTarde = horarioAberturaTarde;
    }
    
    public LocalTime getHorarioFechamentoTarde() {
        return horarioFechamentoTarde;
    }
    
    public void setHorarioFechamentoTarde(LocalTime horarioFechamentoTarde) {
        this.horarioFechamentoTarde = horarioFechamentoTarde;
    }
    
    public Boolean getFechado() {
        return fechado;
    }
    
    public void setFechado(Boolean fechado) {
        this.fechado = fechado;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public Boolean getAberto() {
        return aberto;
    }
    
    public void setAberto(Boolean aberto) {
        this.aberto = aberto;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
