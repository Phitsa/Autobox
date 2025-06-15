package com.boxpro.dto.response;

import com.boxpro.entity.EmpresaContatos;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class EmpresaContatosResponseDTO {
    
    private Long id;
    
    @JsonProperty("empresaId")
    private Long empresaId;
    
    @JsonProperty("tipoContato")
    private String tipoContato; // Sempre retornará em minúsculo para o frontend
    
    @JsonProperty("nomeTipoContato")
    private String nomeTipoContato;
    
    private String valor;
    
    @JsonProperty("valorFormatado")
    private String valorFormatado;
    
    private String descricao;
    
    private Boolean principal;
    
    private Boolean ativo;
    
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    
    @JsonProperty("updatedAt")
    private LocalDateTime updatedAt;
    
    // Construtor padrão
    public EmpresaContatosResponseDTO() {
    }
    
    // Construtor a partir da entidade
    public EmpresaContatosResponseDTO(EmpresaContatos contato) {
        this.id = contato.getId();
        this.empresaId = contato.getEmpresa() != null ? contato.getEmpresa().getId() : null;
        
        // IMPORTANTE: Converter enum maiúsculo do banco para minúsculo do frontend
        this.tipoContato = contato.getTipoContato() != null ? 
            contato.getTipoContato().name().toLowerCase() : null;
            
        this.nomeTipoContato = contato.getNomeTipoContato();
        this.valor = contato.getValor();
        this.valorFormatado = contato.getValorFormatado();
        this.descricao = contato.getDescricao();
        this.principal = contato.getPrincipal();
        this.ativo = contato.getAtivo();
        this.createdAt = contato.getCreatedAt();
        this.updatedAt = contato.getUpdatedAt();
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
    
    public String getTipoContato() {
        return tipoContato;
    }
    
    public void setTipoContato(String tipoContato) {
        this.tipoContato = tipoContato;
    }
    
    public String getNomeTipoContato() {
        return nomeTipoContato;
    }
    
    public void setNomeTipoContato(String nomeTipoContato) {
        this.nomeTipoContato = nomeTipoContato;
    }
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
    }
    
    public String getValorFormatado() {
        return valorFormatado;
    }
    
    public void setValorFormatado(String valorFormatado) {
        this.valorFormatado = valorFormatado;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Boolean getPrincipal() {
        return principal;
    }
    
    public void setPrincipal(Boolean principal) {
        this.principal = principal;
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
    
    @Override
    public String toString() {
        return "EmpresaContatosResponseDTO{" +
                "id=" + id +
                ", empresaId=" + empresaId +
                ", tipoContato='" + tipoContato + '\'' +
                ", valor='" + valor + '\'' +
                ", principal=" + principal +
                ", ativo=" + ativo +
                '}';
    }
}