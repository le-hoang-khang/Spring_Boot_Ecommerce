package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.OrderRequestDTO;
import com.example.Midterm.DTOs.Response.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderResponseDTO> getAll(Pageable pageable);
    OrderResponseDTO getById(Long id);
    OrderResponseDTO createOrder(OrderRequestDTO orderRequest);
    OrderResponseDTO createOrderFromCart(Long customerId, String address, String phone);
    OrderResponseDTO cancelOrder(Long id);
    void delete(Long id);
}