package com.example.studycore.infrastructure.api.exception;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.DomainException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final int NOT_FOUND = HttpStatus.NOT_FOUND.value();
    private static final int UNPROCESSABLE_ENTITY = HttpStatus.UNPROCESSABLE_ENTITY.value();
    private static final int BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    private static final int UNAUTHORIZED = HttpStatus.UNAUTHORIZED.value();
    private static final int INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("✗ BUSINESS_EXCEPTION | status=422 | message={}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(),
                UNPROCESSABLE_ENTITY,
                ex.getMessage(),
                null)
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.warn("✗ NOT_FOUND_EXCEPTION | status=404 | message={}", ex.getMessage());
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(
                        Instant.now(),
                        NOT_FOUND,
                        ex.getMessage(),
                        null)
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("✗ ILLEGAL_ARGUMENT_EXCEPTION | status=400 | message={}", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(
                                Instant.now(),
                                BAD_REQUEST,
                                ex.getMessage(),
                                null
                        )
                );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException ex) {
        log.warn("✗ DOMAIN_EXCEPTION | status=401 | message={}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse(
                Instant.now(),
                UNAUTHORIZED,
                ex.getMessage(),
                null)
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("✗ UNAUTHORIZED_EXCEPTION | status=401 | message={}", ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED)
                .body(new ErrorResponse(
                        Instant.now(),
                        UNAUTHORIZED,
                        ex.getMessage(),
                        null)
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(java.util.stream.Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value" : fieldError.getDefaultMessage(),
                        (first, second) -> first
                ));

        log.warn("✗ VALIDATION_EXCEPTION | status=400 | errors={}", errors);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        BAD_REQUEST,
                        "Validation failed.",
                        errors)
                );
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

        log.warn("✗ TYPE_MISMATCH_EXCEPTION | status=400 | param={} | value={} | message={}", parameterName, invalidValue, message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        BAD_REQUEST,
                        message,
                        null)
                );
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

        log.warn("✗ TYPE_MISMATCH_EXCEPTION | status=400 | type={} | value={} | message={}", parameterType, invalidValue, message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                        Instant.now(),
                        BAD_REQUEST,
                        message,
                        null)
                );
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

        log.warn("✗ CONVERSION_FAILED_EXCEPTION | status=400 | type={} | value={} | message={}", targetType, invalidValue, message);
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(
                                Instant.now(),
                                BAD_REQUEST,
                                message,
                                null
                        )
                );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "✗ NO_RESOURCE_FOUND_EXCEPTION | status=404 | method={} | path={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "Resource not found",
                        null
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        String message = "Invalid request body.";

        Throwable cause = ex.getCause();

        if (cause != null && cause.getMessage() != null) {

            if (cause.getMessage().contains("UUID")) {
                message = "Invalid UUID format in request body.";
            }

            if (cause.getMessage().contains("Cannot deserialize")) {
                message = "Invalid field format in request body.";
            }
        }

        log.warn(
                "✗ JSON_PARSE_EXCEPTION | status=400 | method={} | path={} | message={}",
                request.getMethod(),
                request.getRequestURI(),
                message
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        message,
                        null
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {

        StackTraceElement origin = ex.getStackTrace()[0];

        log.error(
                "✗ UNEXPECTED_EXCEPTION | status=500 | method={} | path={} | class={} | methodOrigin={} | line={} | message={} | exception={}",
                request.getMethod(),
                request.getRequestURI(),
                origin.getClassName(),
                origin.getMethodName(),
                origin.getLineNumber(),
                ex.getMessage(),
                ex.getClass().getSimpleName()
        );

        String message = ex.getMessage() == null ? "Unexpected error." : ex.getMessage();

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        Instant.now(),
                        INTERNAL_SERVER_ERROR,
                        message,
                        null
                ));
    }

    public record ErrorResponse(
            Instant timestamp,
            int status,
            String message,
            Map<String, String> errors
    ) {
    }
}
