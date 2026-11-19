package com.coffeeshop.repository;

import com.coffeeshop.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void checkUserRepository() {
        User savedUser = userRepository.save(new User("Testov", "asd@asd.asd"));
        assertNotNull(savedUser.getId());
        Optional<User> option = userRepository.findById(savedUser.getId());
        User user = option.orElseThrow();

        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals("Testov", user.getName()),
                () -> assertEquals("asd@asd.asd", user.getEmail())
        );

    }

    @Test
    void checkForUniqueEmail() {
        User savedUser = userRepository.save(new User("Testov", "asd@asd.asd"));

        assertNotNull(savedUser.getId());

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(new User("Oleg", "asd@asd.asd"));
        });
    }

}
