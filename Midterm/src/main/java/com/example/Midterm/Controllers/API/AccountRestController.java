package com.example.Midterm.Controllers.API;

import com.example.Midterm.DTOs.Request.AccountRequestDTO;
import com.example.Midterm.DTOs.Request.PasswordUpdateRequestDTO;
import com.example.Midterm.DTOs.Response.AccountResponseDTO;
import com.example.Midterm.DTOs.Response.ApiResponse;
import com.example.Midterm.Services.AccountService;
import com.example.Midterm.Validations.OnCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AccountRestController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AccountResponseDTO>>> getAllAccounts(
            @RequestParam(required = false) String fullName,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<AccountResponseDTO> results;
        if (fullName != null && !fullName.isEmpty()) {
            results = accountService.getByFullName(fullName, pageable);
        } else {
            results = accountService.getAll(pageable);
        }

        return ResponseEntity.ok(
                ApiResponse.<Page<AccountResponseDTO>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Get accounts successfully")
                        .data(results)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> getAccountById(@PathVariable Long id) {
        AccountResponseDTO result = accountService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<AccountResponseDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Get account successfully with id " + id)
                        .data(result)
                        .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponseDTO>> createAccount(
            @Validated(OnCreate.class) @RequestBody AccountRequestDTO request) {
        AccountResponseDTO result = accountService.save(request);

        return new ResponseEntity<>(
                ApiResponse.<AccountResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Created account. Cart is created for this account")
                        .data(result)
                        .build(),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponseDTO>> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountRequestDTO request) {
        AccountResponseDTO result = accountService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<AccountResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Updated account with id " + id)
                        .data(result)
                        .build());
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateRequestDTO request) {
        accountService.updatePassword(id, request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Changed password successfully")
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        accountService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Deleted account with id " + id)
                        .build());
    }
}