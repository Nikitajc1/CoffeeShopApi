package com.coffeeshop.api;

import com.coffeeshop.dto.order.OrderRequest;
import com.coffeeshop.entity.User;
import com.coffeeshop.helpers.ApiHelper;
import com.coffeeshop.helpers.DataOfFields;
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
    private OrderRequest request2;
    private static final Long NON_EXISTING_USER_ID = 999999999L;
    private final DataOfFields field = new DataOfFields();


    @BeforeEach
    void init() {
        user = help.generateUser();
        userId = given()
                .spec(help.getSpec())
                .body(user)
                .when().post(help.getUserPath())
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong(field.getId());
        request = help.createRequest(userId);
        request2 = help.createRequest(userId);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        cleaner.deleteUserByEmail(user.getEmail());
    }

    @Test
    void shouldCreateOrder() {
        help.createOrder(request)
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getStatus(), equalTo(request.status()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0));
    }

    @Test
    void shouldNotCreateOrderForNonExistingUser() {
        given()
                .spec(help.getSpec())
                .body(new OrderRequest(NON_EXISTING_USER_ID,
                        field.getAsdDescription(), field.getStatusNew()))
                .when().post(help.getOrderPath())
                .then()
                .statusCode(404)
                .body(field.getError(), equalTo(field.getNotFound()))
                .body(field.getMessage(), containsString("not found"))
                .body(field.getDetails(), empty());
    }

    @Test
    void shouldNotCreateOrderWithoutDescription() {
        given()
                .spec(help.getSpec())
                .body(new OrderRequest(request.userId(), "", request.status()))
                .when().post(help.getOrderPath())
                .then()
                .statusCode(400)
                .body(field.getError(), equalTo(field.getBadRequest()))
                .body(field.getMessage(), equalTo(field.getValidationFailed()))
                .body(field.getDetails(), hasItem(allOf(
                        containsString(field.getDescription()),
                        containsString(field.getDescription())
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
                .body(field.getError(), equalTo(field.getBadRequest()))
                .body(field.getMessage(), equalTo(field.getValidationFailed()))
                .body(field.getDetails(), hasItem(allOf(
                        containsString(field.getStatus()),
                        containsString(field.getStatusMustBe())
                )));
    }

    @Test
    void shouldGetOrderById() {
        Response response = help.createOrder(request)
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getStatus(), equalTo(request.status()))
                .body(field.getCreatedAt(), notNullValue())
                .extract().response();

        JsonPath path = response.jsonPath();
        Long id = path.getLong(field.getId());

        given()
                .spec(help.getSpec())
                .pathParam(field.getId(), id)
                .when().get(help.getOrderField())
                .then()
                .statusCode(200)
                .body(field.getUserId(), equalTo(path.get(field.getUserId())))
                .body(field.getId(), equalTo(path.get(field.getId())))
                .body(field.getDescription(), equalTo(path.getString(field.getDescription())))
                .body(field.getStatus(), equalTo(path.getString(field.getStatus())))
                .body(field.getCreatedAt(), equalTo(path.getString(field.getCreatedAt())));
    }

    @Test
    void shouldNotGetNonExistingOrder() {
        given()
                .spec(help.getSpec())
                .pathParam(field.getId(), 123123123)
                .when().get(help.getOrderField())
                .then()
                .statusCode(404)
                .body(field.getError(), equalTo(field.getNotFound()))
                .body(field.getMessage(),
                        containsString(field.orderNotFoundMessage(123123123)))
                .body(field.getDetails(), empty());
    }

    @Test
    void shouldGetOrdersByUserId() {
        Response order1 = help.createOrder(request)
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getStatus(), equalTo(request.status()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0))
                .extract().response();

        Response order2 = help.createOrder(request2)
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .body(field.getDescription(), equalTo(request2.description()))
                .body(field.getStatus(), equalTo(request2.status()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0))
                .extract().response();

        JsonPath path1 = order1.jsonPath();
        JsonPath path2 = order2.jsonPath();

        given()
                .spec(help.getSpec())
                .pathParam(field.getUserId(), userId)
                .when().get(help.getOrdersOfUserById())
                .then()
                .statusCode(200)
                .body(field.getSizeForBody(), equalTo(2))
                .body(field.getId(), containsInAnyOrder(
                        path1.getInt(field.getId()),
                        path2.getInt(field.getId())
                        )
                )
                .body(field.getUserId(), everyItem(equalTo(userId.intValue())))
                .body(field.getDescription(), hasItem(equalTo(request.description())))
                .body(field.getDescription(), hasItem(equalTo(request2.description())))
                .body(field.getStatus(), hasItem(equalTo(request.status())))
                .body(field.getStatus(), hasItem(equalTo(request2.status())))
                .body(field.getCreatedAt(), everyItem(notNullValue()));
    }

    @Test
    void shouldGetOrdersByIdAndStatus() {

        help.createOrderStatus(request, field.getStatusNew())
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .and()
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0));

        help.createOrderStatus(request, field.getStatusCompleted())
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0));

        help.createOrderStatus(request, field.getStatusNew())
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0));

        given()
                .spec(help.getSpec())
                .pathParam(field.getUserId(), userId)
                .pathParam(field.getStatus(), field.getStatusNew())
                .when().get(help.getOrdersOfUserByIdAndStatus())
                .then()
                .statusCode(200)
                .body(field.getSizeForBody(), equalTo(2))
                .body(field.getUserId(), everyItem(equalTo(userId.intValue())))
                .body(field.getStatus(), everyItem(equalTo(field.getStatusNew())));;
    }

    @Test
    void shouldReturnEmptyOrders() {
        help.createOrderStatus(request, field.getStatusNew())
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .and()
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0));

        help.createOrderStatus(request, field.getStatusNew())
                .body(field.getUserId(), equalTo(userId.intValue()))
                .body(field.getId(), notNullValue())
                .and()
                .body(field.getDescription(), equalTo(request.description()))
                .body(field.getCreatedAt(), notNullValue())
                .body(field.getId(), greaterThan(0));

        given()
                .spec(help.getSpec())
                .pathParam(field.getUserId(), userId)
                .pathParam(field.getStatus(), field.getStatusCompleted())
                .when().get(help.getOrdersOfUserByIdAndStatus())
                .then()
                .statusCode(200)
                .body("$", empty());

    }

    @Test
    void shouldReturnErrorForNonExistingUser() {
        given()
                .spec(help.getSpec())
                .pathParam(field.getUserId(), 123)
                .when().get(help.getOrdersOfUserById())
                .then()
                .statusCode(404)
                .body(field.getError(), equalTo(field.getNotFound()))
                .body(field.getMessage(), containsString(field.userNotFoundMessage(123)))
                .body(field.getDetails(), empty());
    }

}
