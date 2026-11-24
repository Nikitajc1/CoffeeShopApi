package com.coffeeshop.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OrderRequest(
        @NotNull
        Long userId,
        @NotBlank
        @Size(max = 255)
        String description,
        @NotBlank
        @Size(max = 50)
        @Pattern(regexp = "NEW|IN_PROGRESS|COMPLETED",
                message = "Status must be NEW, IN_PROGRESS or COMPLETED"
        )
        String status) {
}
