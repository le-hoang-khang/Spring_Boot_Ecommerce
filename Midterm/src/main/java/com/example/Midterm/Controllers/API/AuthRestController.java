package com.example.Midterm.Controllers.API;

import com.example.Midterm.DTOs.Request.AccountRequestDTO;
import com.example.Midterm.DTOs.Request.LoginRequestDTO;
import com.example.Midterm.DTOs.Response.AccountResponseDTO;
import com.example.Midterm.DTOs.Response.ApiResponse;
import com.example.Midterm.Security.JwtTokenProvider;
import com.example.Midterm.Services.AccountService;
import com.example.Midterm.Services.BlackListTokenService;
import com.example.Midterm.Validations.OnCreate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AccountService accountService;
    private final BlackListTokenService blackListTokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(
            @Valid @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        String jwt = tokenProvider.generateToken(authentication.getName());

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Login successfully")
                .data(jwt)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> register(
            @Validated(OnCreate.class) @RequestBody AccountRequestDTO request) {
        AccountResponseDTO result = accountService.save(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<AccountResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Registration successful. Please login")
                        .data(result)
                        .build());
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        // Get token from header and add to blacklist
        String authorization = request.getHeader("Authorization");
        String prefix = "Bearer ";

        if (authorization!= null && authorization.startsWith(prefix)) {
            String token = authorization.substring(prefix.length());
            if (token != null && !token.isEmpty()) {
                blackListTokenService.invalidateToken(token);
            }
        }

        // Clear context
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Logged out successfully")
                .build());
    }
}