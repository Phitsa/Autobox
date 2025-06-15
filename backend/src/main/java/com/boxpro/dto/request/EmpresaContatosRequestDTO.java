package com.boxpro.dto.request;

import com.boxpro.entity.EmpresaContatos.TipoContato;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EmpresaContatosRequestDTO {
    
    @NotNull(message = "ID da empresa é obrigatório")
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
    
    // Construtores
    public EmpresaContatosRequestDTO() {}
    
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
}
