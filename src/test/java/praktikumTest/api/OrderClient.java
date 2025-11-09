package praktikumTest.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private final RequestSpecification requestSpec;

    public OrderClient() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
        this.requestSpec = new RequestSpecBuilder()
                .setBaseUri(RestAssured.baseURI)
                .setContentType(ContentType.JSON)
                .build();
    }

    // Получение всех ингредиентов
    public Response getIngredients() {
        return given()
                .spec(requestSpec)
                .when()
                .get("/ingredients");
    }

    // Создание заказа (авторизованный/неавторизованный)
    public Response createOrder(Order order, String token) {
        RequestSpecification spec = given()
                .spec(requestSpec)
                .body(order);

        if (token != null) {
            spec.header("Authorization", token);
        }

        return spec.when()
                .post("/orders");
    }
}
