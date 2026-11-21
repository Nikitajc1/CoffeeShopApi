package com.coffeeshop.service;

import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.User;
import com.coffeeshop.exception.OrderNotFoundException;
import com.coffeeshop.exception.UserNotFoundException;
import com.coffeeshop.repository.OrderRepository;
import com.coffeeshop.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public Order createOrder(
            Long userId,
            String description,
            String status ) {
        return orderRepository.save(new Order(
                userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId)),
                description,
                status
        ));
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
