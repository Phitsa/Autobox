package com.boxpro.dto.response;

import com.boxpro.entity.enums.TipoFuncionario;
import java.time.LocalDateTime;

public class FuncionarioResponseDTO {
    
    private Integer id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private TipoFuncionario tipoFuncionario;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime ultimoLogin;
    private Integer tentativasLogin;
    private Boolean bloqueado;

    // Construtores
    public FuncionarioResponseDTO() {}

    public FuncionarioResponseDTO(Integer id, String nome, String email, String telefone, String cpf,
                                 TipoFuncionario tipoFuncionario, Boolean ativo, LocalDateTime dataCriacao,
                                 LocalDateTime dataAtualizacao, LocalDateTime ultimoLogin, 
                                 Integer tentativasLogin, Boolean bloqueado) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.tipoFuncionario = tipoFuncionario;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
        this.ultimoLogin = ultimoLogin;
        this.tentativasLogin = tentativasLogin;
        this.bloqueado = bloqueado;
    }

    // Método estático para converter de Funcionario para DTO
    public static FuncionarioResponseDTO fromEntity(com.boxpro.entity.Funcionario funcionario) {
        if (funcionario == null) return null;
        
        return new FuncionarioResponseDTO(
            funcionario.getId(),
            funcionario.getNome(),
            funcionario.getEmail(),
            funcionario.getTelefone(),
            funcionario.getCpf(),
            funcionario.getTipoFuncionario(),
            funcionario.getAtivo(),
            funcionario.getDataCriacao(),
            funcionario.getDataAtualizacao(),
            funcionario.getUltimoLogin(),
            funcionario.getTentativasLogin(),
            funcionario.getBloqueado()
        );
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public TipoFuncionario getTipoFuncionario() { return tipoFuncionario; }
    public void setTipoFuncionario(TipoFuncionario tipoFuncionario) { this.tipoFuncionario = tipoFuncionario; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }

    public Integer getTentativasLogin() { return tentativasLogin; }
    public void setTentativasLogin(Integer tentativasLogin) { this.tentativasLogin = tentativasLogin; }

    public Boolean getBloqueado() { return bloqueado; }
    public void setBloqueado(Boolean bloqueado) { this.bloqueado = bloqueado; }
}