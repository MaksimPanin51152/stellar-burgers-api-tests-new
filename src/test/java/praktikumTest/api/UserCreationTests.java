package praktikumTest.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import praktikumTest.api.User;
import praktikumTest.api.UserClient;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserCreationTests {

    private static RequestSpecification requestSpec;
    private String accessToken;
    private String email;
    private final String password = "123456";
    private final String name = "TestUser";


    @BeforeClass
    public static void setupClass() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru/api";
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(RestAssured.baseURI)
                .setContentType(ContentType.JSON)
                .build();
    }

    @After
    public void cleanupUser() {
        if (accessToken != null) {
            given()
                    .spec(requestSpec)
                    .header("Authorization", accessToken)
                    .when()
                    .delete("/auth/user")
                    .then()
                    .statusCode(anyOf(is(200), is(202)));
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешной регистрации нового пользователя с уникальным email")
    public void createUniqueUser() {
        email = "user_" + UUID.randomUUID() + "@test.com";
        User user = new User(email, password, name);

        Response response = given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .extract()
                .response();

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    @Description("Проверка, что нельзя зарегистрировать пользователя с уже существующим email")
    public void createExistingUser() {
        User existingUser = new User("existinguser@test.com", password, name);

        given()
                .spec(requestSpec)
                .body(existingUser)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без имени")
    @Description("Проверка, что при отсутствии поля name возвращается ошибка 403 с корректным сообщением")
    public void createUserMissingName() {
        String body = "{ \"email\": \"user_missing_" + UUID.randomUUID() + "@test.com\", \"password\": \"123456\" }";

        given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без пароля")
    @Description("Проверка, что при отсутствии поля password возвращается ошибка 403 с корректным сообщением")
    public void createUserMissingPassword() {
        String body = "{ \"email\": \"user_missing_pass_" + UUID.randomUUID() + "@test.com\", \"name\": \"TestUser\" }";

        given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без почты")
    @Description("Проверка, что при отсутствии поля email возвращается ошибка 403 с корректным сообщением")
    public void createUserMissingEmail() {
        String body = "{ \"password\": \"123456\", \"name\": \"TestUser\" }";

        given()
                .spec(requestSpec)
                .body(body)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}

