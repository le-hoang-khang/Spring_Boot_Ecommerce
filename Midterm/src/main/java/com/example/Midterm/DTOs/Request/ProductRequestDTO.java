package com.example.Midterm.DTOs.Request;

import com.example.Midterm.Validations.OnCreate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "Product name is required", groups = OnCreate.class)
    private String name;

    private String description;

    @NotNull(message = "Price is required", groups = OnCreate.class)
    @Min(value = 0, message = "Price must not have less than 0")
    private Double price;

    private Long brandId;
    private Long categoryId;
    private String color;

    @NotNull(message = "Stock quantity is required", groups = OnCreate.class)
    @Min(value = 0, message = "Stock quantity must not have less than 0")
    private Integer stockQuantity;
}