package com.coffeeshop.api;

import com.coffeeshop.dto.order.OrderRequest;
import com.coffeeshop.entity.User;
import com.coffeeshop.helpers.ApiHelper;
import com.coffeeshop.helpers.TestDataCleaner;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderApiTests {
    private final ApiHelper help = new ApiHelper();
    private User user;
    private final TestDataCleaner cleaner = new TestDataCleaner();
    private Long userId;
    private OrderRequest request;
    private static final Long NON_EXISTING_USER_ID = 999999999L;


    @BeforeEach
    void init() {
        user = help.generateUser();
        userId = given()
                .spec(help.getSpec())
                .body(user)
                .when().post(help.getUserPath())
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
        request = help.createRequest(userId);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        cleaner.deleteUserByEmail(user.getEmail());
    }

    @Test
    void shouldCreateOrder() {
        help.createOrder(request)
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .body("description", equalTo(request.description()))
                .body("status", equalTo(request.status()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0));
    }

    @Test
    void shouldNotCreateOrderForNonExistingUser() {
        given()
                .spec(help.getSpec())
                .body(new OrderRequest(NON_EXISTING_USER_ID,
                        "asd", "NEW"))
                .when().post(help.getOrderPath())
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"))
                .body("message", containsString("not found"))
                .body("details", empty());
    }

    @Test
    void shouldNotCreateOrderWithoutDescription() {
        given()
                .spec(help.getSpec())
                .body(new OrderRequest(request.userId(), "", request.status()))
                .when().post(help.getOrderPath())
                .then()
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Validation failed"))
                .body("details", hasItem(allOf(
                        containsString("description"),
                        containsString("не должно быть пустым")
                )));
    }

    @Test
    void shouldNotCreateOrderWithIncorrectStatus() {
        given()
                .spec(help.getSpec())
                .body(new OrderRequest(request.userId(),
                        request.description(), "123"))
                .when().post(help.getOrderPath())
                .then()
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Validation failed"))
                .body("details", hasItem(allOf(
                        containsString("status"),
                        containsString("Status must be NEW, IN_PROGRESS or COMPLETED")
                )));
    }

    @Test
    void shouldGetOrderById() {
        Response response = help.createOrder(request)
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .body("description", equalTo(request.description()))
                .body("status", equalTo(request.status()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0))
                .extract().response();

        JsonPath path = response.jsonPath();
        Long id = path.getLong("id");

        given()
                .spec(help.getSpec())
                .pathParam("id", id)
                .when().get("/orders/{id}")
                .then()
                .statusCode(200)
                .body("userId", equalTo(path.get("userId")))
                .body("id", equalTo(path.get("id")))
                .body("description", equalTo(path.getString("description")))
                .body("status", equalTo(path.getString("status")))
                .body("createdAt", equalTo(path.getString("createdAt")));
    }

    @Test
    void shouldNotGetNonExistingOrder() {
        given()
                .spec(help.getSpec())
                .pathParam("id", 123123123)
                .when().get("/orders/{id}")
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"))
                .body("message", containsString("not found"))
                .body("details", empty());
    }

    @Test
    void shouldGetOrdersByUserId() {
        Response order1 = help.createOrder(request)
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .body("description", equalTo(request.description()))
                .body("status", equalTo(request.status()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0))
                .extract().response();

        Response order2 = help.createOrder(request)
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .body("description", equalTo(request.description()))
                .body("status", equalTo(request.status()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0))
                .extract().response();

        JsonPath path1 = order1.jsonPath();
        JsonPath path2 = order2.jsonPath();

        given()
                .spec(help.getSpec())
                .pathParam("userId", userId)
                .when().get("/orders/user/{userId}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("id", containsInAnyOrder(
                        path1.getLong("id"),
                        path2.getLong("id")
                ))
                .body("userId", everyItem(equalTo(userId.intValue())))
                .body("description", everyItem(equalTo(request.description())))
                .body("status", everyItem(equalTo(request.status())))
                .body("createdAt", everyItem(notNullValue()));
    }

    @Test
    void shouldGetOrdersByIdAndStatus() {

        help.createOrderStatus(request, "NEW")
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .and()
                .body("description", equalTo(request.description()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0));

        help.createOrderStatus(request, "COMPLETED")
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .body("description", equalTo(request.description()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0));

        help.createOrderStatus(request, "NEW")
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .body("description", equalTo(request.description()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0));

        given()
                .spec(help.getSpec())
                .pathParam("userId", userId)
                .pathParam("status", "NEW")
                .when().get("/orders/user/{userId}/{status}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("userId", everyItem(equalTo(userId.intValue())))
                .body("status", everyItem(equalTo("NEW")));;
    }

    @Test
    void shouldReturnEmptyOrders() {
        help.createOrderStatus(request, "NEW")
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .and()
                .body("description", equalTo(request.description()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0));

        help.createOrderStatus(request, "NEW")
                .body("userId", equalTo(userId.intValue()))
                .body("id", notNullValue())
                .and()
                .body("description", equalTo(request.description()))
                .body("createdAt", notNullValue())
                .body("id", greaterThan(0));

        given()
                .spec(help.getSpec())
                .pathParam("userId", userId)
                .pathParam("status", "COMPLETED")
                .when().get("/orders/user/{userId}/{status}")
                .then()
                .statusCode(200)
                .body("size()", equalTo(0))
                .body("$", empty());

    }

    @Test
    void shouldReturnErrorForNonExistingUser() {
        given()
                .spec(help.getSpec())
                .pathParam("userId", 123)
                .when().get("/orders/user/{userId}")
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"))
                .body("message", containsString("not found"))
                .body("details", empty());
    }

}
