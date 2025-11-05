package praktikumTest.api;

import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserLoginTests {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }

    @Test
    public void loginExistingUser() {
        String body = "{ \"email\": \"existinguser@test.com\", \"password\": \"123456\" }";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    public void loginWithInvalidCredentials() {
        String body = "{ \"email\": \"wrong@test.com\", \"password\": \"wrongpass\" }";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", containsString("email or password are incorrect"));
    }
}

