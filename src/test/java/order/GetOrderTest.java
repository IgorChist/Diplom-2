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

import static org.hamcrest.Matchers.*;

public class GetOrderTest {
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

    @DisplayName("Получение заказов с авторизацией")
    @Description("Проверка получения списка заказов с авторизацией")
    @Test
    public void shouldGetOrdersAuthorizedUser() {
        userSteps
                .createUser(user);
        accessToken = userSteps.loginUser(user)
                .extract().body().path("accessToken");
        orderSteps.getUserOrders(accessToken)
                .statusCode(200)
                .body("success", is(true));
    }

    @DisplayName("Получение заказов без авторизации")
    @Description("Нельзя получить список заказов без авторизации")
    @Test
    public void cannotGetOrdersUnauthorizedUser() {
        userSteps
                .createUser(user);
        accessToken = "";
        orderSteps.getUserOrders(accessToken)
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
