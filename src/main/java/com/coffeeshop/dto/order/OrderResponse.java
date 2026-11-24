package com.coffeeshop.dto.order;

import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long userId,
        String description,
        String status,
        LocalDateTime createdAt) {
}
