package com.example.Midterm.DTOs.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderProductResponseDTO {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double priceAtPurchase;
}