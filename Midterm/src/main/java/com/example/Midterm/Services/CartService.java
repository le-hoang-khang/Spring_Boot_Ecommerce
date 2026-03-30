package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.CartProductRequestDTO;
import com.example.Midterm.DTOs.Response.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCartByCustomer(Long customerId);
    void addToCart(CartProductRequestDTO request, Long customerId);
    void updateQuantity(Long productId, Integer quantity, Long customerId);
    void removeFromCart(Long productId, Long customerId);
    void clearCart(Long customerId);
}