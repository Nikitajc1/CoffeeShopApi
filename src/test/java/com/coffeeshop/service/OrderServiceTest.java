package com.coffeeshop.service;

import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.User;
import com.coffeeshop.exception.OrderNotFoundException;
import com.coffeeshop.exception.UserNotFoundException;
import com.coffeeshop.repository.OrderRepository;
import com.coffeeshop.repository.UserRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        User user = new User("Test", "asd@asd.asd");
        user.setId(1L);
        Order order = new Order(user, "asd", "NEW");
        order.setId(1L);
        order.setCreatedAt(LocalDateTime.now());

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        Order result = orderService.createOrder(1L, "asd", "NEW");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(captor.capture());
        verify(userRepository, times(1)).findById(1L);

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

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertEquals("User with id 1 not found",
                assertThrows(UserNotFoundException.class, () -> {
                    orderService.createOrder(userId, "asd", "NEW");
                }).getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldGetOrdersByUserId() {
        User user = new User("Test", "asd@asd.asd");
        user.setId(1L);
        Order order = new Order(user, "asd", "NEW");
        List<Order> orders = List.of(order);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByUserId(user.getId()))
                .thenReturn(orders);

        List<Order> result = orderService.getOrdersByUserId(user.getId());

        assertEquals(1, result.size());
        assertSame(order, result.getFirst());

        verify(userRepository, times(1)).findById(user.getId());
        verify(orderRepository, times(1)).findByUserId(user.getId());

    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoOrders() {
        User user = new User("Test", "asd@asd.asd");
        user.setId(1L);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByUserId(user.getId()))
                .thenReturn(List.of());

        List<Order> result = orderService.getOrdersByUserId(user.getId());

        assertEquals(0, result.size());

        verify(userRepository, times(1)).findById(user.getId());
        verify(orderRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundWhenGetOrders() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertEquals("User with id 1 not found",
                assertThrows(UserNotFoundException.class, () -> {
                    orderService.getOrdersByUserId(userId);
                }).getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(orderRepository, never()).findByUserId(userId);
    }

    @Test
    void shouldGetOrdersByStatusAndUserId() {
        User user = new User("Test", "asd@asd.asd");
        user.setId(1L);
        Order order = new Order(user, "asd", "NEW");
        List<Order> orders = List.of(order);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByStatusAndUserId(order.getStatus(), user.getId()))
                .thenReturn(orders);

        List<Order> result = orderService
                .getOrdersByStatusAndUserId(order.getStatus(), user.getId());

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertSame(order, result.getFirst()),
                () -> assertEquals("NEW", result.getFirst().getStatus())
        );

        verify(userRepository, times(1)).findById(user.getId());
        verify(orderRepository, times(1))
                .findByStatusAndUserId(order.getStatus(), user.getId());

    }

    @Test
    void shouldReturnEmptyListWhenNoOrdersWithSpecifiedStatus() {
        User user = new User("Test", "asd@asd.asd");
        user.setId(1L);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        when(orderRepository.findByStatusAndUserId("NEW", user.getId()))
                .thenReturn(List.of());

        List<Order> result = orderService
                .getOrdersByStatusAndUserId("NEW", user.getId());

        assertEquals(0, result.size());

        verify(userRepository, times(1)).findById(user.getId());
        verify(orderRepository, times(1))
                .findByStatusAndUserId("NEW", user.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundWhenGetOrdersByStatus() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertEquals("User with id 1 not found",
                assertThrows(UserNotFoundException.class, () -> {
                    orderService.getOrdersByStatusAndUserId("NEW", userId);
                }).getMessage());


        verify(userRepository, times(1)).findById(userId);
        verify(orderRepository, never())
                .findByStatusAndUserId("NEW", userId);
    }
}
