package com.boxpro.exception;

public class ErrorMessages {
    
    // Mensagens de autenticação
    public static final String INVALID_CREDENTIALS = "Email ou senha inválidos";
    public static final String TOKEN_EXPIRED = "Token expirado";
    public static final String TOKEN_INVALID = "Token inválido";
    public static final String UNAUTHORIZED = "Acesso não autorizado";
    public static final String ACCESS_DENIED = "Acesso negado";
    
    // Mensagens de validação
    public static final String VALIDATION_FAILED = "Falha na validação dos dados";
    public static final String REQUIRED_FIELD = "Campo obrigatório";
    public static final String INVALID_EMAIL = "Email inválido";
    public static final String INVALID_PHONE = "Telefone inválido";
    public static final String INVALID_DATE = "Data inválida";
    public static final String PAST_DATE = "Data deve ser futura";
    
    // Mensagens de recursos
    public static final String USER_NOT_FOUND = "Usuário não encontrado";
    public static final String SERVICE_NOT_FOUND = "Serviço não encontrado";
    public static final String VEHICLE_NOT_FOUND = "Veículo não encontrado";
    public static final String APPOINTMENT_NOT_FOUND = "Agendamento não encontrado";
    public static final String CATEGORY_NOT_FOUND = "Categoria não encontrada";
    
    // Mensagens de negócio - Usuário
    public static final String EMAIL_ALREADY_EXISTS = "Email já cadastrado";
    public static final String INVALID_PASSWORD = "Senha deve conter letras e números";
    public static final String PASSWORD_MISMATCH = "Senhas não conferem";
    public static final String CURRENT_PASSWORD_INCORRECT = "Senha atual incorreta";
    
    // Mensagens de negócio - Veículo
    public static final String PLATE_ALREADY_EXISTS = "Placa já cadastrada";
    public static final String INVALID_PLATE_FORMAT = "Formato de placa inválido";
    public static final String VEHICLE_NOT_OWNED = "Veículo não pertence ao usuário";
    public static final String VEHICLE_HAS_APPOINTMENTS = "Veículo possui agendamentos";
    
    // Mensagens de negócio - Serviço
    public static final String SERVICE_NAME_EXISTS = "Serviço já existe com este nome";
    public static final String SERVICE_INACTIVE = "Serviço não está disponível";
    public static final String SERVICE_HAS_APPOINTMENTS = "Serviço possui agendamentos";
    public static final String INVALID_PRICE = "Preço deve ser maior que zero";
    public static final String INVALID_DURATION = "Duração inválida";
    
    // Mensagens de negócio - Agendamento
    public static final String TIME_SLOT_UNAVAILABLE = "Horário não disponível";
    public static final String INVALID_APPOINTMENT_DATE = "Data de agendamento inválida";
    public static final String APPOINTMENT_CANNOT_BE_CANCELLED = "Agendamento não pode ser cancelado";
    public static final String APPOINTMENT_CANNOT_BE_RESCHEDULED = "Agendamento não pode ser remarcado";
    public static final String INVALID_STATUS_TRANSITION = "Transição de status inválida";
    public static final String OUTSIDE_BUSINESS_HOURS = "Horário fora do expediente";
    public static final String SUNDAY_NOT_AVAILABLE = "Não atendemos aos domingos";
    
    // Mensagens de negócio - Categoria
    public static final String CATEGORY_NAME_EXISTS = "Categoria já existe com este nome";
    public static final String CATEGORY_HAS_SERVICES = "Categoria possui serviços vinculados";
    
    // Mensagens gerais
    public static final String OPERATION_SUCCESS = "Operação realizada com sucesso";
    public static final String INTERNAL_ERROR = "Erro interno do servidor";
    public static final String INVALID_REQUEST = "Requisição inválida";
    public static final String DATA_INTEGRITY_VIOLATION = "Violação de integridade de dados";
    
    private ErrorMessages() {
        // Classe utilitária
    }
}