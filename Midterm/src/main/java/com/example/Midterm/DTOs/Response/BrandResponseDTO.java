package com.example.Midterm.DTOs.Response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandResponseDTO {
    private Long id;
    private String name;
    private String description;
    private int totalProducts;
}