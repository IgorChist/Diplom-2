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

public class ChangeUserDataTest {
    UserSteps userSteps = new UserSteps();
    User user;
    String accessToken;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        user.setName(RandomStringUtils.randomAlphabetic(6));
    }

    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверка изменения данных существующего пользователя с авторизацией")
    @Test
    public void shouldChangeUserDataWithAuthorization() {
        userSteps
                .createUser(user);
        accessToken = userSteps.loginUser(user)
                .extract().body().path("accessToken");
        user.setName(RandomStringUtils.randomAlphabetic(6));
        userSteps
                .changeUserData(user, accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()));
    }

    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка изменения данных существующего пользователя без авторизации")
    @Test
    public void cannotChangeUserDataWithoutAuthorization() {
        userSteps
                .createUser(user);
        userSteps.loginUser(user);
        accessToken = "";
        user.setName(RandomStringUtils.randomAlphabetic(6));
        userSteps
                .changeUserData(user, accessToken)
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
    @After
    public void tearDown() {
        accessToken = userSteps.loginUser(user)
                .extract().body().path("accessToken");
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }
}
