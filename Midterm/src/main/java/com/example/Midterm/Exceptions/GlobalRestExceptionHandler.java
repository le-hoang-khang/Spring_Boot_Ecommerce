package com.example.Midterm.Exceptions;

import com.example.Midterm.DTOs.Response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    /*
     * 400
     */
    // Wrong old password
    @ExceptionHandler(IncorrectOldPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleIncorrectOldPasswordException(IncorrectOldPasswordException e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(e.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    // Validate request body
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(
                fieldError -> errors.put(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()));

        return new ResponseEntity<>(
                ApiResponse.<Map<String, String>>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid data")
                        .errors(errors)
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    // Incorrect field's type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Param '" + e.getName() + "' must be '" + e.getRequiredType().getSimpleName() + "' data type")
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    // Missing request param
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Param '" + e.getParameterName() + "' is required")
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    // Incorrect request body's JSON type
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Missing request body or wrong JSON format")
                        .build(),
                HttpStatus.BAD_REQUEST);
    }


    /*
     * 401
     */
    // Incorrect username or password (from UsernamePasswordAuthenticationToken)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Incorrect username or password")
                        .build());
    }


    // No authentication/no token
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Authentication is required")
                        .build());
    }


    /*
     * 403/401
     */
    // Handle @PreAuthorized (special case)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
        // Special case: @PreAuthorized only throw 403 exception
        // so we need to check anonymous user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.<Void>builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Authentication is required")
                            .build());
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("You do not have the required permission")
                        .build());
    }


    /*
     * 404
     */
    // Resource not found in db
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(e.getMessage())
                        .build());
    }

    // Url not found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message("URL is not exists: " + e.getResourcePath())
                        .build());
    }

    // User not found (wrong username of token)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(e.getMessage())
                        .build());
    }


    /*
     * 405
     */
    // Method not allow
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                        .message("Method '" + e.getMethod() + "' is not allowed")
                        .build(),
                HttpStatus.METHOD_NOT_ALLOWED);
    }


    /*
     * 409
     */
    // Duplicate resource in db
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResourceException(DuplicateResourceException e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(e.getMessage())
                        .build(),
                HttpStatus.CONFLICT);
    }


    /*
     * 415
     */
    // Incorrect request body's content type
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                        .message("Unsupported content type: " + e.getContentType())
                        .build(),
                HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }


    /*
     * 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAnyException(Exception e) {
        return new ResponseEntity<>(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Internal server error: " + e.getMessage())
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}