package com.example.Midterm.DTOs.Request;

import com.example.Midterm.Validations.OnCreate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartProductRequestDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1", groups = OnCreate.class)
    private Integer quantity;
}