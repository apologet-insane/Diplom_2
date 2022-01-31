import api.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class PatchUserTest {

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
    @DisplayName("Изменения данных пользователя с авторизацией: имя")
    public void testPositvePatchWithAuthorizationName() {
        boolean success = PatchUser
                .patchUser(userToken(), testPositiveUserName())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("success");

        assertThat(String.valueOf(success), true);

    }


    @Step("Тело для изменения имени")
    public String testPositiveUserName() {
        String registerRequestBody = "{\"name\":\"" + userName + userName + "\"}";

        return registerRequestBody;
    }

    @Step("Тело для авторизации")
    public static String bodyLogin() {

        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

        return registerRequestBody;
    }

    @Step("Авторизация для получения токена")
    public static String userToken() {

        String token = Login
                .login(bodyLogin())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("accessToken");

        return token.substring(7);

    }

    @Test
    @DisplayName("Изменения данных пользователя с авторизацией: email")
    public void testPositvePatchWithAuthorizationMail() {

        boolean success = PatchUser
                .patchUser(userToken(), testPositiveUserMail())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("success");

        assertThat(String.valueOf(success), true);

    }


    @Step("Тело для изменения email")
    public String testPositiveUserMail() {

        String registerRequestBody = "{\"email\":\"" + userName + userMail + "\"}";

        return registerRequestBody;
    }

    @Test
    @DisplayName("Изменения данных пользователя с авторизацией: пароль")
    public void testPositvePatchWithAuthorizationPassword() {

        boolean success = PatchUser
                .patchUser(userToken(), testPositiveUserPassword())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("success");

        assertThat(String.valueOf(success), true);

    }


    @Step("Тело для изменения пароля")
    public String testPositiveUserPassword() {

        String registerRequestBody = "{\"password\":\"" + userName + userPassword + "\"}";

        return registerRequestBody;
    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации: пароль")
    public void testPatchWithoutAuthorizationPassword() {

        String message = PatchUser
                .patchUser("", testPositiveUserPassword())
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .extract()
                .path("message");

        assertThat(message, equalTo("You should be authorised"));

    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации: email")
    public void testPatchWithoutAuthorizationMail() {

        String message = PatchUser
                .patchUser("", testPositiveUserMail())
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .extract()
                .path("message");

        assertThat(message, equalTo("You should be authorised"));

    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации: имя")
    public void testPatchWithoutAuthorizationName() {

        String message = PatchUser
                .patchUser("", testPositiveUserName())
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .extract()
                .path("message");

        assertThat(message, equalTo("You should be authorised"));

    }

    @Step("Если поместить этот метод в After, все тесты падают")
    public void tearDown() {

        DeleteUser
                .deleteUser(userToken());

    }

}
