package com.example.Midterm.DTOs.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandRequestDTO {
    @NotBlank(message = "Brand name is required")
    private String name;
    private String description;
}