package praktikumTest.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikumTest.api.User;
import praktikumTest.api.UserClient;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class UserLoginTests {

    private String accessToken;
    private String email;
    private final String password = "123456";
    private final String name = "TestUser";

    @Before
    public void createUser() {
        // создаем уникального пользователя перед каждым тестом
        email = "user_" + UUID.randomUUID() + "@test.com";
        User user = new User(email, password, name);

        Response response = UserClient.registerUser(user);
        accessToken = response.then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken)
                    .then()
                    .statusCode(anyOf(is(200), is(202)));
        }
    }

    @Test
    @DisplayName("Успешный логин существующего пользователя")
    @Description("Проверка, что пользователь с корректными данными может авторизоваться и получить токены")
    public void loginExistingUser() {
        User credentials = new User(email, password, null);

        UserClient.loginUser(credentials)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что при вводе неверного пароля возвращается ошибка 401 и корректное сообщение")
    public void loginWithWrongPassword() {
        User credentials = new User(email, "wrongPassword", null);

        UserClient.loginUser(credentials)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин несуществующего пользователя")
    @Description("Проверка, что при вводе несуществующих учетных данных возвращается ошибка 401")
    public void loginWithInvalidCredentials() {
        User wrongCredentials = new User("nonexistent_" + UUID.randomUUID() + "@test.com", "wrongpass", null);

        UserClient.loginUser(wrongCredentials)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
