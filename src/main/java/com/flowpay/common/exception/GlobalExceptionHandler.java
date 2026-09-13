package com.flowpay.common.exception;

import com.flowpay.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(
            AppException exception
    ){
        ErrorCode errorCode = exception.getErrorCode();
        String traceId = createTraceId();

        log.warn("Business exception: code={}, traceId={}", errorCode.getCode(), traceId);

        ErrorResponse response = new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                traceId,
                Instant.now()
        );
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                ));
        String traceId = createTraceId();
        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.getCode(),
                ErrorCode.VALIDATION_ERROR.getMessage(),
                errors,
                traceId,
                Instant.now()
        );
        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(
            Exception exception
    ){
        String traceId = createTraceId();
        log.error("Unexpected exception: traceId={}", traceId, exception);
        ErrorResponse response = new ErrorResponse(
                ErrorCode.INTERNAL_SERVICE_ERROR.getCode(),
                ErrorCode.INTERNAL_SERVICE_ERROR.getMessage(),
                null,
                traceId,
                Instant.now()
        );

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVICE_ERROR.getHttpStatus())
                .body(response);
    }

    private String createTraceId() {
        return UUID.randomUUID().toString();
    }
}
