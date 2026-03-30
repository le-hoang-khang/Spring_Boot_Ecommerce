package com.example.Midterm.DTOs.Request;

import com.example.Midterm.Validations.OnCreate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountRequestDTO {
    @NotBlank(message = "Username is required", groups = OnCreate.class)
    private String username;

    @NotBlank(message = "Password is required", groups = OnCreate.class)
    @Size(min = 8, max = 20, message = "Password must be from 8 to 20 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    private String address;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    private Long roleId;
}