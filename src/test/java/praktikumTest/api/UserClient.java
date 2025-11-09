package praktikumTest.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UserClient {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://stellarburgers.education-services.ru/api")
            .setContentType(ContentType.JSON)
            .build();

    public static Response registerUser(User user) {
        return given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/auth/register");
    }

    public static Response loginUser(User user) {
        return given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/auth/login");
    }

    public static Response deleteUser(String accessToken) {
        return given()
                .spec(requestSpec)
                .header("Authorization", accessToken)
                .when()
                .delete("/auth/user");
    }
}
