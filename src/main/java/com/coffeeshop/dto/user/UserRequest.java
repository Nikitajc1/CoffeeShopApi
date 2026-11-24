package com.coffeeshop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank
        @Size(min = 2, max = 100)
        String name,
        @NotBlank
        @Email
        @Size(max = 100)
        String email
    )
{}
