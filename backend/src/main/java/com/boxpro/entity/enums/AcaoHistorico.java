package com.boxpro.entity.enums;

public enum AcaoHistorico {
    CRIADO("criado"),
    EDITADO("editado"),
    CANCELADO("cancelado"),
    REMARCADO("remarcado"),
    FINALIZADO("finalizado");
    
    private final String valor;
    
    AcaoHistorico(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}