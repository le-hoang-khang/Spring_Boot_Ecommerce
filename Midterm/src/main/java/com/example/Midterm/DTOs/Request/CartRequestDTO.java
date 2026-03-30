package com.example.Midterm.DTOs.Request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class CartRequestDTO {
    private Long customerId;

    @Valid // check all items in list
    private List<CartProductRequestDTO> items;
}