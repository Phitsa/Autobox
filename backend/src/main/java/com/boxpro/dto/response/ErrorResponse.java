package com.boxpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    
    private String message;
    private String error;
    private Integer status;
    private LocalDateTime timestamp;
    private String path;
    private List<FieldError> errors;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
    
    public static ErrorResponse of(String message, String error, Integer status, String path) {
        return ErrorResponse.builder()
                .message(message)
                .error(error)
                .status(status)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}