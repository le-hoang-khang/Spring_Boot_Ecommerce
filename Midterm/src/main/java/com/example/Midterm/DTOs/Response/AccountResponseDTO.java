package com.example.Midterm.DTOs.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String address;
    private String phoneNumber;
    private String roleName;
}