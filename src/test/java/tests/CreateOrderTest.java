package tests;

import generators.UserGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.IngredientSteps;
import steps.OrderSteps;
import steps.UserSteps;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class CreateOrderTest {

    private static List<String> ingredientIds;

    private String email;
    private String accessToken;

    @BeforeAll
    public static void setUpClass() {
        ingredientIds = IngredientSteps.getIngredientIds(2);
    }

    @BeforeEach
    public void setUp() {
        email = UserGenerator.randomEmail();
        String password = UserGenerator.randomPassword();
        String name = UserGenerator.randomName();

        Response registerResponse = UserSteps.register(email, password, name);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @AfterEach
    public void tearDown() {
        UserSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией привязывает заказ к пользователю")
    public void shouldCreateOrderWithAuth() {
        Response response = OrderSteps.createOrder(accessToken, ingredientIds);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue())
                .body("order.owner.email", equalTo(email));
    }

    @Test
    @DisplayName("Создание заказа без авторизации возвращает 200")
    public void shouldCreateOrderWithoutAuth() {
        Response response = OrderSteps.createOrder(null, ingredientIds);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами возвращает заказ с тем же количеством ингредиентов")
    public void shouldCreateOrderWithIngredients() {
        Response response = OrderSteps.createOrder(accessToken, ingredientIds);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.ingredients.size()", equalTo(ingredientIds.size()));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает 400")
    public void shouldNotCreateOrderWithoutIngredients() {
        Response response = OrderSteps.createOrder(accessToken, Collections.emptyList());

        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента возвращает 500")
    public void shouldNotCreateOrderWithInvalidIngredientHash() {
        Response response = OrderSteps.createOrder(accessToken, List.of("invalidhash123"));

        response.then()
                .statusCode(500);
    }
}
