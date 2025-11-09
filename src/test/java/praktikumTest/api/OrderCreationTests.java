package praktikumTest.api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class OrderCreationTests {

    private OrderClient orderClient;
    private String accessToken;
    private String email;
    private final String password = "123456";
    private final String name = "OrderUser";

    @Before
    public void setup() {
        orderClient = new OrderClient();

        email = "user_" + UUID.randomUUID() + "@test.com";
        User user = new User(email, password, name);

        // Регистрация пользователя через статический метод UserClient
        Response createResponse = UserClient.registerUser(user);
        createResponse.then().statusCode(200);
        accessToken = createResponse.path("accessToken");
    }

    @After
    public void cleanup() {
        if (accessToken != null) {
            UserClient.deleteUser(accessToken);
        }
    }

    // Получение ID первого ингредиента
    private String getFirstIngredientId() {
        Response response = orderClient.getIngredients();
        response.then().statusCode(200);
        return response.path("data[0]._id");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка успешного создания заказа при наличии accessToken и валидных ингредиентов")
    public void createOrderWithAuth() {
        String ingredientId = getFirstIngredientId();
        Order order = new Order(List.of(ingredientId));

        Response response = orderClient.createOrder(order, accessToken);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка возможности создания заказа без accessToken")
    public void createOrderWithoutAuth() {
        String ingredientId = getFirstIngredientId();
        Order order = new Order(List.of(ingredientId));

        Response response = orderClient.createOrder(order, null);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка, что при отсутствии ингредиентов возвращается 400 и корректное сообщение")
    public void createOrderWithoutIngredients() {
        Order emptyOrder = new Order(List.of());

        Response response = orderClient.createOrder(emptyOrder, accessToken);
        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным ингредиентом")
    @Description("Проверка, что при неверном ID ингредиента сервер возвращает 500 Internal Server Error")
    public void createOrderWithInvalidIngredientHash() {
        Order invalidOrder = new Order(List.of("invalid_id_123"));

        Response response = orderClient.createOrder(invalidOrder, accessToken);
        response.then()
                .statusCode(500)
                .body(containsString("Internal Server Error")); // проверяем, что тело содержит текст
    }


    @Test
    @DisplayName("Создание заказа с несколькими ингредиентами")
    @Description("Проверка, что можно создать заказ с несколькими валидными ингредиентами")
    public void createOrderWithMultipleIngredients() {
        Response ingredientsResponse = orderClient.getIngredients();
        ingredientsResponse.then().statusCode(200);

        List<String> ingredients = ingredientsResponse.path("data._id");
        // Берем первые два ингредиента для теста
        Order order = new Order(ingredients.subList(0, Math.min(2, ingredients.size())));

        Response response = orderClient.createOrder(order, accessToken);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }
}
