package com.coffeeshop.api;

import com.coffeeshop.entity.User;
import com.coffeeshop.helpers.ApiHelper;
import com.coffeeshop.helpers.DataOfFields;
import com.coffeeshop.helpers.TestDataCleaner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UserApiTests {

    private final ApiHelper help = new ApiHelper();
    private User user;
    private final TestDataCleaner cleaner = new TestDataCleaner();
    private final DataOfFields field = new DataOfFields();


    @BeforeEach
    void init() {
        user = help.generateUser();
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
                .body(field.getName(), equalTo(user.getName()))
                .body(field.getEmail(), equalTo(user.getEmail()))
                .body(field.getId(), notNullValue());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() {
        given()
                .spec(help.getSpec())
                .body(new User(field.getTestName(), "asd"))
                .when().post(help.getUserPath())
                .then()
                .statusCode(400)
                .body(field.getMessage(), equalTo(field.getValidationFailed()))
                .body(field.getDetails()
                        , contains("email: должно иметь формат адреса электронной почты"));
    }

    @Test
    void shouldNotGetNonExistingUser() {
        given()
                .spec(help.getSpec())
                .pathParam(field.getId(), 123123123)
                .when().get(help.getUserField())
                .then()
                .statusCode(404)
                .body(field.getError(), equalTo(field.getNotFound()))
                .body(field.getMessage(), containsString("not found"))
                .body(field.getDetails(), empty());
    }

    @Test
    void shouldNotCreateUserWithDuplicateEmail() {
        given()
                .spec(help.getSpec())
                .body(user)
                .when().post(help.getUserPath())
                .then()
                .statusCode(201)
                .body(field.getName(), equalTo(user.getName()))
                .body(field.getEmail(), equalTo(user.getEmail()))
                .body(field.getId(), notNullValue());

        given()
                .spec(help.getSpec())
                .body(user)
                .when().post(help.getUserPath())
                .then()
                .statusCode(409)
                .body(field.getError(), equalTo("Conflict"))
                .body(field.getMessage(), containsString("already exists"));
    }

    @Test
    void shouldNotCreateUserWithEmptyName() {
        given()
                .spec(help.getSpec())
                .body(new User("", "a@a.a"))
                .when().post(help.getUserPath())
                .then()
                .statusCode(400)
                .body(field.getError(), equalTo(field.getBadRequest()))
                .body(field.getMessage(), equalTo(field.getValidationFailed()))
                .body(field.getDetails(),
                        hasItem(containsString("не должно быть пустым")));
    }
}
