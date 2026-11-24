package com.coffeeshop.service;

import com.coffeeshop.entity.User;
import com.coffeeshop.exception.EmailAlreadyExistsException;
import com.coffeeshop.exception.UserNotFoundException;
import com.coffeeshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Autowired
    private Long id;

    @Autowired
    private String email;

    @Test
    void shouldCreateUser() {
        User user = new User("Test", "asd@asd.asd");
        String email = user.getEmail();

        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(userRepository.existsByEmail(email)).thenReturn(false);

        User result = userService.createUser("Test", "asd@asd.asd");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals("Test", result.getName()),
                () -> assertEquals("asd@asd.asd", result.getEmail())
        );
        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    void shouldGetUserById() {
        User user = new User("Test", "asd@asd.asd");
        user.setId(1L);

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(user.getId());

        verify(userRepository, times(1)).findById
                (user.getId());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(user.getId(), result.getId()),
                () -> assertEquals(user.getName(), result.getName()),
                () -> assertEquals(user.getEmail(), result.getEmail())
        );
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertEquals("User with id 1 not found",
                assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        }).getMessage()
        );
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldNotCreateUserWithSameEmail() {
        String email = "asd@asd.asd";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertEquals("User with email asd@asd.asd already exists"
                , assertThrows(EmailAlreadyExistsException.class, () -> {
                    userService.createUser("Test", email);
        }).getMessage());
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));

    }

}