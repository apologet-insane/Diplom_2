import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class PatchUserTest {

    public static String userName = RandomStringUtils.randomAlphabetic(10);
    public static String userPassword = RandomStringUtils.randomAlphabetic(10);
    public static String userMail = RandomStringUtils.randomAlphabetic(5) + "@" + RandomStringUtils.randomAlphabetic(5) + ".ru";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
    @Before
    public void createTestUser() {
        String registerRequestBody = "{\"name\":\"" + userName + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";
        RestAssured.given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody)
                .when()
                .post("https://stellarburgers.nomoreparties.site/api/auth/register");
    }

    @Test
    @DisplayName("Изменения данных пользователя с авторизацией: имя")
    public void testPositvePatchWithAuthorizationName(){
      boolean success =  given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(testPositiveUserName())
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(200).and().extract().path("success");

      assertThat(String.valueOf(success), true);

    }


    @Step ("Тело для изменения имени")
    public String testPositiveUserName() {
        String registerRequestBody = "{\"name\":\"" + userName + userName + "\"}";

        return registerRequestBody;
    }

    @Step("Авторизация для получения токена")
    public static String userToken() {
        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";


            String token =  given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(registerRequestBody)
                    .when()
                    .post("/api/auth/login")
                    .then().assertThat().statusCode(200).and().extract().path("accessToken");

            return token;

    }

    @Test
    @DisplayName("Изменения данных пользователя с авторизацией: email")
    public void testPositvePatchWithAuthorizationMail(){
        boolean success =  given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(testPositiveUserMail())
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(200).and().extract().path("success");

        assertThat(String.valueOf(success), true);

    }


    @Step ("Тело для изменения email")
    public String testPositiveUserMail() {
        String registerRequestBody = "{\"email\":\"" + userName + userMail + "\"}";

        return registerRequestBody;
    }

    @Test
    @DisplayName("Изменения данных пользователя с авторизацией: пароль")
    public void testPositvePatchWithAuthorizationPassword(){
        boolean success =  given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(testPositiveUserPassword())
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(200).and().extract().path("success");

        assertThat(String.valueOf(success), true);

    }


    @Step ("Тело для изменения пароля")
    public String testPositiveUserPassword() {
        String registerRequestBody = "{\"password\":\"" + userName + userPassword + "\"}";

        return registerRequestBody;
    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации: пароль")
    public void testPatchWithoutAuthorizationPassword(){
       String message =  given()
                .header("Content-type", "application/json")
                .and()
                .body(testPositiveUserPassword())
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(401).and().extract().path("message");

        assertThat(message, equalTo("You should be authorised"));

    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации: email")
    public void testPatchWithoutAuthorizationMail(){
        String message =  given()
                .header("Content-type", "application/json")
                .and()
                .body(testPositiveUserMail())
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(401).and().extract().path("message");

        assertThat(message, equalTo("You should be authorised"));

    }

    @Test
    @DisplayName("Изменения данных пользователя без авторизации: имя")
    public void testPatchWithoutAuthorizationName(){
        String message =  given()
                .header("Content-type", "application/json")
                .and()
                .body(testPositiveUserName())
                .when()
                .patch("/api/auth/user")
                .then().assertThat().statusCode(401).and().extract().path("message");

       assertThat(message, equalTo("You should be authorised"));

    }

   @Step
    public void tearDown(){

        given()
                .auth().oauth2(userToken().substring(7))
                .when()
                .delete("/api/auth/user");

    }

}
