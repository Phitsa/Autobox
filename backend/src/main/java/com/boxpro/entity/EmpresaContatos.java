package com.boxpro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "empresa_contatos", indexes = {
        @Index(name = "idx_empresa_contatos_empresa_id", columnList = "empresa_id"),
        @Index(name = "idx_empresa_contatos_tipo", columnList = "tipo_contato"),
        @Index(name = "idx_empresa_contatos_principal", columnList = "principal")
})
public class EmpresaContatos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Empresa é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_empresa_contatos_empresa"))
    private Empresa empresa;

    @NotNull(message = "Tipo de contato é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contato", nullable = false)
    private TipoContato tipoContato;

    @NotBlank(message = "Valor do contato é obrigatório")
    @Size(max = 255, message = "Valor deve ter no máximo 255 caracteres")
    @Column(name = "valor", nullable = false)
    private String valor;

    @Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres")
    @Column(name = "descricao")
    private String descricao;

    @Column(name = "principal", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean principal = false;

    @Column(name = "ativo", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enum corrigido para corresponder EXATAMENTE ao banco de dados (MAIÚSCULO)
    public enum TipoContato {
        TELEFONE("TELEFONE", "Telefone"),
        CELULAR("CELULAR", "Celular"), 
        WHATSAPP("WHATSAPP", "WhatsApp"),
        EMAIL("EMAIL", "E-mail"),
        FAX("FAX", "Fax");

        private final String codigo;
        private final String nome;

        TipoContato(String codigo, String nome) {
            this.codigo = codigo;
            this.nome = nome;
        }

        public String getCodigo() {
            return codigo;
        }

        public String getNome() {
            return nome;
        }

        public static TipoContato fromCodigo(String codigo) {
            for (TipoContato tipo : values()) {
                if (tipo.codigo.equalsIgnoreCase(codigo)) {
                    return tipo;
                }
            }
            throw new IllegalArgumentException("Código de tipo de contato inválido: " + codigo);
        }

        // Método para compatibilidade com strings em qualquer case
        public static TipoContato fromString(String str) {
            if (str == null) return null;
            
            // Converter para maiúscula e tentar match direto
            String upperStr = str.toUpperCase();
            
            for (TipoContato tipo : values()) {
                if (tipo.name().equals(upperStr) || tipo.codigo.equals(upperStr)) {
                    return tipo;
                }
            }
            
            // Fallback para compatibilidade com versões antigas (minúsculas do frontend)
            switch (upperStr) {
                case "TELEFONE": return TELEFONE;
                case "CELULAR": return CELULAR;
                case "WHATSAPP": return WHATSAPP;
                case "EMAIL": return EMAIL;
                case "FAX": return FAX;
                default:
                    throw new IllegalArgumentException("Tipo de contato inválido: " + str);
            }
        }

        // Método para retornar valor em minúscula (compatibilidade com frontend)
        public String getValorFrontend() {
            return this.name().toLowerCase();
        }
    }

    // Construtores
    public EmpresaContatos() {
    }

    public EmpresaContatos(Empresa empresa, TipoContato tipoContato, String valor) {
        this.empresa = empresa;
        this.tipoContato = tipoContato;
        this.valor = valor;
        this.principal = false;
        this.ativo = true;
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
    public String getNomeTipoContato() {
        return tipoContato.getNome();
    }

    public String getValorFormatado() {
        if (tipoContato == TipoContato.TELEFONE || tipoContato == TipoContato.CELULAR
                || tipoContato == TipoContato.WHATSAPP || tipoContato == TipoContato.FAX) {
            return formatarTelefone(valor);
        }
        return valor;
    }

    private String formatarTelefone(String telefone) {
        if (telefone == null) return null;
        
        String numbers = telefone.replaceAll("\\D", "");
        if (numbers.length() == 11) {
            return String.format("(%s) %s-%s",
                    numbers.substring(0, 2),
                    numbers.substring(2, 7),
                    numbers.substring(7));
        } else if (numbers.length() == 10) {
            return String.format("(%s) %s-%s",
                    numbers.substring(0, 2),
                    numbers.substring(2, 6),
                    numbers.substring(6));
        }
        return telefone;
    }

    @Override
    public String toString() {
        return "EmpresaContatos{" +
                "id=" + id +
                ", empresaId=" + (empresa != null ? empresa.getId() : null) +
                ", tipoContato=" + tipoContato +
                ", valor='" + valor + '\'' +
                ", descricao='" + descricao + '\'' +
                ", principal=" + principal +
                ", ativo=" + ativo +
                '}';
    }
}