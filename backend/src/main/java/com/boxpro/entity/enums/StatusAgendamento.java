package com.boxpro.entity.enums;

public enum StatusAgendamento {
    AGENDADO("agendado"),
    EM_ANDAMENTO("em_andamento"),
    FINALIZADO("finalizado"),
    CANCELADO("cancelado");
    
    private final String valor;
    
    StatusAgendamento(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}