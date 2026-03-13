package com.example.studycore.infrastructure.api.exception;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.DomainException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.exception.UnauthorizedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(Instant.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(Instant.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(new ErrorResponse(Instant.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(Instant.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(Instant.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage(),
                        (first, second) -> first
                ));

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Instant.now(), 400, "Validation failed.", errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        final var parameterName = ex.getName();
        final var parameterType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        final var invalidValue = ex.getValue();

        String message;
        if ("UUID".equals(parameterType)) {
            message = String.format("Invalid UUID format: '%s'. Expected valid UUID.", invalidValue);
        } else {
            message = String.format("Invalid %s format: '%s'. Expected valid %s.",
                    parameterName, invalidValue, parameterType);
        }

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Instant.now(), 400, message, null));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(TypeMismatchException ex) {
        final var parameterType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        final var invalidValue = ex.getValue();

        String message;
        if ("UUID".equals(parameterType)) {
            message = String.format("Invalid UUID format: '%s'. Expected valid UUID.", invalidValue);
        } else {
            message = String.format("Invalid parameter format: '%s'. Expected valid %s.",
                    invalidValue, parameterType);
        }

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Instant.now(), 400, message, null));
    }

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<ErrorResponse> handleConversionFailed(ConversionFailedException ex) {
        final var invalidValue = ex.getValue();
        final var targetType = ex.getTargetType().getType().getSimpleName();

        String message;
        if ("UUID".equals(targetType)) {
            message = String.format("Invalid UUID format: '%s'. Expected valid UUID.", invalidValue);
        } else {
            message = String.format("Invalid format: '%s'. Expected valid %s.", invalidValue, targetType);
        }

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(Instant.now(), 400, message, null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(Instant.now(), 500, ex.getMessage() == null ? "Unexpected error." : ex.getMessage(), null));
    }

    public record ErrorResponse(
            Instant timestamp,
            int status,
            String message,
            Map<String, String> errors
    ) {
    }
}
