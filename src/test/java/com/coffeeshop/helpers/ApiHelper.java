package com.coffeeshop.helpers;

import com.coffeeshop.dto.order.OrderRequest;
import com.coffeeshop.entity.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class ApiHelper {
    private final String uri = "http://localhost:8080/api";
    private final String userPath = "/users";
    private final String orderPath = "/orders";
    private final DataGenerator data = new DataGenerator();
    private final RequestSpecification specification;

    public ApiHelper() {
        specification = new RequestSpecBuilder()
                .setBaseUri(uri)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .build();
    }

    public User generateUser() {
        return new User(data.getNameOfUserOne(), data.getEmailOne());
    }

    public RequestSpecification getSpec() {
        return specification;
    }

    public String getUserPath() {
        return userPath;
    }

    public String getOrderPath() {
        return orderPath;
    }

    public OrderRequest createRequest(Long userId) {
        return new OrderRequest(userId, data.getDescriptionOne(), data.getStatus());
    }
}
