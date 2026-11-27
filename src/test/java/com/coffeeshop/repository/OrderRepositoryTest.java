package com.coffeeshop.repository;

import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.User;
import com.coffeeshop.helpers.DataGenerator;
import com.coffeeshop.helpers.DataOfFields;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    private final DataGenerator data = new DataGenerator();
    private final DataOfFields field = new DataOfFields();

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void checkOrderRepository() {
        User user = userRepository.save(new User(field.getTestName(), field.getAsdEmail()));
        assertNotNull(user.getId());

        Order order = orderRepository.save(new Order
                (user, field.getAsdDescription(), field.getStatusNew()));
        assertNotNull(order.getCreatedAt());

        assertAll(
                () -> assertNotNull(order.getId()),
                () -> assertEquals(field.getAsdDescription(), order.getDescription()),
                () -> assertEquals(field.getStatusNew(), order.getStatus()),
                () -> assertEquals(user.getId(), order.getUser().getId())
        );
    }

    @Test
    void checkOneToMany() {
        User user = userRepository.save(
                new User(data.getNameOfUserOne(), data.getEmailOne())
        );
        assertNotNull(user.getId());

        Order order = orderRepository.save(
                new Order(user, data.getDescriptionOne(), data.getStatus())
        );
        assertNotNull(order.getCreatedAt());

        entityManager.flush();
        entityManager.clear();

        User foundUser = userRepository.findById(user.getId()).orElseThrow();

        List<Order> userOrders = foundUser.getOrders();
        assertFalse(userOrders.isEmpty());
        Order userOrder = userOrders.getFirst();

        assertAll(
                () -> assertEquals(order.getId(), userOrder.getId()),
                () -> assertEquals(
                        order.getCreatedAt().truncatedTo(ChronoUnit.MICROS),
                        userOrder.getCreatedAt()
                ),
                () -> assertEquals(userOrder.getDescription(), order.getDescription()),
                () -> assertEquals(userOrder.getStatus(), order.getStatus())
        );
    }

    @Test
    void shouldNotDeleteUserWithOrders() {
        User user = userRepository.save(
                new User(data.getNameOfUserOne(), data.getEmailOne())
        );
        assertNotNull(user.getId());

        Order order = orderRepository.save(
                new Order(user, data.getDescriptionOne(), data.getStatus())
        );
        assertNotNull(order.getCreatedAt());

        entityManager.flush();
        entityManager.clear();

        User foundUser = userRepository.findById(user.getId()).orElseThrow();

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.delete(foundUser);
            userRepository.flush();
        });

    }

    @Test
    void shouldDeleteOrderWithoutDeletingUser() {
        User user = userRepository.save(
                new User(data.getNameOfUserOne(), data.getEmailOne())
        );
        assertNotNull(user.getId());

        Order order = orderRepository.save(
                new Order(user, data.getDescriptionOne(), data.getStatus())
        );
        assertNotNull(order.getCreatedAt());

        entityManager.flush();
        entityManager.clear();

        assertDoesNotThrow(() -> {
            orderRepository.delete(order);
            orderRepository.flush();
        });

        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(user.getId(), foundUser.getId());

    }

    @Test
    void shouldFindOrdersByUserId() {
        User user1 = userRepository.save(
                new User(data.getNameOfUserOne(), data.getEmailOne())
        );
        assertNotNull(user1.getId());

        User user2 = userRepository.save(
                new User(data.getNameOfUserTwo(), data.getEmailTwo())
        );
        assertNotNull(user2.getId());


        Order order1ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionOne(), data.getStatus())
        );
        assertNotNull(order1ForUser1.getCreatedAt());
        Order order2ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionTwo(), data.getStatus())
        );
        assertNotNull(order2ForUser1.getCreatedAt());
        Order order1ForUser2 = orderRepository.save(
                new Order(user2, data.getDescriptionThree(), data.getStatus())
        );
        assertNotNull(order1ForUser2.getCreatedAt());

        List<Order> ordersOfUser1 = orderRepository.findByUserId(user1.getId());
        assertEquals(2, ordersOfUser1.size());

        Order firstOrderOfUser1 = ordersOfUser1.getFirst();
        Order secondOrderOfUser1 = ordersOfUser1.get(1);

        assertAll(
                () -> assertEquals(firstOrderOfUser1.getStatus(),
                        order1ForUser1.getStatus()),
                () -> assertEquals(firstOrderOfUser1.getDescription(),
                        order1ForUser1.getDescription()),
                () -> assertEquals(firstOrderOfUser1.getUser().getId(),
                        order1ForUser1.getUser().getId()),
                () -> assertEquals(secondOrderOfUser1.getStatus(),
                        order2ForUser1.getStatus()),
                () -> assertEquals(secondOrderOfUser1.getDescription(),
                        order2ForUser1.getDescription()),
                () -> assertEquals(secondOrderOfUser1.getUser().getId(),
                        order2ForUser1.getUser().getId()),
                () -> assertNotEquals(firstOrderOfUser1.getUser().getId(),
                        order1ForUser2.getUser().getId()),
                () ->assertNotEquals(secondOrderOfUser1.getUser().getId(),
                        order1ForUser2.getUser().getId()),
                () ->assertNotEquals(secondOrderOfUser1.getDescription(),
                        order1ForUser2.getDescription())

        );

        Set<Long> expectedIds = Set.of(
                order1ForUser1.getId(),
                order2ForUser1.getId()
        );

        Set<Long> actualIds = ordersOfUser1.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        assertEquals(expectedIds, actualIds);

    }

    @Test
    void shouldFindOrdersByStatus() {
        User user1 = userRepository.save(
                new User(data.getNameOfUserOne(), data.getEmailOne())
        );
        assertNotNull(user1.getId());

        Order order1ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionOne(), field.getStatusNew())
        );
        assertNotNull(order1ForUser1.getCreatedAt());

        Order order2ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionTwo(), field.getStatusNew())
        );
        assertNotNull(order2ForUser1.getCreatedAt());

        Order order3ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionThree(), field.getStatusCompleted())
        );
        assertNotNull(order3ForUser1.getCreatedAt());

        entityManager.flush();
        entityManager.clear();

        List<Order> orders = orderRepository.findByStatusAndUserId(
                field.getStatusNew(),
                user1.getId());

        assertAll(
                () -> assertEquals(2, orders.size()),
                () -> assertTrue(orders.stream()
                        .allMatch(order -> order.getStatus().equals(field.getStatusNew())))
        );


    }

    @Test
    void checkCreationDate() {
        User user1 = userRepository.save(
                new User(data.getNameOfUserOne(), data.getEmailOne())
        );
        assertNotNull(user1.getId());

        Order order1ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionOne(), field.getStatusCompleted())
        );
        assertNotNull(order1ForUser1.getCreatedAt());

        Order order2ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionTwo(), field.getStatusCompleted())
        );
        assertNotNull(order2ForUser1.getCreatedAt());

        Order order3ForUser1 = orderRepository.save(
                new Order(user1, data.getDescriptionThree(), field.getStatusCompleted())
        );
        assertNotNull(order3ForUser1.getCreatedAt());

        entityManager.flush();
        entityManager.clear();

        List<Order> orders = orderRepository
                .findByUserIdOrderByCreatedAtDesc(user1.getId());
        assertEquals(3, orders.size());

        assertAll(
                () -> assertTrue(orders.get(0).getCreatedAt()
                        .isAfter(orders.get(1).getCreatedAt())),
                () -> assertTrue(orders.get(1).getCreatedAt()
                        .isAfter(orders.get(2).getCreatedAt()))
        );

        Set<Long> expectedIds = Set.of(
                order1ForUser1.getId(),
                order2ForUser1.getId(),
                order3ForUser1.getId()
        );

        Set<Long> actualIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toSet());

        assertEquals(expectedIds, actualIds);

    }
}
