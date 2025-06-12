package com.boxpro.dto.response;

import com.boxpro.entity.enums.TipoFuncionario;

public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private Integer id;
    private String nome;
    private String email;
    private TipoFuncionario tipoFuncionario;

    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, Integer id, String nome, String email, TipoFuncionario tipoFuncionario) {
        this.token = token;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoFuncionario = tipoFuncionario;
    }

    public AuthResponse(String token, String tipo, Integer id, String nome, String email, TipoFuncionario tipoFuncionario) {
        this.token = token;
        this.tipo = tipo;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoFuncionario = tipoFuncionario;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public TipoFuncionario getTipoFuncionario() { return tipoFuncionario; }
    public void setTipoFuncionario(TipoFuncionario tipoFuncionario) { this.tipoFuncionario = tipoFuncionario; }
}