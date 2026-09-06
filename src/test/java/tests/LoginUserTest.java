package tests;

import generators.UserGenerator;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.UserSteps;

import static org.hamcrest.Matchers.*;

public class LoginUserTest {

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
    @DisplayName("Логин под существующим пользователем возвращает 200 и токены")
    public void shouldLoginExistingUser() {
        Response response = UserSteps.login(email, password);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(email));
    }

    @Test
    @DisplayName("Логин с неверным паролем возвращает 401")
    public void shouldNotLoginWithWrongPassword() {
        Response response = UserSteps.login(email, "wrong-" + password);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
