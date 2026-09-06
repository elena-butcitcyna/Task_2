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

import java.util.List;

import static org.hamcrest.Matchers.*;

public class GetUserOrdersTest {

    private static List<String> ingredientIds;

    private String accessToken;

    @BeforeAll
    public static void setUpClass() {
        ingredientIds = IngredientSteps.getIngredientIds(1);
    }

    @BeforeEach
    public void setUp() {
        String email = UserGenerator.randomEmail();
        String password = UserGenerator.randomPassword();
        String name = UserGenerator.randomName();

        Response registerResponse = UserSteps.register(email, password, name);
        accessToken = registerResponse.jsonPath().getString("accessToken");

        OrderSteps.createOrder(accessToken, ingredientIds);
    }

    @AfterEach
    public void tearDown() {
        UserSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем возвращает 200 и список заказов")
    public void shouldGetOrdersForAuthorizedUser() {
        Response response = OrderSteps.getUserOrders(accessToken);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", not(empty()))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов неавторизованным пользователем возвращает 401")
    public void shouldNotGetOrdersForUnauthorizedUser() {
        Response response = OrderSteps.getUserOrders(null);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
