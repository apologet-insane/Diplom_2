import api.Base;
import api.CreateUser;
import api.DeleteUser;
import api.Login;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class LoginTest {

    public static String userName = RandomStringUtils.randomAlphabetic(10);
    public static String userPassword = RandomStringUtils.randomAlphabetic(10);
    public static String userMail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru";

    @Before
    public void setUp() {
        RestAssured.baseURI = Base.BASE_URL;
    }

    @Before
    public void createTestUser() {
        String registerRequestBody = "{\"name\":\"" + userName + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

        CreateUser.createUser(registerRequestBody);
    }


    @Test
    @DisplayName("Пользователь может авторизоваться, успешная авторизация возвращает токен")
    public void testPositiveUserLogin() {

        Response response = Login.login(testPositiveUser());
        String token = response
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("accessToken");

        assertThat(token, notNullValue());

    }

    @Step("Тело для тестового пользователя")
    public String testPositiveUser() {
        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

        return registerRequestBody;
    }


    @Test
    @DisplayName("Если email неверный, запрос вернёт ошибку")
    public void testLoginWithBadLogin() {

        Response response = Login.login(requestBodyLoginWithBadEmail());
        String messageWithBadLogin = response
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .extract()
                .path("message");

        assertThat(messageWithBadLogin, equalTo("email or password are incorrect"));

    }

    @Step("Тело реквеста для авторизации c неправильным email")
    public String requestBodyLoginWithBadEmail() {

        String loginRequestBodyWithBadEmail = "{\"email\":\"" + userName + "\","
                + "\"password\":\"" + userPassword + "\"}";

        return loginRequestBodyWithBadEmail;
    }

    @Test
    @DisplayName("Если пароль неверный, запрос вернёт ошибку")
    public void testLoginWithBadPassword() {

        Response response = Login.login(requestBodyLoginWithBadPassword());
        String messageWithBadPassword = response
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .extract()
                .path("message");

        assertThat(messageWithBadPassword, equalTo("email or password are incorrect"));

    }

    @Step("Тело реквеста для авторизации c неправильным паролем")
    public String requestBodyLoginWithBadPassword() {

        String loginRequestBodyWithBadPassword = "{\"email\":\"" + userMail + "\","
                + "\"password\":\"" + userName + "\"}";

        return loginRequestBodyWithBadPassword;
    }

    @Test
    @DisplayName("Если пароль и email неверный, запрос вернёт ошибку ")
    public void testLoginWithBadEmailPassword() {

        Response response = Login.login(requestBodyLoginWithBadEmailPassword());
        String messageWithBadEmailPassword = response
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .extract()
                .path("message");

        assertThat(messageWithBadEmailPassword, equalTo("email or password are incorrect"));

    }

    @Step("Тело реквеста для авторизации c неправильным логином и паролем")
    public String requestBodyLoginWithBadEmailPassword() {

        String loginRequestBodyWithBadEmailPassword = "{\"email\":\"" + userPassword + "\","
                + "\"password\":\"" + userName + "\"}";

        return loginRequestBodyWithBadEmailPassword;
    }

    @After
    public void tearDown() {
        String token = Login
                .login(testPositiveUser())
                .then()
                .extract()
                .path("accessToken");

        DeleteUser.deleteUser(token.substring(7));

    }

}
