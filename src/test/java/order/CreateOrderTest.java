package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import model.Order;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest {
    User user;
    UserSteps userSteps = new UserSteps();
    Order order;
    OrderSteps orderSteps = new OrderSteps();
    String accessToken;
    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        user = new User();
        user.setEmail(RandomStringUtils.randomAlphabetic(10) + "@yandex.ru");
        user.setPassword(RandomStringUtils.randomAlphabetic(10));
        user.setName(RandomStringUtils.randomAlphabetic(6));
        order = new Order();
        order.setIngredients(orderSteps.getAllIngredients().extract().body().path("data._id"));
    }
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка создания заказа с авторизацией")
    @Test
    public void shouldCreateOrderWithAuthorization() {
        userSteps
                .createUser(user);
        accessToken = userSteps.loginUser(user)
                .extract().body().path("accessToken");
        orderSteps.createOrder(order, accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа без авторизации")
    @Test
    public void shouldCreateOrderWithoutAuthorization() {
        userSteps
                .createUser(user);
        accessToken = "";
        orderSteps.createOrder(order, accessToken)
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Нельзя создать заказ без ингредиентов")
    @Test
    public void cannotCreateOrderWithoutIngredients() {
        userSteps
                .createUser(user);
        accessToken = userSteps.loginUser(user)
                .extract().body().path("accessToken");
        order.setIngredients(Collections.emptyList());
        orderSteps.createOrder(order, accessToken)
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }
    @DisplayName("Создание заказа с неправильным ингредиентом")
    @Description("Нельзя создать заказ с неправильным ингредиентом")
    @Test
    public void cannotCreateOrderWithIncorrectIngredient() {
        userSteps
                .createUser(user);
        accessToken = userSteps.loginUser(user)
                .extract().body().path("accessToken");
        List<String> incorrectIngredient = order.getIngredients();
        incorrectIngredient.clear();
        incorrectIngredient.add("Somebody once told me the world is gonna roll me");
        order.setIngredients(incorrectIngredient);
        orderSteps.createOrder(order, accessToken)
                .statusCode(500);
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
