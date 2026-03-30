package com.example.Midterm.DTOs.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String color;
    private Integer stockQuantity;
    private String brandName;
    private String categoryName;
}