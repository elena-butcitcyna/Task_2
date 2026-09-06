package tests;

import generators.UserGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.UserSteps;

import static org.hamcrest.Matchers.*;

public class RegisterUserTest {

    private String accessToken;

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            UserSteps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Регистрация уникального пользователя возвращает 200 и данные пользователя")
    public void shouldRegisterUniqueUser() {
        String email = UserGenerator.randomEmail();
        String password = UserGenerator.randomPassword();
        String name = UserGenerator.randomName();

        Response response = UserSteps.register(email, password, name);
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Повторная регистрация с тем же email возвращает 403")
    public void shouldNotRegisterExistingUser() {
        String email = UserGenerator.randomEmail();
        String password = UserGenerator.randomPassword();
        String name = UserGenerator.randomName();

        Response first = UserSteps.register(email, password, name);
        accessToken = first.jsonPath().getString("accessToken");

        Response second = UserSteps.register(email, password, name);

        second.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Регистрация без обязательного поля password возвращает 403")
    public void shouldNotRegisterWithoutRequiredField() {
        String email = UserGenerator.randomEmail();
        String name = UserGenerator.randomName();

        Response response = UserSteps.registerWithoutPassword(email, name);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
