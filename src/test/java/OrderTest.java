import api.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class OrderTest {

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
    @DisplayName("Успешное создание заказа с авторизацией и ингредиентами")
    public void createPositiveOrder() {

        String name = Order
                .order(userToken(), positiveOrderBody())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("name");

        assertThat(name, containsString("бургер"));

    }

    @Step("Тело для тестового пользователя")
    public String testUser() {

        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

        return registerRequestBody;
    }

    @Step("Авторизация для получения токена")
    public String userToken() {

        String token = Login
                .login(testUser())
                .then()
                .extract()
                .path("accessToken");

        return token.substring(7);

    }

    @Step("Получение первого ингредиенты")
    public String firstIngredient() {
        String ing = Ingredient
                .ingredient(userToken())
                .then()
                .extract()
                .path("data[0]._id");

        return ing;

    }

    @Step("Получение второго ингредиенты")
    public String secondIngredient() {
        String ing = Ingredient
                .ingredient(userToken())
                .then()
                .extract()
                .path("data[1]._id");

        return ing;

    }

    @Step("Тело для позитивного запроса")
    public String positiveOrderBody() {

        String body = "{\"ingredients\":" + "[" + "\"" + firstIngredient() + "\"" + ", " + "\"" + secondIngredient() + "\"" + "]" + "}";

        return body;
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutLogin() {

        Order
                .order("", positiveOrderBody())
                .then()
                .assertThat()
                .statusCode(401);

    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {

        String message = Order
                .order(userToken(), orderBodyWithoutIngredients())
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .extract()
                .path("message");

        assertThat(message, equalTo("Ingredient ids must be provided"));

    }

    @Step
    public String orderBodyWithoutIngredients() {
        String body = "{\"ingredients\":" + "\"" + "\"" + "}";

        return body;
    }

    @Test
    @DisplayName("Успешное создание заказа с авторизацией и неверными ингредиентами")
    public void createOrderWithBadIngredients() {

        Order
                .order(userToken(), negativeOrderBody())
                .then()
                .assertThat()
                .statusCode(500);

    }

    @Step("Тело для позитивного запроса")
    public String negativeOrderBody() {

        String body = "{\"ingredients\":" + "[" + "\"" + firstIngredient() + "222" + "\"" + ", " + "\"" + secondIngredient() + "3333" + "\"" + "]" + "}";

        return body;
    }

    @After
    public void tearDown() {

        DeleteUser.deleteUser(userToken());

    }

}
