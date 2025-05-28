package com.boxpro.exception.handler;

import com.boxpro.dto.response.ErrorResponse;
import com.boxpro.exception.BusinessException;
import com.boxpro.exception.ResourceNotFoundException;
import com.boxpro.exception.UnauthorizedException;
import com.boxpro.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Tratar erros de validação do Bean Validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.error("Erro de validação: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    Object rejectedValue = ((FieldError) error).getRejectedValue();
                    
                    return ErrorResponse.FieldError.builder()
                            .field(fieldName)
                            .message(errorMessage)
                            .rejectedValue(rejectedValue)
                            .build();
                })
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Erro de validação")
                .error("Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Tratar erros de negócio
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        
        log.error("Erro de negócio: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(),
                "Business Error",
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Tratar recursos não encontrados
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        log.error("Recurso não encontrado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(),
                "Resource Not Found",
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Tratar erros de autenticação
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {
        
        log.error("Erro de autenticação: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                ex.getMessage(),
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Tratar erros de validação customizada
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {
        
        log.error("Erro de validação customizada: {}", ex.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        
        if (ex.getErrors() != null) {
            ex.getErrors().forEach((field, message) -> {
                fieldErrors.add(ErrorResponse.FieldError.builder()
                        .field(field)
                        .message(message)
                        .build());
            });
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Custom Validation Failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Tratar erros de acesso negado
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        log.error("Acesso negado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                "Você não tem permissão para acessar este recurso",
                "Access Denied",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Tratar erros de autenticação do Spring Security
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        
        log.error("Erro de autenticação: {}", ex.getMessage());
        
        String message = "Falha na autenticação";
        
        if (ex instanceof BadCredentialsException) {
            message = "Email ou senha inválidos";
        }
        
        ErrorResponse errorResponse = ErrorResponse.of(
                message,
                "Authentication Failed",
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Tratar violações de integridade de dados
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        
        log.error("Violação de integridade de dados: {}", ex.getMessage());
        
        String message = "Operação viola restrições de integridade do banco de dados";
        
        // Verificar se é erro de duplicação
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
            message = "Registro já existe no sistema";
        }
        
        ErrorResponse errorResponse = ErrorResponse.of(
                message,
                "Data Integrity Violation",
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Tratar parâmetros ausentes
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        
        log.error("Parâmetro ausente: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                String.format("Parâmetro obrigatório '%s' está ausente", ex.getParameterName()),
                "Missing Parameter",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Tratar tipos de argumentos incorretos
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        log.error("Tipo de argumento incorreto: {}", ex.getMessage());
        
        String message = String.format("O parâmetro '%s' deveria ser do tipo '%s'",
                ex.getName(), ex.getRequiredType().getSimpleName());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                message,
                "Type Mismatch",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Tratar mensagens HTTP não legíveis
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        log.error("Mensagem HTTP não legível: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                "Formato de requisição inválido. Verifique o JSON enviado.",
                "Malformed JSON Request",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Tratar método HTTP não suportado
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {
        
        log.error("Método HTTP não suportado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                String.format("Método %s não é suportado para esta requisição", ex.getMethod()),
                "Method Not Allowed",
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * Tratar tipo de mídia não suportado
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {
        
        log.error("Tipo de mídia não suportado: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                "Tipo de conteúdo não suportado. Use 'application/json'",
                "Unsupported Media Type",
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Tratar rota não encontrada
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpServletRequest request) {
        
        log.error("Rota não encontrada: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
                "Endpoint não encontrado",
                "Not Found",
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Tratar qualquer exceção não tratada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Erro interno do servidor: ", ex);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                "Ocorreu um erro interno no servidor",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}