package com.boxpro.dto.response;

import com.boxpro.entity.EmpresaContatos;
import com.boxpro.entity.EmpresaContatos.TipoContato;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class EmpresaContatosResponseDTO {
    
    private Long id;
    private Long empresaId;
    private String nomeEmpresa;
    private TipoContato tipoContato;
    private String nomeTipoContato;
    private String valor;
    private String valorFormatado;
    private String descricao;
    private Boolean principal;
    private Boolean ativo;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Construtores
    public EmpresaContatosResponseDTO() {}
    
    public EmpresaContatosResponseDTO(EmpresaContatos contato) {
        this.id = contato.getId();
        this.empresaId = contato.getEmpresa().getId();
        this.nomeEmpresa = contato.getEmpresa().getNomeFantasia();
        this.tipoContato = contato.getTipoContato();
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
    
    public String getNomeEmpresa() {
        return nomeEmpresa;
    }
    
    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }
    
    public TipoContato getTipoContato() {
        return tipoContato;
    }
    
    public void setTipoContato(TipoContato tipoContato) {
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
}
