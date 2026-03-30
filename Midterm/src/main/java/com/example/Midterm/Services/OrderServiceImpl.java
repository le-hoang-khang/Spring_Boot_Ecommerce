package com.example.Midterm.Services;

import com.example.Midterm.Constants.OrderStatus;
import com.example.Midterm.Constants.PaymentMethod;
import com.example.Midterm.DTOs.Request.OrderProductRequestDTO;
import com.example.Midterm.DTOs.Request.OrderRequestDTO;
import com.example.Midterm.DTOs.Response.OrderProductResponseDTO;
import com.example.Midterm.DTOs.Response.OrderResponseDTO;
import com.example.Midterm.Entities.*;
import com.example.Midterm.Exceptions.ResourceNotFoundException;
import com.example.Midterm.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;

    @Override
    public Page<OrderResponseDTO> getAll(Pageable pageable) {
        return orderRepository
                .findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public OrderResponseDTO getById(Long id) {
        return mapToResponseDTO(orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id)));
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        Account customer = accountRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setShippingPhoneNumber(orderRequest.getShippingPhoneNumber());
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.COD);

        // Decrease stock quantity, get total price, and save items
        List<OrderProduct> orderProducts = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderProductRequestDTO itemReq: orderRequest.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            // Update stock
            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            // Update items
            OrderProduct orderProduct = new OrderProduct(order, product, itemReq.getQuantity());
            orderProducts.add(orderProduct);

            // Update total price
            totalPrice += orderProduct.getPrice() * itemReq.getQuantity();
        }

        order.setOrderProducts(orderProducts);
        order.setTotalPrice(totalPrice);

        return mapToResponseDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrderFromCart(Long customerId, String address, String phone) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> createAndAddNewCart(customerId));

        if (cart.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot checkout an empty cart");
        }

        Order order = new Order();
        order.setCustomer(cart.getCustomer());
        order.setShippingAddress(address);
        order.setShippingPhoneNumber(phone);
        order.setOrderDate(LocalDate.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMethod(PaymentMethod.COD);

        // Decrease stock quantity, get total price, and save items
        List<OrderProduct> orderProducts = new ArrayList<>();
        double totalPrice = 0.0;

        for (CartProduct item: cart.getProducts()) {
            Product product = item.getProduct();

            // Update stock
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            // Update items
            OrderProduct orderProduct = new OrderProduct(order, product, item.getQuantity());
            orderProducts.add(orderProduct);

            // Update total price
            totalPrice += orderProduct.getPrice() * item.getQuantity();
        }

        order.setOrderProducts(orderProducts);
        order.setTotalPrice(totalPrice);

        // Clear cart
        cartProductRepository.deleteByCartId(cart.getId());

        return mapToResponseDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(Long id) {
        Order order = orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        // Check order status
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel a delivered order");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return mapToResponseDTO(order);
        }

        // Refund quantity in stock of products
        for (OrderProduct item : order.getOrderProducts()) {
            Product product = productRepository
                    .findById(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + item.getProduct().getId()));

            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);

        return mapToResponseDTO(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found to delete");
        }
        orderRepository.deleteById(id);
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        List<OrderProductResponseDTO> itemDTOs = order
                .getOrderProducts()
                .stream()
                .map(item -> OrderProductResponseDTO.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .priceAtPurchase(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .customerName(order.getCustomer().getFullName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(order.getShippingAddress())
                .products(itemDTOs)
                .build();
    }

    private Cart createAndAddNewCart(Long customerId) {
        Account account = accountRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
        Cart cart = new Cart();
        cart.setCustomer(account);
        return cartRepository.save(cart);
    }
}