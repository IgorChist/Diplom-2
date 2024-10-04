package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Order;

import static config.Config.*;
import static io.restassured.RestAssured.given;

public class OrderSteps {
    @Step("Send POST request to api/orders")
    public ValidatableResponse createOrder (Order order, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(HOST)
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    @Step("Send GET request to api/ingredients")
    public ValidatableResponse getAllIngredients () {
        return given()
                .baseUri(HOST)
                .when()
                .get(GET_INGREDIENTS)
                .then();
    }
    @Step("Send GET request to api/orders")
    public ValidatableResponse getUserOrders (String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(HOST)
                .when()
                .get(GET_USER_ORDERS)
                .then();
    }
}
