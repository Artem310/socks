package com.warehouse.socks.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

//Глобальный обработчик исключений
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Обработка ошибок валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    //Обработка ошибки недостаточного количества носков
    @ExceptionHandler(InsufficientSocksException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientSocksException(
            InsufficientSocksException ex
    ) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    //Обработка ошибки некорректной операции
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOperationException(
            InvalidOperationException ex
    ) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    //Обработка остальных исключений
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        log.error("Unexpected error", ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Внутренняя ошибка сервера");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
