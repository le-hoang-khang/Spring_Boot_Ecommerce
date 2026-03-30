package com.example.Midterm.DTOs.Response;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class CartResponseDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private List<CartProductResponseDTO> products;
    private Integer totalItems;
    private Double totalPrice;
}