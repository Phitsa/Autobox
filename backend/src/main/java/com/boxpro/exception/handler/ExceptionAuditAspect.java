package com.boxpro.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ExceptionAuditAspect {
    
    /**
     * Auditar todas as exceções lançadas nos controllers
     */
    @AfterThrowing(
        pointcut = "within(@org.springframework.web.bind.annotation.RestController *)",
        throwing = "exception"
    )
    public void auditControllerExceptions(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.error("=== EXCEÇÃO CAPTURADA ===");
        log.error("Timestamp: {}", LocalDateTime.now());
        log.error("Classe: {}", className);
        log.error("Método: {}", methodName);
        log.error("Argumentos: {}", Arrays.toString(args));
        log.error("Exceção: {}", exception.getClass().getSimpleName());
        log.error("Mensagem: {}", exception.getMessage());
        log.error("Stack trace:", exception);
        log.error("========================");
    }
    
    /**
     * Auditar exceções críticas nos services
     */
    @AfterThrowing(
        pointcut = "within(@org.springframework.stereotype.Service *)",
        throwing = "exception"
    )
    public void auditServiceExceptions(JoinPoint joinPoint, Exception exception) {
        // Apenas logar exceções que não são de negócio
        if (!(exception instanceof com.boxpro.exception.BusinessException) &&
            !(exception instanceof com.boxpro.exception.ResourceNotFoundException)) {
            
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();
            
            log.error("Exceção crítica no service {}.{}: {}", 
                     className, methodName, exception.getMessage(), exception);
        }
    }
}