import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CreateUserTest {

    String userName = RandomStringUtils.randomAlphabetic(10);
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userMail = RandomStringUtils.randomAlphabetic(5)+"@" + RandomStringUtils.randomAlphabetic(5)+".ru" ;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Проверка создания пользователя: код ответа")
    public void createUserPosistive(){
        Response response = createUserPositive(registerRequestBody());
        response.then().assertThat().statusCode(200);

     }

    @Step ("Позитивный запрос на создание пользователя")
    public Response createUserPositive (String body){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/register");

        return response;
    }

    @Step("Получить JSON для тела реквеста")
    public String registerRequestBody(){

        String registerRequestBody = "{\"name\":\"" + userName + "\","
                + "\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";
        return registerRequestBody;
    }

    @Step
    public void deleteTestUser1(){
        String token = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody())
                .when()
                .post("/api/auth/login").then().extract().path("accessToken");

        given()
                .auth().oauth2(token.substring(7))
                .when()
                .delete("/api/auth/user");

       }

    @Test
    @DisplayName("Нельзя создать пользователя, который уже зарегистрирован")
    public void testTwoSameUsers() {
        Response response = createTwoSameUsers(registerRequestBody());
        response.then().assertThat().statusCode(403);
        response.then().assertThat().
                body("message", equalTo("User already exists"));

       }

    @Step("Создание пользователя, который уже зарегистрирован")
    public Response createTwoSameUsers(String body){

        given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/register");

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/register");

        return response;
    }

    @Step
    public void deleteTestUser2(){
        String token = given()
                .header("Content-type", "application/json")
                .and()
                .body(registerRequestBody())
                .when()
                .post("/api/auth/login").then().extract().path("accessToken");

        given()
                .auth().oauth2(token.substring(7))
                .when()
                .delete("/api/auth/user");


    }

    @Test
    @DisplayName("Чтобы создать пользователя, нужно передать в ручку все обязательные поля: " +
            "если нет имени, возвращается ошибка")
    public void testCreateUserWithoutName() {

        Response response = createUserWithoutName(registerRequestBodyWithoutName());
        String messageWithoutName = response.then().assertThat()
                .statusCode(403).and().extract()
                .path("message");

        assertThat(messageWithoutName, equalTo("Email, password and name are required fields"));

    }

    @Step("Получить JSON для тела реквеста без имени")
    public String registerRequestBodyWithoutName(){

        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";
        return registerRequestBody;
    }

    @Step ("Создание пользователя без имени")
    public Response createUserWithoutName (String body){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/register");

        return response;
    }

    @Test
    @DisplayName("Чтобы создать пользователя, нужно передать в ручку все обязательные поля: " +
            "если нет пароля, возвращается ошибка")
    public void testCreateUserWithoutPassword(){

        Response response = createUserWithoutPassword(registerRequestBodyWithoutPassword());
        String messageWithoutPassword =
                response.then().assertThat().statusCode(403).and().extract()
                        .path("message");

        assertThat(messageWithoutPassword, equalTo("Email, password and name are required fields"));

    }

    @Step("Получить JSON для тела реквеста без пароля")
    public String registerRequestBodyWithoutPassword(){

        String registerRequestBody =  "{\"name\":\"" + userName + "\","
                + "\"email\":\"" + userMail + "\"}";
        return registerRequestBody;
    }

    @Step ("Создание пользователя без пароля")
    public Response createUserWithoutPassword (String body){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/register");

        return response;
    }

    @Test
    @DisplayName("Чтобы создать пользователя, нужно передать в ручку все обязательные поля: " +
            "если нет почты, возвращается ошибка")
    public void testCreateUserWithoutEmail(){

        Response response = createUserWithoutEmail(registerRequestBodyWithoutEmail());
        String messageWithoutEmail =
                response.then().assertThat().statusCode(403).and().extract()
                        .path("message");

        assertThat(messageWithoutEmail, equalTo("Email, password and name are required fields"));

    }

    @Step("Получить JSON для тела реквеста без почты")
    public String registerRequestBodyWithoutEmail(){

        String registerRequestBody =  "{\"name\":\"" + userName + "\","
                + "\"passwoer\":\"" + userPassword + "\"}";
        return registerRequestBody;
    }

    @Step ("Создание пользователя без почты")
    public Response createUserWithoutEmail (String body){

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(body)
                .when()
                .post("/api/auth/register");

        return response;
    }

}
