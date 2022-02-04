package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserOrderList {

    @Step("Получение списка заказов пользователя")
    public static Response userOrderList(String token) {
        Response response = given()
                .auth().oauth2(token)
                .and()
                .when()
                .get("/api/orders");

        return response;
    }

}
