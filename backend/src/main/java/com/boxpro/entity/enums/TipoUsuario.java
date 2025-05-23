package com.boxpro.entity.enums;

public enum TipoUsuario {
    CLIENTE("cliente"),
    ADMINISTRADOR("administrador");
    
    private final String valor;
    
    TipoUsuario(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}