package com.coffeeshop.api;

import com.coffeeshop.entity.User;
import com.coffeeshop.helpers.ApiHelper;
import com.coffeeshop.helpers.TestDataCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserApiTests {

    private final ApiHelper help = new ApiHelper();
    private Long userId;
    private User user;
    private final TestDataCleaner cleaner = new TestDataCleaner();

    @BeforeEach
    void init() {
        user = help.generateUser();
        userId = user.getId();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        cleaner.deleteUserByEmail(user.getEmail());
    }

    @Test
    void generateUser() {
        given()
                .spec(help.getSpec())
                .body(user)
                .when().post(help.getUserPath())
                .then()
                .statusCode(201)
                .body("name", equalTo(user.getName()))
                .body("email", equalTo(user.getEmail()))
                .body("id", notNullValue());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        given()
                .spec(help.getSpec())
                .body(new User("asd", "asd"))
                .when().post(help.getUserPath())
                .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"))
                .body("details"
                        , contains("email: должно иметь формат адреса электронной почты"));
    }

    @Test
    void shouldNotGetNonExistingUser() {
        given()
                .spec(help.getSpec())
                .pathParam("id", 123123123)
                .when().get("/users/{id}")
                .then()
                .statusCode(404)
                .body("error", equalTo("Not Found"))
                .body("message", containsString(" not found"))
                .body("details", empty());
    }

    @Test
    void shouldNotCreateUserWithDuplicateEmail() {
        given()
                .spec(help.getSpec())
                .body(user)
                .when().post(help.getUserPath())
                .then()
                .statusCode(201)
                .body("name", equalTo(user.getName()))
                .body("email", equalTo(user.getEmail()))
                .body("id", notNullValue());

        given()
                .spec(help.getSpec())
                .body(user)
                .when().post(help.getUserPath())
                .then()
                .statusCode(409)
                .body("error", equalTo("Conflict"))
                .body("message", containsString("already exists"));
    }

    @Test
    void shouldNotCreateUserWithEmptyName() {
        given()
                .spec(help.getSpec())
                .body(new User("", "a@a.a"))
                .when().post(help.getUserPath())
                .then()
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .body("message", equalTo("Validation failed"))
                .body("details", hasItem(containsString("не должно быть пустым")));
    }
}
