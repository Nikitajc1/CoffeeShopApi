package com.coffeeshop.service;

import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.User;
import com.coffeeshop.exception.OrderNotFoundException;
import com.coffeeshop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        User user = new User("Test", "asd@asd.asd");
        Order order = new Order(user, "asd", "NEW");
        order.setId(1L);
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        Order result = orderService.createOrder(user, "asd", "NEW");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(captor.capture());

        Order capturedOrder = captor.getValue();

        assertAll(
                () -> assertNotNull(result),
                () -> assertSame(order, result),
                () -> assertSame(user, capturedOrder.getUser()),
                () -> assertEquals("asd", capturedOrder.getDescription()),
                () -> assertEquals("NEW", capturedOrder.getStatus())
        );
    }

    @Test
    void shouldGetOrderById() {
        User user = new User("Test", "asd@asd.asd");
        Order order = new Order(user, "asd", "NEW");
        order.setId(1L);
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findById(order.getId()))
                .thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(order.getId());

        verify(orderRepository, times(1)).findById(order.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(order.getId(), result.getId()),
                () -> assertSame(order.getUser(), result.getUser()),
                () -> assertEquals(order.getDescription(), result.getDescription()),
                () -> assertEquals(order.getStatus(), result.getStatus()),
                () -> assertEquals(order.getCreatedAt(), result.getCreatedAt())
        );
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        Long orderId = 1L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertEquals(
                "Order with id 1 not found",
                assertThrows(OrderNotFoundException.class, () -> {
                    orderService.getOrderById(orderId);
                }).getMessage()
        );
        verify(orderRepository, times(1))
                .findById(orderId);
    }
}
