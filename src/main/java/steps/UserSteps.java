package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.User;

import static config.Config.*;
import static io.restassured.RestAssured.given;

public class UserSteps {
    @Step("Send POST request to api/auth/register")
        public ValidatableResponse createUser (User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOST)
                .body(user)
                .when()
                .post(CREATE_USER)
                .then();
    }
    @Step("Send POST request to api/auth/login")
    public ValidatableResponse loginUser (User user) {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(HOST)
                .body(user)
                .when()
                .post(LOGIN_USER)
                .then();
    }
    @Step("Send DELETE request to api/auth/user")
    public ValidatableResponse deleteUser (String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .baseUri(HOST)
                .when()
                .delete(DELETE_USER)
                .then()
                .statusCode(202);
        }
    @Step("Send PATCH request to api/auth/user")
    public ValidatableResponse changeUserData (User user, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .baseUri(HOST)
                .body(user)
                .when()
                .patch(CHANGE_USER_DATA)
                .then();
    }
}
