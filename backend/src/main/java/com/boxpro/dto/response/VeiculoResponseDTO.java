package com.boxpro.dto.response;

import java.time.LocalDateTime;

public class VeiculoResponseDTO {
    
    private Long id;
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private String cor;
    private String nomeCliente;
    private Long clientId;
    private LocalDateTime dataCriacao;

    public VeiculoResponseDTO() {}

    public VeiculoResponseDTO(Long id, String marca, String modelo, Integer ano, String placa, String cor, String nomeCliente, Long clientId, LocalDateTime dataCriacao) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.cor = cor;
        this.nomeCliente = nomeCliente;
        this.clientId = clientId;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

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

    public String getNomeCliente() {return nomeCliente;}
    public void setNomeCliente(String nomeCliente) {this.nomeCliente = nomeCliente;}

    public Long getClientId() {return clientId;}
    public void setClientId(Long clientId) {this.clientId = clientId;}

    public LocalDateTime getDataCriacao() { return dataCriacao;}
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

}