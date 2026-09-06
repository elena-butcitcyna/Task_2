package tests;

import generators.UserGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.UserSteps;

import static org.hamcrest.Matchers.*;

public class UpdateUserTest {

    private String email;
    private String password;
    private String accessToken;

    @BeforeEach
    public void setUp() {
        email = UserGenerator.randomEmail();
        password = UserGenerator.randomPassword();
        String name = UserGenerator.randomName();

        Response registerResponse = UserSteps.register(email, password, name);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @AfterEach
    public void tearDown() {
        UserSteps.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Обновление имени с авторизацией возвращает 200 и новое имя")
    public void shouldUpdateNameWithAuth() {
        String newName = UserGenerator.randomName();

        Response response = UserSteps.updateUserField(accessToken, "name", newName);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Обновление email с авторизацией возвращает 200 и новый email")
    public void shouldUpdateEmailWithAuth() {
        String newEmail = UserGenerator.randomEmail();

        Response response = UserSteps.updateUserField(accessToken, "email", newEmail);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(newEmail));
    }

    @Test
    @DisplayName("Обновление пароля с авторизацией позволяет войти с новым паролем")
    public void shouldUpdatePasswordWithAuth() {
        String newPassword = UserGenerator.randomPassword();

        Response updateResponse = UserSteps.updateUserField(accessToken, "password", newPassword);
        updateResponse.then()
                .statusCode(200)
                .body("success", is(true));

        Response loginResponse = UserSteps.login(email, newPassword);
        loginResponse.then()
                .statusCode(200)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Обновление данных без авторизации возвращает 401")
    public void shouldNotUpdateWithoutAuth() {
        Response response = UserSteps.updateUserField(null, "name", UserGenerator.randomName());

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
