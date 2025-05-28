package com.boxpro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    
    private String message;
    private Boolean success;
    private LocalDateTime timestamp;
    private Object data;
    
    public static MessageResponse success(String message) {
        return MessageResponse.builder()
                .message(message)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    public static MessageResponse success(String message, Object data) {
        return MessageResponse.builder()
                .message(message)
                .success(true)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }
    
    public static MessageResponse error(String message) {
        return MessageResponse.builder()
                .message(message)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }
}