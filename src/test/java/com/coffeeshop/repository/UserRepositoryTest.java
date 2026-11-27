package com.coffeeshop.repository;

import com.coffeeshop.entity.User;
import com.coffeeshop.helpers.DataGenerator;
import com.coffeeshop.helpers.DataOfFields;
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

    private final DataOfFields field = new DataOfFields();
    private final DataGenerator fakeData = new DataGenerator();

    @Autowired
    private UserRepository userRepository;

    @Test
    void checkUserRepository() {
        User savedUser = userRepository.save(new User(field.getTestName(),
                fakeData.getEmailTwo()));
        assertNotNull(savedUser.getId());
        Optional<User> option = userRepository.findById(savedUser.getId());
        User user = option.orElseThrow();

        assertAll(
                () -> assertNotNull(user.getId()),
                () -> assertEquals(field.getTestName(), user.getName()),
                () -> assertEquals(savedUser.getEmail(), user.getEmail())
        );

    }

    @Test
    void checkForUniqueEmail() {
        User savedUser = userRepository.save(new User(field.getTestName(),
                field.getAsdEmail()));

        assertNotNull(savedUser.getId());

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(new User("Oleg", field.getAsdEmail()));
        });
    }

}