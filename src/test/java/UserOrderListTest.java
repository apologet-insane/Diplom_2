import api.*;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class UserOrderListTest {

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
    @DisplayName("Получение списка заказов у пользователя с авторизацией")
    public void userOrderWithAuthorization() {

        int testBody = UserOrderList
                .userOrderList(userToken())
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .extract()
                .path("totalToday");

        assertThat(testBody, notNullValue());

    }

    @Step("Тело для авторизации")
    public static String bodyLogin() {

        String registerRequestBody = "{\"password\":\"" + userPassword + "\","
                + "\"email\":\"" + userMail + "\"}";

        return registerRequestBody;
    }

    @Step("Авторизация для получения токена")
    public String userToken() {

        String token = Login
                .login(bodyLogin())
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

    @Step("Получение третьего ингредиенты")
    public String thirdIngredient() {

        String ing = Ingredient
                .ingredient(userToken())
                .then()
                .extract()
                .path("data[2]._id");

        return ing;

    }

    @Step("Получение четвёртого ингредиенты")
    public String fourthIngredient() {

        String ing = Ingredient
                .ingredient(userToken())
                .then()
                .extract()
                .path("data[3]._id");

        return ing;

    }

    @Step("Тело для первого заказа")
    public String firstOrderBody() {

        String body = "{\"ingredients\":" + "[" + "\"" + firstIngredient() + "\"" + ", " + "\"" + secondIngredient() + "\"" + "]" + "}";
        return body;
    }

    @Step("Тело для второго заказа")
    public String secondOrderBody() {

        String body = "{\"ingredients\":" + "[" + "\"" + thirdIngredient() + "\"" + ", " + "\"" + fourthIngredient() + "\"" + "]" + "}";
        return body;
    }

    @Step("Тело для третьего заказа")
    public String thirdOrderBody() {

        String body = "{\"ingredients\":" + "[" + "\"" + thirdIngredient() + "\"" + ", " + "\"" + firstIngredient() + "\"" + "]" + "}";
        return body;
    }

    @Step("Формирование первого заказа")
    public void firstOrder() {

        Order.order(userToken(), firstOrderBody());

    }

    @Step("Формирование второго заказа")
    public void secondOrder() {

        Order.order(userToken(), secondOrderBody());

    }

    @Step("Формирование третьего заказа")
    public void thirdOrder() {

        Order.order(userToken(), thirdOrderBody());
    }

    @Test
    @DisplayName("Список заказов неавторизованного пользователя")
    public void userOrderWithoutAuthorization() {

        String message = UserOrderList
                .userOrderList("")
                .then()
                .assertThat()
                .statusCode(401)
                .and()
                .extract()
                .path("message");

        assertThat(message, equalTo("You should be authorised"));

    }

    @After
    public void tearDown() {

        DeleteUser.deleteUser(userToken());

    }

}
