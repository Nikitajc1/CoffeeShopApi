package com.coffeeshop.helpers;

import com.coffeeshop.dto.order.OrderRequest;
import com.coffeeshop.entity.User;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

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

    public ValidatableResponse createOrder(OrderRequest request) {
        return given()
                .spec(getSpec())
                .body(request)
                .when().post(getOrderPath())
                .then()
                .statusCode(201);
    }

    public ValidatableResponse createOrderStatus(OrderRequest request, String status) {
        return given()
                .spec(getSpec())
                .body(new OrderRequest(request.userId(), request.description(), status))
                .when().post(getOrderPath())
                .then()
                .statusCode(201);
    }

}
