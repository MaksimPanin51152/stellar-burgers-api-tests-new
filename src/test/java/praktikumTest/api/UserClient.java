package praktikumTest.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {

    @Step("Регистрация нового пользователя")
    public static Response registerUser(User user) {
        return given()
                .spec(baseSpec)
                .body(user)
                .when()
                .post("/auth/register");
    }

    @Step("Авторизация пользователя")
    public static Response loginUser(User user) {
        return given()
                .spec(baseSpec)
                .body(user)
                .when()
                .post("/auth/login");
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", accessToken)
                .when()
                .delete("/auth/user");
    }
}
