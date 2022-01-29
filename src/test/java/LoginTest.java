import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class LoginTest {

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
    @DisplayName("Пользователь может авторизоваться, успешная авторизация возвращает токен")
    public void testPositiveUserLogin() {

        Response response = loginUserPositive(testPositiveUser());
        String token =  response.then().assertThat().statusCode(200).and().extract().path("accessToken");
        assertThat(token, notNullValue());

    }

 @Step ("Тело для тестового пользователя")
    public String testPositiveUser() {
        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

       return registerRequestBody;
   }

    @Step ("Позитивная авторизация")
    public Response loginUserPositive(String testUser){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(testUser)
                .when()
                .post("/api/auth/login");

        return response;
    }

    @Test
    @DisplayName("Если email неверный, запрос вернёт ошибку")
    public void testLoginWithBadLogin() {

        Response response = loginWithBadEmail(requestBodyLoginWithBadEmail());
        String messageWithBadLogin = response.then().assertThat()
                .statusCode(401).and().extract().path("message");

        assertThat(messageWithBadLogin, equalTo("email or password are incorrect"));

    }

    @Step ("Тело реквеста для авторизации c неправильным email")
    public String requestBodyLoginWithBadEmail(){

        String loginRequestBodyWithBadEmail  = "{\"email\":\"" + userName + "\","
                + "\"password\":\"" + userPassword + "\"}";

        return loginRequestBodyWithBadEmail;
    }

    @Step("Авторизация с неправильным email")
    public Response loginWithBadEmail(String body) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/login");

        return response;
    }

    @Test
    @DisplayName("Если пароль неверный, запрос вернёт ошибку")
    public void testLoginWithBadPassword() {

        Response response = loginWithBadPassword(requestBodyLoginWithBadPassword());
        String messageWithBadPassword = response.then().assertThat().statusCode(401).and().extract().path("message");

        assertThat(messageWithBadPassword, equalTo("email or password are incorrect"));


    }

    @Step ("Тело реквеста для авторизации c неправильным паролем")
    public String requestBodyLoginWithBadPassword(){

        String loginRequestBodyWithBadPassword = "{\"email\":\"" + userMail + "\","
                + "\"password\":\"" + userName + "\"}";

        return loginRequestBodyWithBadPassword;
    }

    @Step("Авторизация с неверным паролем")
    public Response loginWithBadPassword(String body) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/login");

        return response;

    }


    @Test
    @DisplayName("Если пароль и email неверный, запрос вернёт ошибку ")
    public void testLoginWithBadEmailPassword() {

        Response response = loginWithBadEmailPassword(requestBodyLoginWithBadEmailPassword());
        String messageWithBadEmailPassword = response.then()
                .assertThat().statusCode(401).and().extract().path("message");

        assertThat(messageWithBadEmailPassword, equalTo("email or password are incorrect"));
      
    }

    @Step ("Тело реквеста для авторизации c неправильным логином и паролем")
    public String requestBodyLoginWithBadEmailPassword(){

        String loginRequestBodyWithBadEmailPassword = "{\"email\":\"" + userPassword + "\","
                + "\"password\":\"" + userName + "\"}";

        return loginRequestBodyWithBadEmailPassword;
    }

    @Step("Авторизация с неверным email и паролем")
    public Response loginWithBadEmailPassword(String body) {

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/login");

        return response;
    }


@After
public void tearDown(){
    String token = loginUserPositive(testPositiveUser()).then().extract().path("accessToken");

    given()
            .auth().oauth2(token.substring(7))
            .when()
            .delete("/api/auth/user").then().statusCode(202);

   }

}
