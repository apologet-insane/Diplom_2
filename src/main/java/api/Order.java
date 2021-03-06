package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Order {

    @Step("Формирование заказа")
    public static Response order(String token, String body) {

        Response orderResponse = given()
                .header("Content-type", "application/json")
                .auth().oauth2(token)
                .and()
                .body(body)
                .when()
                .post("/api/orders");

        return orderResponse;
    }

}
