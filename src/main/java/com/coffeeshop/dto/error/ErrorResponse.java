package com.coffeeshop.dto.error;

import java.util.List;

public record ErrorResponse(
        int status,
        String error,
        String message,
        List<String> details
) {
}
