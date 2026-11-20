package com.coffeeshop.repository;

import com.coffeeshop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatusAndUserId(String status, Long userId);
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
}
