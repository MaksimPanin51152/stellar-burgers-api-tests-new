package praktikumTest.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserCreationTests {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
    }

    @Test
    public void createUniqueUser() {
        String email = "user_" + UUID.randomUUID() + "@test.com";
        String body = "{ \"email\": \"" + email + "\", \"password\": \"123456\", \"name\": \"TestUser\" }";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email));
    }

    @Test
    public void createExistingUser() {
        String body = "{ \"email\": \"existinguser@test.com\", \"password\": \"123456\", \"name\": \"TestUser\" }";

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", containsString("User already exists"));
    }

    @Test
    public void createUserMissingField() {
        String body = "{ \"email\": \"user_missing@test.com\", \"password\": \"123456\" }"; // пропущено имя

        given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", containsString("required fields"));
    }
}
