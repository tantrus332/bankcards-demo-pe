package com.tantrus332.bankcards.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.tantrus332.bankcards.exception.BusinessLogicException;
import com.tantrus332.bankcards.exception.ResourceNotFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed for one or more fields");
        problemDetail.setTitle("Invalid Request Parameters");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Database integrity violation: {}", ex.getMessage());

        String detail = "Resource already exists or violates database constraints";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ProblemDetail> handleBusinessConflict(BusinessLogicException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ProblemDetail> handleNotFound(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleAuthException() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ProblemDetail> handleExpiredJwt() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Session expired. Please log in again.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AuthorizationDeniedException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "You don't have permission to perform this action");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleReadableException(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Malformed JSON request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGlobalException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal server error occurred. Please contact support."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
