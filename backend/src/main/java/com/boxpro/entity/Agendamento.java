package com.boxpro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    @Column(name = "veiculo_id", nullable = false)
    private Integer veiculoId;

    @Column(name = "servico_id", nullable = false)
    private Integer servicoId;

    @Column(name = "funcionario_responsavel_id")
    private Integer funcionarioResponsavelId;

    @Column(name = "data_agendamento", nullable = false)
    private LocalDate dataAgendamento;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Column(length = 50)
    private String status = "agendado";

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    @Column(name = "motivo_cancelamento", columnDefinition = "TEXT")
    private String motivoCancelamento;

    @Column(name = "taxa_cancelamento", precision = 10, scale = 2)
    private BigDecimal taxaCancelamento;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "agendamento")
    @JsonManagedReference // Lado "pai" da referência
    private List<HistoricoAgendamento> historicos;

    // Construtores
    public Agendamento() {}

    public Agendamento(Integer id, Integer clienteId, Integer veiculoId, Integer servicoId, 
                      Integer funcionarioResponsavelId, LocalDate dataAgendamento, LocalTime horaInicio, 
                      LocalTime horaFim, String status, String observacoes, BigDecimal valorTotal) {
        this.id = id;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.servicoId = servicoId;
        this.funcionarioResponsavelId = funcionarioResponsavelId;
        this.dataAgendamento = dataAgendamento;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.status = status;
        this.observacoes = observacoes;
        this.valorTotal = valorTotal;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }

    public Integer getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Integer veiculoId) { this.veiculoId = veiculoId; }

    public Integer getServicoId() { return servicoId; }
    public void setServicoId(Integer servicoId) { this.servicoId = servicoId; }

    public Integer getFuncionarioResponsavelId() { return funcionarioResponsavelId; }
    public void setFuncionarioResponsavelId(Integer funcionarioResponsavelId) { 
        this.funcionarioResponsavelId = funcionarioResponsavelId; 
    }

    public LocalDate getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(LocalDate dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalTime horaFim) { this.horaFim = horaFim; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public LocalDate getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(LocalDate dataCancelamento) { this.dataCancelamento = dataCancelamento; }

    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }

    public BigDecimal getTaxaCancelamento() { return taxaCancelamento; }
    public void setTaxaCancelamento(BigDecimal taxaCancelamento) { this.taxaCancelamento = taxaCancelamento; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<HistoricoAgendamento> getHistoricos() { return historicos; }
    public void setHistoricos(List<HistoricoAgendamento> historicos) { this.historicos = historicos; }
}