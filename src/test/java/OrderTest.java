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

public class OrderTest {

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
    @DisplayName("Успешное создание заказа с авторизацией и ингредиентами")
    public void createPositiveOrder (){

        String name = given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(positiveOrderBody())
                .when()
                .post("/api/orders")
                .then().assertThat().statusCode(200).and().extract().path("name");

        assertThat(name, containsString("бургер"));

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
    @Step ("Тело для позитивного запроса")
    public String positiveOrderBody(){

      String body = "{\"ingredients\":" + "["+"\""+ firstIngredient() + "\""+ ", " +"\"" +secondIngredient()+"\""+"]" + "}";
      return body;
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutLogin (){

      given()
                .header("Content-type", "application/json")
                .and()
                .body(positiveOrderBody())
                .when()
                .post("/api/orders")
                .then().assertThat().statusCode(401);

    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients (){

        String message = given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body("{\"ingredients\":" +"\""+"\"" + "}")
                .when()
                .post("/api/orders")
                .then().assertThat().statusCode(400).and().extract().path("message");

        assertThat(message, equalTo("Ingredient ids must be provided"));

    }

    @Test
    @DisplayName("Успешное создание заказа с авторизацией и ингредиентами")
    public void createOrderWithBadIngredients (){

        given()
                .header("Content-type", "application/json")
                .auth().oauth2(userToken().substring(7))
                .and()
                .body(negativeOrderBody())
                .when()
                .post("/api/orders")
                .then().assertThat().statusCode(500);


    }

    @Step ("Тело для позитивного запроса")
    public String negativeOrderBody(){

        String body = "{\"ingredients\":" + "["+"\""+ firstIngredient()+"222" + "\""+ ", " +"\"" +secondIngredient()+"3333"+"\""+"]" + "}";
        return body;
    }



    @After
    public void tearDown(){
            given()
                .auth().oauth2(userToken().substring(7))
                .when()
                .delete("/api/auth/user").then().statusCode(202);

    }

}
