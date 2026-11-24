package com.coffeeshop.service;

import com.coffeeshop.entity.User;
import com.coffeeshop.repository.OrderRepository;
import com.coffeeshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class OrderTransactionTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldRollbackOrderCreationWhenExceptionOccurs() {
//        User user = userRepository.save(new User("Test", "asd1@asd.asd"));
//        Long before = orderRepository.count();
//
//        assertEquals("Test transaction rollback", assertThrows(RuntimeException.class, () -> {
//            orderService.createOrder(user.getId(), "asd", "NEW");
//        }).getMessage());
//
//        Long after = orderRepository.count();
//
//        assertEquals(before, after);
    }

}
