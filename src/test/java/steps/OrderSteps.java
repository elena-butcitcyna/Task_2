package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static config.ApiConfig.BASE_URI;
import static io.restassured.RestAssured.given;

public final class OrderSteps {

    private OrderSteps() {
    }

    @Step("Создание заказа с ингредиентами {ingredientIds}")
    public static Response createOrder(String accessToken, List<String> ingredientIds) {
        String ingredientsJson = ingredientIds.stream()
                .map(id -> "\"" + id + "\"")
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        String body = "{\"ingredients\":[" + ingredientsJson + "]}";

        RequestSpecification request = given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(body);

        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }

        return request
                .when()
                .post("/orders");
    }

    @Step("Получение заказов пользователя")
    public static Response getUserOrders(String accessToken) {
        RequestSpecification request = given()
                .baseUri(BASE_URI);

        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }

        return request
                .when()
                .get("/orders");
    }
}
