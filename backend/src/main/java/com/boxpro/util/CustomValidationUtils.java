package com.boxpro.util;

import com.boxpro.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class CustomValidationUtils {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\(?[1-9]{2}\\)?\\s?9?[0-9]{4}-?[0-9]{4}$"
    );
    
    private static final Pattern PLATE_PATTERN = Pattern.compile(
        "^[A-Z]{3}[0-9]{1}[A-Z0-9]{1}[0-9]{2}$"
    );
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$"
    );
    
    /**
     * Validar email
     */
    public void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Email inválido");
        }
    }
    
    /**
     * Validar telefone brasileiro
     */
    public void validatePhone(String phone) {
        if (phone != null && !phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Telefone inválido. Use formato: (11) 91234-5678");
        }
    }
    
    /**
     * Validar placa de veículo
     */
    public void validatePlate(String plate) {
        if (plate == null || !PLATE_PATTERN.matcher(plate.toUpperCase()).matches()) {
            throw new ValidationException("Placa inválida. Use formato: ABC1234 ou ABC1D23");
        }
    }
    
    /**
     * Validar senha
     */
    public void validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ValidationException("Senha deve ter no mínimo 6 caracteres, contendo letras e números");
        }
    }
    
    /**
     * Validar data futura
     */
    public void validateFutureDate(LocalDate date) {
        if (date == null || !date.isAfter(LocalDate.now())) {
            throw new ValidationException("Data deve ser futura");
        }
    }
    
    /**
     * Validar horário de funcionamento
     */
    public void validateBusinessHours(LocalTime time, LocalTime openTime, LocalTime closeTime) {
        if (time.isBefore(openTime) || time.isAfter(closeTime)) {
            throw new ValidationException(
                String.format("Horário deve estar entre %s e %s", openTime, closeTime)
            );
        }
    }
    
    /**
     * Validar intervalo de datas
     */
    public void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Datas de início e fim são obrigatórias");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Data inicial não pode ser posterior à data final");
        }
    }
    
    /**
     * Validar múltiplos campos
     */
    public void validateFields(Map<String, ValidationRule> rules) {
        Map<String, String> errors = new HashMap<>();
        
        rules.forEach((field, rule) -> {
            try {
                rule.validate();
            } catch (Exception e) {
                errors.put(field, e.getMessage());
            }
        });
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Erros de validação encontrados", errors);
        }
    }
    
    /**
     * Interface funcional para regras de validação
     */
    @FunctionalInterface
    public interface ValidationRule {
        void validate() throws Exception;
    }
    
    /**
     * Validar CPF (algoritmo completo)
     */
    public boolean isValidCPF(String cpf) {
        if (cpf == null) return false;
        
        // Remove caracteres especiais
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;
        
        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) return false;
        
        // Calcula os dígitos verificadores
        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = Integer.parseInt(cpf.substring(i, i + 1));
        }
        
        // Primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int firstVerifier = 11 - (sum % 11);
        if (firstVerifier >= 10) firstVerifier = 0;
        
        if (firstVerifier != digits[9]) return false;
        
        // Segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        int secondVerifier = 11 - (sum % 11);
        if (secondVerifier >= 10) secondVerifier = 0;
        
        return secondVerifier == digits[10];
    }
}