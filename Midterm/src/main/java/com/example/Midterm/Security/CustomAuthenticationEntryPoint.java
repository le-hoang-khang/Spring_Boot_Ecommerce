package com.example.Midterm.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver resolver;

    // HandlerExceptionResolver had many @Bean construction
    // We need to choose exactly @Bean to inject by using @Qualifier
    // But @Qualifier is only for field injection and constructor injection
    // Lombok @RequiredArgsConstructor only find 1 @Bean per Object
    // Se we create constructor and add @Qualifier for parameters
    @Autowired
    public CustomAuthenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthenticated");

        // Using HandlerExceptionResolver to send exception to Controller Advice
        // instead of response.sendError sends to security filter
        resolver.resolveException(
                request,
                response,
                null,
                authException);
    }
}