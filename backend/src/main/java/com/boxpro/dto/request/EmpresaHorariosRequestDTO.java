package com.boxpro.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public class EmpresaHorariosRequestDTO {
    
    @NotNull(message = "ID da empresa é obrigatório")
    private Long empresaId;
    
    @NotNull(message = "Dia da semana é obrigatório")
    @Min(value = 0, message = "Dia da semana deve ser entre 0 (Domingo) e 6 (Sábado)")
    @Max(value = 6, message = "Dia da semana deve ser entre 0 (Domingo) e 6 (Sábado)")
    private Integer diaSemana;
    
    private LocalTime horarioAbertura;
    private LocalTime horarioFechamento;
    private LocalTime horarioAberturaTarde;
    private LocalTime horarioFechamentoTarde;
    
    private Boolean fechado = false;
    
    @Size(max = 255, message = "Observações deve ter no máximo 255 caracteres")
    private String observacoes;
    
    private Boolean ativo = true;
    
    // Construtores
    public EmpresaHorariosRequestDTO() {}
    
    // Getters e Setters
    public Long getEmpresaId() {
        return empresaId;
    }
    
    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }
    
    public Integer getDiaSemana() {
        return diaSemana;
    }
    
    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
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
}
