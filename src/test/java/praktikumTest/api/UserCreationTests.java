package praktikumTest.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class UserCreationTests {

    private String accessToken;
    private final String password = "123456";
    private final String name = "TestUser";

    @After
    public void cleanupUser() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken)
                    .then()
                    .statusCode(anyOf(is(200), is(202)));
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешной регистрации нового пользователя с уникальным email")
    public void createUniqueUser() {
        String email = "user_" + UUID.randomUUID() + "@test.com";
        User user = new User(email, password, name);

        Response response = UserClient.registerUser(user);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(email));

        accessToken = response.path("accessToken");
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    @Description("Проверка, что нельзя зарегистрировать пользователя с уже существующим email")
    public void createExistingUser() {
        User existingUser = new User("existinguser@test.com", password, name);

        UserClient.registerUser(existingUser)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без имени")
    @Description("Проверка, что при отсутствии поля name возвращается ошибка 403 с корректным сообщением")
    public void createUserMissingName() {
        User user = new User("user_missing_" + UUID.randomUUID() + "@test.com", password, null);

        UserClient.registerUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без пароля")
    @Description("Проверка, что при отсутствии поля password возвращается ошибка 403")
    public void createUserMissingPassword() {
        User user = new User("user_missing_pass_" + UUID.randomUUID() + "@test.com", null, name);

        UserClient.registerUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без почты")
    @Description("Проверка, что при отсутствии поля email возвращается ошибка 403")
    public void createUserMissingEmail() {
        User user = new User(null, password, name);

        UserClient.registerUser(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
