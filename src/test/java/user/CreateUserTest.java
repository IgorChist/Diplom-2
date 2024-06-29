package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static org.hamcrest.Matchers.*;

public class CreateUserTest {
    UserSteps userSteps = new UserSteps();
    User user;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        user.setName(RandomStringUtils.randomAlphabetic(6));
    }

    @DisplayName("Создание уникального пользователя")
    @Description("Проверка создания уникального пользователя")
    @Test
    public void shouldCreateUserAndReturn200() {
        userSteps
                .createUser(user)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @DisplayName("Создание не уникального пользователя")
    @Description("Нельзя создать не уникального пользователя")
    @Test
    public void cannotCreateTwoIdenticalUsers() {
        userSteps
                .createUser(user);
        userSteps
                .createUser(user)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @DisplayName("Создание пользователя без email")
    @Description("Нельзя создать пользователя без email")
    @Test
    public void cannotCreateUserWithoutEmail() {
        user.setEmail(null);

        userSteps
                .createUser(user)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Создание пользователя без имени")
    @Description("Нельзя создать пользователя без имени")
    @Test
    public void cannotCreateUserWithoutName() {
        user.setName(null);

        userSteps
                .createUser(user)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @DisplayName("Создание пользователя без пароля")
    @Description("Нельзя создать пользователя без пароля")
    @Test
    public void cannotCreateUserWithoutPassword() {
        user.setPassword(null);

        userSteps
                .createUser(user)
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        String accessToken = userSteps.loginUser(user)
                .extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
