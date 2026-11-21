package com.coffeeshop.exception;

public class OrderNotFoundException extends RuntimeException {
    private final Long id;

    public OrderNotFoundException(Long id) {
        super("Order with id " + id + " not found");
        this.id = id;
    }
}
