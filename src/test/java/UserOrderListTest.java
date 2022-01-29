import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserOrderListTest {
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
    @DisplayName("Получение списка заказов у пользователя с авторизацией")
    public void userOrderWithAuthorization () {

        int testBody =  given()
                .auth().oauth2(userToken().substring(7))
                .and()
                .when()
                .get("/api/orders")
                .then().assertThat().statusCode(200).and().extract().path("totalToday");

        assertThat(testBody, notNullValue());

    }

    @Step("Авторизация для получения токена")
    public String userToken() {
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

    @Step ("Получение первого ингредиенты")
    public String firstIngredient(){
        String ing = given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .when()
                .get("/api/ingredients")
                .then().assertThat().statusCode(200).and().extract().path("data[0]._id");
        return  ing;

    }

    @Step ("Получение второго ингредиенты")
    public String secondIngredient(){
        String ing = given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .when()
                .get("/api/ingredients")
                .then().assertThat().statusCode(200).and().extract().path("data[1]._id");
        return  ing;

    }

    @Step ("Получение третьего ингредиенты")
    public String thirdIngredient(){
        String ing = given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .when()
                .get("/api/ingredients")
                .then().assertThat().statusCode(200).and().extract().path("data[2]._id");
        return  ing;

    }

    @Step ("Получение четвёртого ингредиенты")
    public String fourthIngredient(){
        String ing = given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .when()
                .get("/api/ingredients")
                .then().assertThat().statusCode(200).and().extract().path("data[3]._id");
        return  ing;

    }
    @Step ("Тело для первого заказа")
    public String firstOrderBody(){

        String body = "{\"ingredients\":" + "["+"\""+ firstIngredient() + "\""+ ", " +"\"" +secondIngredient()+"\""+"]" + "}";
        return body;
    }

    @Step ("Тело для второго заказа")
    public String secondOrderBody(){

        String body = "{\"ingredients\":" + "["+"\""+ thirdIngredient() + "\""+ ", " +"\"" +fourthIngredient()+"\""+"]" + "}";
        return body;
    }

    @Step ("Тело для третьего заказа")
    public String thirdOrderBody(){

        String body = "{\"ingredients\":" + "["+"\""+ thirdIngredient() + "\""+ ", " +"\"" +firstIngredient()+"\""+"]" + "}";
        return body;
    }

    @Step ("Формирование первого заказа")
    public void firstOrder (){

      given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(firstOrderBody())
                .when()
                .post("/api/orders")
                .then().assertThat().statusCode(200);

    }

    @Step ("Формирование второго заказа")
    public void secondOrder (){

        given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(secondOrderBody())
                .when()
                .post("/api/orders")
                .then().assertThat().statusCode(200);

    }

    @Step ("Формирование третьего заказа")
    public void thirdOrder (){

        given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(thirdOrderBody())
                .when()
                .post("/api/orders")
                .then().assertThat().statusCode(200);
    }

    @Test
    @DisplayName("Список заказов неавторизованного пользователя")
    public void userOrderWithoutAuthorization () {

       String message =  given()
                .when()
                .get("/api/orders")
                .then().assertThat().statusCode(401).and().extract().path("message");

        assertThat(message, equalTo("You should be authorised"));

    }


    @After
    public void tearDown(){
        given()
                .auth().oauth2(userToken().substring(7))
                .when()
                .delete("/api/auth/user").then().statusCode(202);

    }

}
