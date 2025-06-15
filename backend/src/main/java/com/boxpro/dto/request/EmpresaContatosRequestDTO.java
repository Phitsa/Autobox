package com.boxpro.dto.request;

import com.boxpro.entity.EmpresaContatos.TipoContato;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EmpresaContatosRequestDTO {
    
    @NotNull(message = "ID da empresa é obrigatório")
    @JsonProperty("empresaId")
    private Long empresaId;
    
    @NotNull(message = "Tipo de contato é obrigatório")
    private TipoContato tipoContato;
    
    @NotBlank(message = "Valor do contato é obrigatório")
    @Size(max = 255, message = "Valor deve ter no máximo 255 caracteres")
    private String valor;
    
    @Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres")
    private String descricao;
    
    private Boolean principal = false;
    
    private Boolean ativo = true;
    
    // Construtor padrão
    public EmpresaContatosRequestDTO() {
    }
    
    // Construtor com parâmetros principais
    public EmpresaContatosRequestDTO(Long empresaId, TipoContato tipoContato, String valor) {
        this.empresaId = empresaId;
        this.tipoContato = tipoContato;
        this.valor = valor;
    }
    
    // Getters e Setters
    public Long getEmpresaId() {
        return empresaId;
    }
    
    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }
    
    public TipoContato getTipoContato() {
        return tipoContato;
    }
    
    public void setTipoContato(TipoContato tipoContato) {
        this.tipoContato = tipoContato;
    }
    
    // Setter especial para aceitar strings do frontend (em minúsculo)
    @JsonSetter("tipoContato")
    public void setTipoContatoFromString(String tipoContatoStr) {
        if (tipoContatoStr != null) {
            this.tipoContato = TipoContato.fromString(tipoContatoStr);
        }
    }
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
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
    
    @Override
    public String toString() {
        return "EmpresaContatosRequestDTO{" +
                "empresaId=" + empresaId +
                ", tipoContato=" + tipoContato +
                ", valor='" + valor + '\'' +
                ", descricao='" + descricao + '\'' +
                ", principal=" + principal +
                ", ativo=" + ativo +
                '}';
    }
}