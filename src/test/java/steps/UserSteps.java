package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static config.ApiConfig.BASE_URI;
import static io.restassured.RestAssured.given;

public final class UserSteps {

    private UserSteps() {
    }

    @Step("Регистрация пользователя с email {email}")
    public static Response register(String email, String password, String name) {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"name\":\"" + name + "\"}";
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/auth/register");
    }

    @Step("Регистрация пользователя без обязательного поля password")
    public static Response registerWithoutPassword(String email, String name) {
        String body = "{\"email\":\"" + email + "\",\"name\":\"" + name + "\"}";
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/auth/register");
    }

    @Step("Авторизация пользователя с email {email}")
    public static Response login(String email, String password) {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        return given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/auth/login");
    }

    @Step("Обновление поля {field} пользователя")
    public static Response updateUserField(String accessToken, String field, String value) {
        String body = "{\"" + field + "\":\"" + value + "\"}";
        io.restassured.specification.RequestSpecification request = given()
                .baseUri(BASE_URI)
                .header("Content-type", "application/json")
                .body(body);

        if (accessToken != null) {
            request.header("Authorization", accessToken);
        }

        return request
                .when()
                .patch("/auth/user");
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken) {
        return given()
                .baseUri(BASE_URI)
                .header("Authorization", accessToken)
                .when()
                .delete("/auth/user");
    }
}
