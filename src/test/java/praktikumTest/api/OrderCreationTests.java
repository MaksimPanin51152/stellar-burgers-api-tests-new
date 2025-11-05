package praktikumTest.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderCreationTests {

    private static String accessToken;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";

        // Логинимся и получаем токен для авторизации заказов
        String loginBody = "{ \"email\": \"existinguser@test.com\", \"password\": \"123456\" }";
        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().response();

        accessToken = response.path("accessToken");
    }

    @Test
    public void createOrderWithAuth() {
        String body = "{ \"ingredients\": [\"60d3b41abdacab0026a733c6\"] }";

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    public void createOrderWithoutAuth() {
        String body = "{ \"ingredients\": [\"60d3b41abdacab0026a733c6\"] }";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/orders")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", containsString("authorised"));
    }

    @Test
    public void createOrderWithIngredients() {
        String body = "{ \"ingredients\": [\"60d3b41abdacab0026a733c6\", \"609646e4dc916e00276b2870\"] }";

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/orders")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void createOrderWithoutIngredients() {
        String body = "{ \"ingredients\": [] }";

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/orders")
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", containsString("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithInvalidIngredientHash() {
        String body = "{ \"ingredients\": [\"invalid_hash\"] }";

        given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .when()
                .post("/orders")
                .then()
                .statusCode(500);
    }
}
