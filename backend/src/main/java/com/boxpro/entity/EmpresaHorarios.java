package com.boxpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "empresa_horarios", 
       uniqueConstraints = @UniqueConstraint(name = "uk_empresa_horarios_dia", 
                                           columnNames = {"empresa_id", "dia_semana"}),
       indexes = @Index(name = "idx_empresa_horarios_dia_semana", columnList = "dia_semana"))
public class EmpresaHorarios {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Empresa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_empresa_horarios_empresa"))
    private Empresa empresa;
    
    @NotNull(message = "Dia da semana é obrigatório")
    @Min(value = 0, message = "Dia da semana deve ser entre 0 (Domingo) e 6 (Sábado)")
    @Max(value = 6, message = "Dia da semana deve ser entre 0 (Domingo) e 6 (Sábado)")
    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana;
    
    @Column(name = "horario_abertura")
    private LocalTime horarioAbertura;
    
    @Column(name = "horario_fechamento")
    private LocalTime horarioFechamento;
    
    @Column(name = "horario_abertura_tarde")
    private LocalTime horarioAberturaTarde;
    
    @Column(name = "horario_fechamento_tarde")
    private LocalTime horarioFechamentoTarde;
    
    @Column(name = "fechado", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean fechado = false;
    
    @Size(max = 255, message = "Observações deve ter no máximo 255 caracteres")
    @Column(name = "observacoes")
    private String observacoes;
    
    @Column(name = "ativo", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean ativo = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Construtores
    public EmpresaHorarios() {}
    
    public EmpresaHorarios(Empresa empresa, Integer diaSemana) {
        this.empresa = empresa;
        this.diaSemana = diaSemana;
        this.fechado = false;
        this.ativo = true;
    }
    
    // Enum para dias da semana
    public enum DiaSemana {
        DOMINGO(0, "Domingo"),
        SEGUNDA(1, "Segunda-feira"),
        TERCA(2, "Terça-feira"),
        QUARTA(3, "Quarta-feira"),
        QUINTA(4, "Quinta-feira"),
        SEXTA(5, "Sexta-feira"),
        SABADO(6, "Sábado");
        
        private final int codigo;
        private final String nome;
        
        DiaSemana(int codigo, String nome) {
            this.codigo = codigo;
            this.nome = nome;
        }
        
        public int getCodigo() {
            return codigo;
        }
        
        public String getNome() {
            return nome;
        }
        
        public static DiaSemana fromCodigo(int codigo) {
            for (DiaSemana dia : values()) {
                if (dia.codigo == codigo) {
                    return dia;
                }
            }
            throw new IllegalArgumentException("Código de dia da semana inválido: " + codigo);
        }
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Empresa getEmpresa() {
        return empresa;
    }
    
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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
    
    // Métodos auxiliares
    public String getNomeDiaSemana() {
        return DiaSemana.fromCodigo(this.diaSemana).getNome();
    }
    
    public boolean isAberto() {
        return !fechado && (horarioAbertura != null || horarioAberturaTarde != null);
    }
}
