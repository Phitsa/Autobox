package com.boxpro.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "historico_agendamentos")
public class HistoricoAgendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "agendamento_id", nullable = false)
    @JsonBackReference
    private Agendamento agendamento;

    @Column(name = "funcionario_id", nullable = false)
    private Integer funcionarioId;

    @Column(nullable = false)
    private String acao;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(name = "data_acao")
    private LocalDateTime dataAcao;

    // Construtores
    public HistoricoAgendamento() {}

    public HistoricoAgendamento(Integer id, Agendamento agendamento, Integer funcionarioId, 
                               String acao, String detalhes, LocalDateTime dataAcao) {
        this.id = id;
        this.agendamento = agendamento;
        this.funcionarioId = funcionarioId;
        this.acao = acao;
        this.detalhes = detalhes;
        this.dataAcao = dataAcao;
    }

    @PrePersist
    protected void onCreate() {
        dataAcao = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Agendamento getAgendamento() { return agendamento; }
    public void setAgendamento(Agendamento agendamento) { this.agendamento = agendamento; }

    public Integer getFuncionarioId() { return funcionarioId; }
    public void setFuncionarioId(Integer funcionarioId) { this.funcionarioId = funcionarioId; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }

    public LocalDateTime getDataAcao() { return dataAcao; }
    public void setDataAcao(LocalDateTime dataAcao) { this.dataAcao = dataAcao; }
}