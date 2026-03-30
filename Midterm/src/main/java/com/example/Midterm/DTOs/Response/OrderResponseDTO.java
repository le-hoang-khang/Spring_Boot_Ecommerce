package com.example.Midterm.DTOs.Response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private Long id;
    private String customerName;
    private LocalDate orderDate;
    private String status;
    private Double totalPrice;
    private String shippingAddress;
    private List<OrderProductResponseDTO> products;
}