package com.boxpro.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public class VeiculoRequestDTO {
    
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private String cor;
    private LocalDateTime dataCriacao;

    @NotNull(message = "clienteId não pode ser nulo")
    @JsonProperty("clienteId")
    private Long clienteId;

    public VeiculoRequestDTO() {}

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getMarca() {return marca;}
    public void setMarca(String marca) {this.marca = marca;}

    public String getModelo() {return modelo;}
    public void setModelo(String modelo) {this.modelo = modelo;}

    public Integer getAno() {return ano;}
    public void setAno(Integer ano) {this.ano = ano;}

    public String getPlaca() {return placa;}
    public void setPlaca(String placa) {this.placa = placa;}

    public String getCor() {return cor;}
    public void setCor(String cor) {this.cor = cor;}

    public LocalDateTime getDataCriacao() { return dataCriacao;}
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    @Override
    public String toString() {
    return "VeiculoRequestDTO{" +
            "marca='" + marca + '\'' +
            ", modelo='" + modelo + '\'' +
            ", ano=" + ano +
            ", placa='" + placa + '\'' +
            ", cor='" + cor + '\'' +
            ", clienteId=" + clienteId +
            '}';
    }   
}
