import api.Base;
import api.CreateUser;
import api.DeleteUser;
import api.Login;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {

    String userName = RandomStringUtils.randomAlphabetic(10);
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userMail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru";

    @Before
    public void setUp() {
        RestAssured.baseURI = Base.BASE_URL;
    }

    @Test
    @DisplayName("Проверка создания пользователя: код ответа")
    public void createUserPosistive() {
        Response response = CreateUser.createUser(registerRequestBody());
        response
                .then()
                .assertThat()
                .statusCode(200);

    }

    @Step("Получить JSON для тела реквеста")
    public String registerRequestBody() {

        String registerRequestBody = "{\"name\":\"" + userName + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

        return registerRequestBody;
    }

    @Step
    public void deleteTestUser1() {
        String token = Login
                .login(registerRequestBody())
                .then()
                .extract()
                .path("accessToken");

        DeleteUser.deleteUser(token.substring(7));

    }

    @Test
    @DisplayName("Нельзя создать пользователя, который уже зарегистрирован")
    public void testTwoSameUsers() {
        Response response = createTwoSameUsers(registerRequestBody());
        response
                .then()
                .assertThat()
                .statusCode(403);

        response
                .then()
                .assertThat()
                .body("message", equalTo("User already exists"));

    }

    @Step("Создание пользователя, который уже зарегистрирован")
    public Response createTwoSameUsers(String body) {

        CreateUser.createUser(registerRequestBody());
        Response response = CreateUser.createUser(registerRequestBody());

        return response;
    }

    @Step
    public void deleteTestUser2() {
        String token = Login.login(registerRequestBody())
                .then()
                .extract()
                .path("accessToken");

        DeleteUser.deleteUser(token.substring(7));


    }

    @Test
    @DisplayName("Чтобы создать пользователя, нужно передать в ручку все обязательные поля: " +
            "если нет имени, возвращается ошибка")
    public void testCreateUserWithoutName() {

        Response response = CreateUser.createUser(registerRequestBodyWithoutName());
        String messageWithoutName = response
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .extract()
                .path("message");

        assertThat(messageWithoutName, equalTo("Email, password and name are required fields"));

    }

    @Step("Получить JSON для тела реквеста без имени")
    public String registerRequestBodyWithoutName() {

        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";
        return registerRequestBody;
    }

    @Test
    @DisplayName("Чтобы создать пользователя, нужно передать в ручку все обязательные поля: " +
            "если нет пароля, возвращается ошибка")
    public void testCreateUserWithoutPassword() {

        Response response = CreateUser.createUser(registerRequestBodyWithoutPassword());
        String messageWithoutPassword =
                response
                        .then()
                        .assertThat()
                        .statusCode(403)
                        .and()
                        .extract()
                        .path("message");

        assertThat(messageWithoutPassword, equalTo("Email, password and name are required fields"));

    }

    @Step("Получить JSON для тела реквеста без пароля")
    public String registerRequestBodyWithoutPassword() {

        String registerRequestBody = "{\"name\":\"" + userName + "\","
                + "\"email\":\"" + userMail + "\"}";
        return registerRequestBody;
    }


    @Test
    @DisplayName("Чтобы создать пользователя, нужно передать в ручку все обязательные поля: " +
            "если нет почты, возвращается ошибка")
    public void testCreateUserWithoutEmail() {

        Response response = CreateUser.createUser(registerRequestBodyWithoutEmail());
        String messageWithoutEmail = response
                .then()
                .assertThat()
                .statusCode(403)
                .and()
                .extract()
                .path("message");

        assertThat(messageWithoutEmail, equalTo("Email, password and name are required fields"));

    }

    @Step("Получить JSON для тела реквеста без почты")
    public String registerRequestBodyWithoutEmail() {

        String registerRequestBody = "{\"name\":\"" + userName + "\","
                + "\"passwoer\":\"" + userPassword + "\"}";
        return registerRequestBody;
    }

}
