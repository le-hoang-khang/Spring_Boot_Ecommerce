package com.example.Midterm.Repositories;

import com.example.Midterm.Entities.OrderProduct;
import com.example.Midterm.Entities.OrderProductId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductId> {
    List<OrderProduct> findByOrderId(Long orderId);
}