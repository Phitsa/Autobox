package com.boxpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "empresa")
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome fantasia é obrigatório")
    @Size(max = 255, message = "Nome fantasia deve ter no máximo 255 caracteres")
    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;
    
    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 255, message = "Razão social deve ter no máximo 255 caracteres")
    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;
    
    @Lob
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;
    
    @Size(max = 18, message = "CNPJ deve ter no máximo 18 caracteres")
    @Column(name = "cnpj", length = 18)
    private String cnpj;
    
    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    @Column(name = "inscricao_estadual", length = 20)
    private String inscricaoEstadual;
    
    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    @Column(name = "inscricao_municipal", length = 20)
    private String inscricaoMunicipal;
    
    @Size(max = 500, message = "Endereço deve ter no máximo 500 caracteres")
    @Column(name = "endereco", length = 500)
    private String endereco;
    
    @Size(max = 10, message = "CEP deve ter no máximo 10 caracteres")
    @Column(name = "cep", length = 10)
    private String cep;
    
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    @Column(name = "cidade", length = 100)
    private String cidade;
    
    @Size(max = 2, message = "Estado deve ter no máximo 2 caracteres")
    @Column(name = "estado", length = 2)
    private String estado;
    
    @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
    @Column(name = "numero", length = 10)
    private String numero;
    
    @Size(max = 100, message = "Complemento deve ter no máximo 100 caracteres")
    @Column(name = "complemento", length = 100)
    private String complemento;
    
    @Column(name = "ativo", nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean ativo = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Construtores
    public Empresa() {}
    
    public Empresa(String nomeFantasia, String razaoSocial) {
        this.nomeFantasia = nomeFantasia;
        this.razaoSocial = razaoSocial;
        this.ativo = true;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNomeFantasia() {
        return nomeFantasia;
    }
    
    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }
    
    public String getRazaoSocial() {
        return razaoSocial;
    }
    
    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getCnpj() {
        return cnpj;
    }
    
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    
    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }
    
    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }
    
    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }
    
    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }
    
    public String getEndereco() {
        return endereco;
    }
    
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    
    public String getCep() {
        return cep;
    }
    
    public void setCep(String cep) {
        this.cep = cep;
    }
    
    public String getCidade() {
        return cidade;
    }
    
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public String getComplemento() {
        return complemento;
    }
    
    public void setComplemento(String complemento) {
        this.complemento = complemento;
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
