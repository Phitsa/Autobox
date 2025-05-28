package com.boxpro.exception;

public enum ApiErrorCode {
    
    // Códigos de autenticação (1000-1099)
    INVALID_CREDENTIALS(1001, "Credenciais inválidas"),
    TOKEN_EXPIRED(1002, "Token expirado"),
    TOKEN_INVALID(1003, "Token inválido"),
    UNAUTHORIZED(1004, "Não autorizado"),
    ACCESS_DENIED(1005, "Acesso negado"),
    
    // Códigos de validação (1100-1199)
    VALIDATION_FAILED(1100, "Falha na validação"),
    REQUIRED_FIELD(1101, "Campo obrigatório"),
    INVALID_FORMAT(1102, "Formato inválido"),
    INVALID_DATE_RANGE(1103, "Intervalo de datas inválido"),
    
    // Códigos de recursos não encontrados (1200-1299)
    RESOURCE_NOT_FOUND(1200, "Recurso não encontrado"),
    USER_NOT_FOUND(1201, "Usuário não encontrado"),
    SERVICE_NOT_FOUND(1202, "Serviço não encontrado"),
    VEHICLE_NOT_FOUND(1203, "Veículo não encontrado"),
    APPOINTMENT_NOT_FOUND(1204, "Agendamento não encontrado"),
    
    // Códigos de conflito (1300-1399)
    DUPLICATE_RESOURCE(1300, "Recurso duplicado"),
    EMAIL_ALREADY_EXISTS(1301, "Email já existe"),
    PLATE_ALREADY_EXISTS(1302, "Placa já existe"),
    TIME_SLOT_CONFLICT(1303, "Conflito de horário"),
    
    // Códigos de regras de negócio (1400-1499)
    BUSINESS_RULE_VIOLATION(1400, "Violação de regra de negócio"),
    INSUFFICIENT_PERMISSION(1401, "Permissão insuficiente"),
    INVALID_STATUS_TRANSITION(1402, "Transição de status inválida"),
    CANCELLATION_NOT_ALLOWED(1403, "Cancelamento não permitido"),
    
    // Códigos de sistema (1500-1599)
    INTERNAL_SERVER_ERROR(1500, "Erro interno do servidor"),
    DATABASE_ERROR(1501, "Erro de banco de dados"),
    EXTERNAL_SERVICE_ERROR(1502, "Erro em serviço externo");
    
    private final int code;
    private final String message;
    
    ApiErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}