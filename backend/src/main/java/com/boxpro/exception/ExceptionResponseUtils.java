package com.boxpro.util;

import com.boxpro.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExceptionResponseUtils {
    
    /**
     * Criar resposta de erro básica
     */
    public ErrorResponse createErrorResponse(String message, String error, 
                                           HttpStatus status, String path) {
        return ErrorResponse.builder()
                .message(message)
                .error(error)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
    
    /**
     * Criar resposta de erro com detalhes de campo
     */
    public ErrorResponse createValidationErrorResponse(String message, 
                                                     List<ErrorResponse.FieldError> fieldErrors,
                                                     String path) {
        return ErrorResponse.builder()
                .message(message)
                .error("Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(path)
                .errors(fieldErrors)
                .build();
    }
    
    /**
     * Extrair path da requisição
     */
    public String extractPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        
        if (queryString != null) {
            path += "?" + queryString;
        }
        
        return path;
    }
    
    /**
     * Sanitizar mensagem de erro para não expor informações sensíveis
     */
    public String sanitizeErrorMessage(String message) {
        if (message == null) {
            return "Erro interno do servidor";
        }
        
        // Remove informações sensíveis comuns
        message = message.replaceAll("(?i)(password|senha|token|secret)=[^\\s]+", "$1=***");
        
        // Remove stack traces
        if (message.contains("at ") && message.contains(".java:")) {
            return "Erro interno do servidor";
        }
        
        return message;
    }
    
    /**
     * Determinar se deve mostrar detalhes do erro baseado no ambiente
     */
    public boolean shouldShowErrorDetails(String profile) {
        return "dev".equalsIgnoreCase(profile) || "development".equalsIgnoreCase(profile);
    }
    
    /**
     * Criar mensagem amigável baseada no status HTTP
     */
    public String getFriendlyMessage(HttpStatus status) {
        switch (status) {
            case BAD_REQUEST:
                return "Requisição inválida. Verifique os dados enviados.";
            case UNAUTHORIZED:
                return "Você precisa estar autenticado para acessar este recurso.";
            case FORBIDDEN:
                return "Você não tem permissão para acessar este recurso.";
            case NOT_FOUND:
                return "O recurso solicitado não foi encontrado.";
            case METHOD_NOT_ALLOWED:
                return "Método HTTP não permitido para este recurso.";
            case CONFLICT:
                return "Conflito ao processar a requisição. O recurso pode já existir.";
            case UNPROCESSABLE_ENTITY:
                return "Não foi possível processar a requisição devido a erros de validação.";
            case INTERNAL_SERVER_ERROR:
                return "Ocorreu um erro interno. Por favor, tente novamente mais tarde.";
            case SERVICE_UNAVAILABLE:
                return "Serviço temporariamente indisponível. Por favor, tente novamente mais tarde.";
            default:
                return "Ocorreu um erro ao processar sua requisição.";
        }
    }
    
    /**
     * Mapear exceção para status HTTP apropriado
     */
    public HttpStatus mapExceptionToStatus(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (ex instanceof SecurityException) {
            return HttpStatus.FORBIDDEN;
        } else if (ex instanceof UnsupportedOperationException) {
            return HttpStatus.NOT_IMPLEMENTED;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}