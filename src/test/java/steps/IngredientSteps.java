package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static config.ApiConfig.BASE_URI;
import static io.restassured.RestAssured.given;

public final class IngredientSteps {

    private IngredientSteps() {
    }

    @Step("Получение списка ингредиентов")
    public static Response getIngredients() {
        return given()
                .baseUri(BASE_URI)
                .when()
                .get("/ingredients");
    }

    @Step("Получение хешей первых {count} ингредиентов")
    public static List<String> getIngredientIds(int count) {
        return getIngredients()
                .then()
                .extract()
                .jsonPath()
                .getList("data._id", String.class)
                .subList(0, count);
    }
}
