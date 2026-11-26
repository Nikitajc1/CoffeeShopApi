package com.coffeeshop.api;

import com.coffeeshop.dto.order.OrderRequest;
import com.coffeeshop.entity.User;
import com.coffeeshop.helpers.ApiHelper;
import com.coffeeshop.helpers.TestDataCleaner;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
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
        given()
                .spec(help.getSpec())
                .body(request)
                .when().post(help.getOrderPath())
                .then()
                .statusCode(201)
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
        Response response = given()
                .spec(help.getSpec())
                .body(request)
                .when().post(help.getOrderPath())
                .then()
                .statusCode(201)
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

}
