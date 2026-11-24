package com.coffeeshop.controller;

import com.coffeeshop.dto.user.UserRequest;
import com.coffeeshop.dto.user.UserResponse;
import com.coffeeshop.entity.User;
import com.coffeeshop.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        User user = userService.createUser(request.name(), request.email());
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
