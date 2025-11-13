package praktikumTest.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {

    @Step("Получить список ингредиентов")
    public Response getIngredients() {
        return given()
                .spec(baseSpec)
                .when()
                .get("/ingredients");
    }

    @Step("Создать заказ (авторизованный или неавторизованный)")
    public Response createOrder(Order order, String token) {
        var spec = given()
                .spec(baseSpec)
                .body(order);

        if (token != null) {
            spec.header("Authorization", token);
        }

        return spec.when()
                .post("/orders");
    }
}
