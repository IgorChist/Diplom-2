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
import static org.hamcrest.Matchers.notNullValue;

public class LoginUserTest {
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
    @DisplayName("Логин существуещего пользователя")
    @Description("Проверка входа существующего пользователя")
    @Test
    public void shouldLoginedAndReturn200 () {
        userSteps
                .createUser(user);
        userSteps
                .loginUser(user)
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }
    @DisplayName("Логин с неверным email")
    @Description("Нельзя пройти авторизацию с неверным email")
    @Test
    public void userCannotLoginedWithIncorrectEmail () {
        String correctEmail = user.getEmail();
        userSteps
                .createUser(user);
        user.setEmail(RandomStringUtils.randomAlphabetic(10));
        userSteps
                .loginUser(user)
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
        user.setEmail(correctEmail);
    }
    @DisplayName("Логин с неверным паролем")
    @Description("Нельзя пройти авторизацию с неверным паролем")
    @Test
    public void userCannotLoginedWithIncorrectPassword () {
        String correctPassword = user.getPassword();
        userSteps
                .createUser(user);
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        userSteps
                .loginUser(user)
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
        user.setPassword(correctPassword);
    }
    @DisplayName("Логин без email")
    @Description("Нельзя пройти авторизацию без email")
    @Test
    public void userCannotLoginedWithoutEmail () {
        String correctEmail = user.getEmail();
        userSteps
                .createUser(user);
        user.setEmail(null);
        userSteps
                .loginUser(user)
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
        user.setEmail(correctEmail);
    }
    @DisplayName("Логин без пароля")
    @Description("Нельзя пройти авторизацию без пароля")
    @Test
    public void userCannotLoginedWithoutPassword () {
        String correctPassword = user.getPassword();
        userSteps
                .createUser(user);
        user.setPassword(null);
        userSteps
                .loginUser(user)
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
        user.setPassword(correctPassword);
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
